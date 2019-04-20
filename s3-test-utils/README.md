# s3-test-utils
**Owner(s):** Carlos Devoto

This library contains a useful JUnit Rule that can be used to set up a mock S3 server and seed it with test data


 
## Usage 

To use this library in a different Java project, add the following ``testCompile`` directive to the ``dependencies`` section of your ``build.gradle`` file:
```groovy
testCompile "com.resolute:s3-test-utils:${rbiDepVersion}"
```

## Sample Code

```java
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
```

