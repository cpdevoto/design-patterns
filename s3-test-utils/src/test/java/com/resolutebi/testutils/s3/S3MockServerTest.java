package com.resolutebi.testutils.s3;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.ClassRule;
import org.junit.Test;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;

public class S3MockServerTest {
  @ClassRule
  public static final S3MockServer S3 = S3MockServer.builder()
      .withPort(8001)
      .withInMemoryBackend()
      .withSeeder(s3 -> {
        s3.createBucket("testbucket");
        s3.putObject("testbucket", "file/name", "contents");
      })
      .build();


  @Test
  public void test_docker_zookeeper_connection() throws IOException, InterruptedException {
    AmazonS3 client = S3.getClient();
    S3Object obj = client.getObject("testbucket", "file/name");
    String content = client.getObjectAsString("testbucket", "file/name");
    assertThat(obj, notNullValue());
    assertThat(obj.getBucketName(), equalTo("testbucket"));
    assertThat(obj.getKey(), equalTo("file/name"));
    assertThat(content, equalTo("contents"));

  }



}
