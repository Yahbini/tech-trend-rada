package com.techtrend.apigateway.config;

import com.techtrendradar.grpc.scoring.TrendScoringServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

@Configuration
public class ScoringGrpcClientConfig {

  // Channel target comes from spring.grpc.client.channel.scoring.*; Spring closes it on shutdown.
  @Bean
  TrendScoringServiceGrpc.TrendScoringServiceBlockingStub trendScoringStub(GrpcChannelFactory grpcChannelFactory) {
    return TrendScoringServiceGrpc.newBlockingStub(grpcChannelFactory.createChannel("scoring"));
  }
}
