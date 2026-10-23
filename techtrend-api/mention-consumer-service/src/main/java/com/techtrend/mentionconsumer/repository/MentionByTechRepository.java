package com.techtrend.mentionconsumer.repository;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.techtrend.mentionconsumer.record.MentionByTechRow;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class MentionByTechRepository {
  private final CqlSession session;
  private final PreparedStatement preparedStatement;

  public MentionByTechRepository(CqlSession session) {
    this.session = session;
    this.preparedStatement = session.prepare(
          "INSERT INTO tech_mentions_by_tech(technology_id, month, crawled_at, source, external_id, url, raw_text) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)"
    );
  }

  /**
   * One execute per row, no batch — rows live in different partitions and the natural key makes a replay overwrite them.
   */
  public void insertAll(List<MentionByTechRow> rows) {
    for (MentionByTechRow row : rows) {
      session.execute(preparedStatement.bind(
            row.technologyId(), row.month(), row.crawledAt(), row.source(),
            row.externalId(), row.url(), row.rawText()
      ));
    }
  }
}
