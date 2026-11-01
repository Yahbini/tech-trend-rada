package com.techtrend.trendscoring.grpc;

import com.techtrend.trendscoring.model.TechnologyScore;
import com.techtrend.trendscoring.service.ScoringQueryService;
import com.techtrendradar.grpc.scoring.ScoreResponse;
import com.techtrendradar.grpc.scoring.ScoresResponse;
import com.techtrendradar.grpc.scoring.TechRequest;
import com.techtrendradar.grpc.scoring.TechsRequest;
import com.techtrendradar.grpc.scoring.TopScoresRequest;
import com.techtrendradar.grpc.scoring.TrendScoringServiceGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.grpc.server.service.GrpcService;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@GrpcService
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TrendScoringGrpcService extends TrendScoringServiceGrpc.TrendScoringServiceImplBase {
  ScoringQueryService scoringQueryService;

  private static final int DEFAULT_LIMIT = 10;
  private static final int MAX_LIMIT = 100;

  // Proto setters throw NPE on null, so a missing category is sent as "".
  private static ScoreResponse toResponse(TechnologyScore s) {
    return ScoreResponse.newBuilder()
                        .setTechnologyId(s.getTechnologyId())
                        .setCanonicalName(s.getCanonicalName())
                        .setCategory(s.getCategoryCode() == null ? "" : s.getCategoryCode())
                        .setScore(s.getScore())
                        .setMentionVelocity(s.getMentionVelocity())
                        .setSourceCount(s.getSourceCount())
                        .setCategoryWeight(s.getCategoryWeight())
                        .build();
  }

  @Override
  public void getScore(TechRequest request, StreamObserver<ScoreResponse> responseObserver) {
    long id = request.getTechnologyId();
    scoringQueryService.findAllScores().stream()
                       .filter(s -> s.getTechnologyId() == id)
                       .findFirst()
                       .ifPresentOrElse(s -> {
                         responseObserver.onNext(toResponse(s));
                         responseObserver.onCompleted();
                       }, () -> responseObserver
                             .onError(Status.NOT_FOUND.withDescription("technology_id " + id + " not found")
                                                      .asRuntimeException()));
  }

  @Override
  public void getScores(TechsRequest request, StreamObserver<ScoresResponse> responseObserver) {
    Set<Long> ids = new HashSet<>(request.getTechnologyIdsList());
    List<ScoreResponse> scores = scoringQueryService.findAllScores().stream()
                                                    .filter(s -> ids.contains(s.getTechnologyId()))
                                                    .map(TrendScoringGrpcService::toResponse)
                                                    .toList();
    responseObserver.onNext(ScoresResponse.newBuilder().addAllScores(scores).build());
    responseObserver.onCompleted();
  }

  @Override
  public void getTopScores(TopScoresRequest request, StreamObserver<ScoresResponse> responseObserver) {
    String category = request.getCategory();
    int limit = request.getLimit() <= 0 ? DEFAULT_LIMIT : Math.min(request.getLimit(), MAX_LIMIT);
    List<ScoreResponse> scores = scoringQueryService.findAllScores().stream()
          .filter(s -> category.isEmpty() || category.equalsIgnoreCase(s.getCategoryCode()))
          .sorted(Comparator.comparingDouble(TechnologyScore::getScore).reversed())
          .limit(limit)
          .map(TrendScoringGrpcService::toResponse)
          .toList();
    responseObserver.onNext(ScoresResponse.newBuilder().addAllScores(scores).build());
    responseObserver.onCompleted();
  }
}
