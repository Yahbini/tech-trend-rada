package com.techtrend.mentionconsumer.mapper;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.techtrend.mentionconsumer.record.MentionByTechRow;
import com.techtrend.mentionconsumer.record.MentionRow;
import com.techtrendradar.avro.TechMention;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class MentionRowMapper {
  private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM").withZone(ZoneOffset.UTC);

  /**
   * param TechMention
   * return MentionRow
   */
  public MentionRow map(TechMention avroMention) {
    return new MentionRow(
          avroMention.getSource().toString(),
          LocalDate.ofInstant(avroMention.getCrawledAt(), ZoneOffset.UTC),
          Uuids.timeBased(),
          avroMention.getExternalId().toString(),
          avroMention.getRawText().toString(),
          avroMention.getUrl().toString(),
          avroMention.getCrawledAt()
    );
  }

  /**
   * One row per technology_id; empty technology_ids yields an empty list (by_source only).
   */
  public List<MentionByTechRow> mapByTech(TechMention avroMention) {
    var month = MONTH_FORMAT.format(avroMention.getCrawledAt());
    return avroMention.getTechnologyIds().stream()
                      .map(techId -> new MentionByTechRow(
                            techId,
                            month,
                            avroMention.getCrawledAt(),
                            avroMention.getSource().toString(),
                            avroMention.getExternalId().toString(),
                            avroMention.getUrl().toString(),
                            avroMention.getRawText().toString()
                      ))
                      .toList();
  }
}
