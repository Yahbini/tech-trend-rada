# =============================================================================
# ttr.sh — lệnh tắt cho docker stack tech-trend-radar (Kafka, Schema Registry,
# MySQL, Cassandra, ES, Kafka Connect, stream-processing).
#
# Cài 1 lần: thêm dòng sau vào ~/.bashrc (WSL) rồi chạy `source ~/.bashrc`
#   source /mnt/d/workspace/projects/web/tech-trend/tech-trend-radar/infra/docker/ttr.sh
# Quên tên lệnh: gõ `dckhelp`. Gõ vài chữ đầu + Tab để shell gợi ý.
#
# Không chứa password: lệnh MySQL đọc user/pass từ infra/docker/.env lúc chạy.
# File phải dùng xuống dòng LF (không phải CRLF), nếu không bash báo lỗi $'\r'.
# =============================================================================

TTR_DOCKER_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TTR_ROOT="$(cd "$TTR_DOCKER_DIR/../.." && pwd)"
TTR_ENV="$TTR_DOCKER_DIR/.env"
TTR_SPS_APP_ID="tech-trend-stream-processing"
TTR_SPS_STATE_DIR="${KAFKA_STREAMS_STATE_DIR:-/var/lib/techtrend/kafka-streams}"
TTR_CONNECTOR="mysql-technology-cdc"

# ---------------------------------------------------------------- helper nội bộ

# -it khi gõ tay ở terminal; chỉ -i khi bị pipe (| grep) hoặc gọi từ script.
_ttr_tty() { if [ -t 0 ] && [ -t 1 ]; then echo "-it"; else echo "-i"; fi; }

# Đọc 1 biến trong .env (bỏ \r và dấu nháy) mà không nạp cả file vào shell.
_ttr_env() {
  grep -E "^$1=" "$TTR_ENV" 2>/dev/null | tail -1 | cut -d= -f2- | tr -d '\r' | sed -E "s/^['\"]|['\"]$//g"
}

# Hỏi lại trước lệnh xoá/reset; mặc định là No.
_ttr_confirm() { local ans; read -r -p "$1 [y/N] " ans; [[ "$ans" =~ ^[Yy]$ ]]; }

# Key deserializer theo topic — sai là lỗi "Unknown magic byte!".
# Rỗng = key Avro (vd tech-updates), để tool tự decode.
_ttr_key_deser() {
  case "$1" in
    tech-mentions)
      echo "org.apache.kafka.common.serialization.StringDeserializer" ;;
    new-tech-alerts|*-repartition|*-changelog)
      echo "org.apache.kafka.common.serialization.LongDeserializer" ;;
    *)
      echo "" ;;
  esac
}

# ---------------------------------------------------------------- Kafka (ttr-kafka)

# Lệnh kafka-topics gốc, tự thêm bootstrap-server. Vd: ktopic --list
ktopic() { docker exec $(_ttr_tty) ttr-kafka kafka-topics --bootstrap-server kafka:29092 "$@"; }

# Liệt kê topic.
ktopics() { ktopic --list; }

# Chi tiết topic (partition, leader, config). Vd: kdesc new-tech-alerts
kdesc() { ktopic --describe --topic "${1:?topic?}"; }

# Tạo topic theo chuẩn local: RF 1, mặc định 1 partition. Vd: kcreate my-topic 3
kcreate() {
  ktopic --create --if-not-exists --topic "${1:?topic?}" --partitions "${2:-1}" --replication-factor 1
}

# Xoá topic (mất toàn bộ message). Vd: kdelete my-topic
kdelete() {
  _ttr_confirm "Xoá topic '${1:?topic?}'?" && ktopic --delete --topic "$1"
}

# Offset cuối mỗi partition = số message đã ghi. Vd: koffsets tech-updates
koffsets() {
  docker exec ttr-kafka kafka-get-offsets --bootstrap-server kafka:29092 --topic "${1:?topic?}"
}

# Đọc message thô (Avro hiện ký tự lạ). Ctrl+C để dừng. Vd: kraw tech-mentions
kraw() {
  local t="${1:?topic?}"; shift
  docker exec $(_ttr_tty) ttr-kafka kafka-console-consumer --bootstrap-server kafka:29092 \
    --topic "$t" --from-beginning --property print.key=true "$@"
}

# Đọc Avro dạng JSON, tự chọn key deserializer. Tham số thêm truyền thẳng cho tool.
# Vd: kavro new-tech-alerts --from-beginning
#     kavro tech-mentions --from-beginning --max-messages 5 --property print.timestamp=true
kavro() {
  local t="${1:?topic?}"; shift
  local deser
  deser="$(_ttr_key_deser "$t")"
  local args=(--bootstrap-server kafka:29092 --topic "$t"
              --property schema.registry.url=http://schema-registry:8081
              --property print.key=true)
  [ -n "$deser" ] && args+=(--property "key.deserializer=$deser")
  # log4j mặc định của image in INFO ra stdout, lẫn vào message — tắt bằng config rỗng.
  docker exec $(_ttr_tty) -e SCHEMA_REGISTRY_LOG4J_OPTS=-Dlog4j.configuration=file:/dev/null \
    ttr-schema-registry kafka-avro-console-consumer "${args[@]}" "$@"
}

