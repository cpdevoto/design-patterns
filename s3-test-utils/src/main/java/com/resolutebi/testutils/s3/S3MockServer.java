package com.resolutebi.testutils.s3;

import static java.util.Objects.requireNonNull;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import io.findify.s3mock.S3Mock;

public class S3MockServer implements TestRule {

  private enum Backend {
    IN_MEMORY, FILE
  }

  private final AtomicBoolean started = new AtomicBoolean();
  private final S3Mock s3Mock;
  private final Optional<Consumer<AmazonS3>> seeder;
  private final AmazonS3 client;


  public static Builder builder() {
    return new Builder();
  }

  private S3MockServer(Builder builder) {
    S3Mock.Builder mockBuilder = new S3Mock.Builder();
    mockBuilder.withPort(builder.port);
    switch (builder.backend) {
      case IN_MEMORY:
        mockBuilder.withInMemoryBackend();
        break;
      case FILE:
        mockBuilder.withFileBackend(builder.path);
    }
    this.s3Mock = mockBuilder.build();
    this.seeder = builder.seeder;
    EndpointConfiguration endpoint =
        new EndpointConfiguration("http://localhost:" + builder.port, "us-east-1");
    this.client = AmazonS3ClientBuilder
        .standard()
        .withPathStyleAccessEnabled(true)
        .withEndpointConfiguration(endpoint)
        .withCredentials(new AWSStaticCredentialsProvider(new AnonymousAWSCredentials()))
        .build();
  }

  public AmazonS3 getClient() {
    return client;
  }

  @Override
  public Statement apply(Statement base, Description description) {
    if (!started.compareAndSet(false, true)) {
      return base;
    }
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        s3Mock.start();
        try {
          seeder.ifPresent(s -> {
            s.accept(client);
          });
          base.evaluate();
        } finally {
          s3Mock.stop();;
        }
      }
    };
  }


  public static class Builder {
    private int port = 8001;
    private Backend backend = Backend.IN_MEMORY;
    private String path;
    private Optional<Consumer<AmazonS3>> seeder = Optional.empty();

    private Builder() {}

    public Builder withPort(int port) {
      this.port = port;
      return this;
    }

    public Builder withInMemoryBackend() {
      this.backend = Backend.IN_MEMORY;
      return this;
    }

    public Builder withFileBackend(String path) {
      requireNonNull(path, "path cannot be null");
      this.backend = Backend.FILE;
      this.path = path;
      return this;
    }

    public Builder withSeeder(Consumer<AmazonS3> seeder) {
      requireNonNull(seeder, "seeder cannot be null");
      this.seeder = Optional.of(seeder);
      return this;
    }

    public S3MockServer build() {
      return new S3MockServer(this);
    }

  }
}
