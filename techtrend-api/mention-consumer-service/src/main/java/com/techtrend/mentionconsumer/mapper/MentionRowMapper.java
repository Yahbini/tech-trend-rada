package com.techtrend.mentionconsumer.mapper;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.techtrendradar.avro.TechMention;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneOffset;

@Component
public class MentionRowMapper {
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
}