# Đọc đúng 1 message tại offset (partition 0). Vd: kat tech-updates 15
kat() {
  kavro "${1:?topic?}" --partition 0 --offset "${2:?offset?}" --max-messages 1 2>/dev/null
}

# Liệt kê consumer group.
kgroups() { docker exec ttr-kafka kafka-consumer-groups --bootstrap-server kafka:29092 --list; }

# Offset/LAG của 1 group; không truyền tên = liệt kê. Vd: kgroup mention-consumer-group
kgroup() {
  [ -z "$1" ] && { kgroups; return; }
  docker exec ttr-kafka kafka-consumer-groups --bootstrap-server kafka:29092 --describe --group "$1"
}

# ---------------------------------------------------------------- Schema Registry

# Danh sách subject (mỗi topic Avro có <topic>-value, key Avro thì thêm <topic>-key).
srsubjects() {
  docker exec ttr-schema-registry curl -s http://localhost:8081/subjects; echo
}

# Schema của subject, mặc định bản mới nhất. Vd: srschema new-tech-alerts-value 1
srschema() {
  docker exec ttr-schema-registry curl -s \
    "http://localhost:8081/subjects/${1:?subject?}/versions/${2:-latest}"; echo
}

# Xoá hẳn subject (soft rồi hard delete). Dùng khi lỡ đăng ký subject rác.
srdelete() {
  local s="${1:?subject?}"
  _ttr_confirm "Xoá vĩnh viễn subject '$s'?" || return 1
  docker exec ttr-schema-registry curl -s -X DELETE "http://localhost:8081/subjects/$s"; echo
  docker exec ttr-schema-registry curl -s -X DELETE "http://localhost:8081/subjects/$s?permanent=true"; echo
}

# ---------------------------------------------------------------- MySQL (ttr-mysql)
# Password truyền qua MYSQL_PWD: không lộ trên dòng lệnh, không in warning.

# Vào shell mysql bằng user app.
mycli() {
  docker exec -it -e MYSQL_PWD="$(_ttr_env MYSQL_PASSWORD)" ttr-mysql \
    mysql -u"$(_ttr_env MYSQL_USER)" "$(_ttr_env MYSQL_DATABASE)"
}

# Chạy 1 câu SQL rồi thoát (in dạng bảng).
# Vd: myq "SELECT id, canonical_name FROM technology LIMIT 5"
myq() {
  docker exec -i -e MYSQL_PWD="$(_ttr_env MYSQL_PASSWORD)" ttr-mysql \
    mysql -t -u"$(_ttr_env MYSQL_USER)" "$(_ttr_env MYSQL_DATABASE)" -e "${1:?sql?}"
}

# Vào shell mysql bằng root (tạo user, GRANT, xem binlog...).
myroot() {
  docker exec -it -e MYSQL_PWD="$(_ttr_env MYSQL_ROOT_PASSWORD)" ttr-mysql mysql -uroot
}

# Kiểm tra binlog cho Debezium: cần log_bin=ON, ROW, FULL, server_id=1.
mybinlog() {
  docker exec -i -e MYSQL_PWD="$(_ttr_env MYSQL_ROOT_PASSWORD)" ttr-mysql mysql -t -uroot -e \
    "SHOW GLOBAL VARIABLES WHERE Variable_name IN ('log_bin','binlog_format','binlog_row_image','server_id');"
}

# ---------------------------------------------------------------- Cassandra (ttr-cassandra)

# Vào cqlsh.
cql() { docker exec $(_ttr_tty) ttr-cassandra cqlsh "$@"; }

# Chạy 1 câu CQL.
# Vd: cqlq "SELECT * FROM tech_trend_radar.tech_mentions_by_source WHERE source='devto' LIMIT 5"
cqlq() { docker exec -i ttr-cassandra cqlsh -e "${1:?cql?}"; }

# Trạng thái node (UN = Up/Normal).
cqlstatus() { docker exec ttr-cassandra nodetool status; }

# ---------------------------------------------------------------- Elasticsearch

eshealth() { docker exec ttr-elasticsearch curl -s "localhost:9200/_cat/health?v"; }
esindices() { docker exec ttr-elasticsearch curl -s "localhost:9200/_cat/indices?v"; }

# ---------------------------------------------------------------- n8n, Zookeeper

# Shell trong n8n (image không có bash).
n8nsh() { docker exec -it ttr-n8n sh; }

# Broker đang đăng ký với Zookeeper (kỳ vọng [1]).
zkbrokers() {
  docker exec ttr-zookeeper zookeeper-shell localhost:2181 ls /brokers/ids 2>/dev/null | tail -1
}

# ---------------------------------------------------------------- Docker compose

# docker compose với đúng file + .env, gọi từ thư mục nào cũng được.
# Vd: dc ps · dc up -d connect · dc up -d --force-recreate mysql (dữ liệu giữ trong volume)
dc() {
  docker compose --project-directory "$TTR_DOCKER_DIR" -f "$TTR_DOCKER_DIR/docker-compose.yml" \
    --env-file "$TTR_ENV" "$@"
}

