package com.techtrend.mentionconsumer.repository;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.techtrend.mentionconsumer.record.MentionRow;
import org.springframework.stereotype.Repository;


@Repository
public class MentionRepository {
    private final CqlSession session;
    private final PreparedStatement preparedStatement;

    public MentionRepository(CqlSession session) {
        this.session = session;
        this.preparedStatement = session.prepare(
            "INSERT INTO tech_mentions_by_source (source, time_bucket, mention_id, external_id, raw_text, url, crawled_at) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)"
        );
    }

    public void insert (MentionRow mentionRow) {
        session.execute(preparedStatement.bind(
            mentionRow.source(), mentionRow.timeBucket(), mentionRow.mentionId(), mentionRow.externalId(),
            mentionRow.rawText(), mentionRow.url(), mentionRow.crawledAt()
        ));
    }
}
