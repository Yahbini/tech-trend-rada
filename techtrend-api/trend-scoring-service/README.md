# trend-scoring-service

gRPC service tính điểm trend 0–1 cho từng công nghệ, khung 7 ngày, chỉ đọc MySQL.

## Công thức

score = w1·norm(velocity) + w2·norm(source_count) + w3·category.weight

| Thành phần | Nguồn |
|---|---|
| velocity | `technology.mention_velocity` = (last_7d − prev_7d) / max(prev_7d, 1) |
| source_count | `COUNT(DISTINCT source_id)` của mention trong 7 ngày gần nhất |
| category.weight | `category.weight`, không có category → 0.50 |

norm(x) = (x − min) / (max − min), min/max lấy trên mọi công nghệ; max = min → 0.

## Trọng số (`app.scoring.weights`)

Mặc định 0.5 / 0.3 / 0.2, tổng = 1 để score nằm trong [0, 1].
- velocity cao nhất: "trending" là độ tăng.
- source_count xác nhận trend có nhiều nguồn, không phải do 1 nguồn nói.
- category.weight chỉ đẩy nhẹ.

Có dữ liệu thật mà thấy lệch thì sửa `application.yml`, ghi lý do vào đây (decisions.md Mục 29).