# Log container: không có pattern = theo dõi liên tục; có pattern = lọc 20 dòng cuối.
# Vd: dlogs ttr-connect · dlogs ttr-connect "snapshot|error|exception"
dlogs() {
  local c="${1:?container?}"
  if [ -n "$2" ]; then
    docker logs "$c" 2>&1 | grep -iE "$2" | tail -20
  else
    docker logs -f --tail 100 "$c"
  fi
}

# ---------------------------------------------------------------- Kafka Connect / Debezium (ttr-connect)
# Gọi REST API ngay trong container (connect:8083), không phụ thuộc port map ra host.

_ttr_kc() { docker exec -i ttr-connect curl -s "$@"; echo; }

kcplugins() { _ttr_kc http://localhost:8083/connector-plugins; }
kclist() { _ttr_kc http://localhost:8083/connectors; }

# Trạng thái connector + task; task FAILED thì đọc field "trace".
kcstatus() { _ttr_kc "http://localhost:8083/connectors/${1:-$TTR_CONNECTOR}/status"; }

# Deploy connector từ infra/connect/mysql-technology-cdc.json.
kcdeploy() {
  _ttr_kc -X POST -H "Content-Type: application/json" --data @- http://localhost:8083/connectors \
    < "$TTR_ROOT/infra/connect/$TTR_CONNECTOR.json"
}

# Gỡ connector để snapshot chạy lại: stop → xoá offset → xoá connector.
# Sau đó (nếu cần làm sạch): kdelete tech-updates, kdelete ttr.schema-history,
# srdelete tech-updates-value, rồi kcdeploy.
kcredeploy() {
  _ttr_confirm "Gỡ connector '$TTR_CONNECTOR' (stop + xoá offset + xoá)?" || return 1
  _ttr_kc -X PUT "http://localhost:8083/connectors/$TTR_CONNECTOR/stop"
  _ttr_kc -X DELETE "http://localhost:8083/connectors/$TTR_CONNECTOR/offsets"
  _ttr_kc -X DELETE "http://localhost:8083/connectors/$TTR_CONNECTOR"
}

# ---------------------------------------------------------------- stream-processing-service

# Offset/LAG của app Kafka Streams.
spslag() { kgroup "$TTR_SPS_APP_ID"; }

# Reset khi đổi cấu trúc topology (thêm/bớt node trước tên tự sinh, đổi key, đổi store).
# App phải tắt trước; tool không xoá state local nên xoá state dir sau đó.
spsreset() {
  if pgrep -f StreamProcessingServiceApplication >/dev/null; then
    echo "stream-processing-service đang chạy — tắt app trước."
    return 1
  fi
  _ttr_confirm "Reset '$TTR_SPS_APP_ID' + xoá $TTR_SPS_STATE_DIR/$TTR_SPS_APP_ID?" || return 1
  docker exec ttr-kafka kafka-streams-application-reset --bootstrap-server kafka:29092 \
    --application-id "$TTR_SPS_APP_ID" --input-topics tech-updates,tech-mentions \
    && rm -rf "${TTR_SPS_STATE_DIR:?}/$TTR_SPS_APP_ID"
}

# ---------------------------------------------------------------- Test webhook (crawl-ingest-service)

# Gửi 1 payload hợp lệ, crawled_at = giờ hiện tại (UTC). Cùng external_id lần 2 = re-crawl (không đếm).
# Vd: whpost test-1 "Rust 2.0 released" · whpost test-2 "Ansible tips" reddit
whpost() {
  local id="${1:?external_id?}" title="${2:?title?}" src="${3:-hackerNews}"
  curl -s -w " (HTTP %{http_code})\n" -X POST http://localhost:18080/webhook/crawl \
    -H "Content-Type: application/json" \
    -d "{\"source\":\"$src\",\"external_id\":\"$id\",\"title\":\"$title\",\"raw_text\":\"$title\",\"url\":\"http://test.local/$id\",\"crawled_at\":\"$(date -u +%Y-%m-%dT%H:%M:%S.%3NZ)\"}"
}

# ---------------------------------------------------------------- help

dckhelp() {
  cat <<'EOF'
Kafka        ktopics · kdesc <t> · kcreate <t> [p] · kdelete <t>! · koffsets <t>
             kraw <t> · kavro <t> [args] · kat <t> <offset> · kgroups · kgroup [g] · ktopic <args>
Schema Reg   srsubjects · srschema <subject> [ver] · srdelete <subject>!
MySQL        mycli · myq "<sql>" · myroot · mybinlog
Cassandra    cql · cqlq "<cql>" · cqlstatus
ES           eshealth · esindices
Khác         n8nsh · zkbrokers · dc <compose args> · dlogs <container> [pattern]
Connect      kcplugins · kclist · kcstatus [name] · kcdeploy · kcredeploy!
Streams      spslag · spsreset!
Test         whpost <external_id> "<title>" [source]
(! = hỏi lại trước khi chạy)
EOF
}
