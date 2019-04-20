# java-docker-db
**Owner(s):** Carlos Devoto

This library contains two very useful JUnit Rules:

 1. **DockerDatabase:** This rule uses Java code to automatically pull either the latest version of ``maddogtechnology-docker-develop.jfrog.io/postgres-schema`` Docker image, or the version referenced in the docker-dependencies.lock file if it exists. It then starts a Docker container based on this image before any of your tests are executed.  When your tests are completed, the container is stopped.  This rule is an improvement of the way we currently start the Resolute Database for testing, since it does not require the use of external Gradle commands (currently, before running your unit tests, you need to run ```./gradlew startDockerDepencies``` from the command line).
 1. **DatabaseSeeder:** This rule automatically seeds the Docker database with whatever SQL scripts you specify before executing any of your unit tests.  When your tests are completed, it executes whatever SQL scripts you specify in order to tear down the database. The functionality is identical to that found in the ```DatabaseSeeder``` class that resides within the ```resolute-docker-postgres-test-utils``` except that it has been refactored to work in conjunction with the ```DockerDatabase``` rule.
 
## Usage 

To use this library in a different Java project, add the following ``testCompile`` directive to the ``dependencies`` section of your ``build.gradle`` file:
```groovy
testCompile "com.resolute:java-docker-db:${rbiDepVersion}"
```
## Best Practices
 * Because of how long it takes to bring up and take down the Docker database, you won't want to do it before and after every test, or even before and after every test class.  Instead, you will define the ```DockerDatabase``` as a static ```ClassRule``` in your test suite class so that it gets executed once before ALL of your tests.  Individual test classes can then each include a static ```DockerDatabase``` data member, also annotated as the ```ClassRule``` snnotation, which gets initialized to the value of the ```DockerDatabase``` instance declared within the test suite class.  This allows you to run the test class independently, while also allowing it to run as part of the test suite. 
 * The ```DatabaseSeeder``` can be defined as a ```ClassRule```, as a ```Rule```, or both.  You probably want to load a bunch of bootstrap data using a ```@ClassRule``` and then complement it with some test data specific to your test class using a separate DatabaseSeeder ``ClassRule``` or ```Rule```.
 * If you define a ```DatabaseSeeder``` as a ```ClassRule```, you need to make sure that its initialization logic executes after the ```DockerDatabase``` rule and that its finalization logic executes after the ```DockerDatabase``` rule.  This can be accomplished by using a ```RuleChain``` as shown in the sample code below.

## Sample Code

```java
public class DockerDbTest {
  private static final DockerDatabase dockerDatabase = new DockerDatabase();

  private static final DatabaseSeeder seeder = DatabaseSeeder.builder()
      .withSeedScript(DockerDbTest.class, "base-data.sql")
      .withTearDownScript(DockerDbTest.class, "base-teardown.sql")
      .build();

  @ClassRule
  public static final RuleChain CHAIN = RuleChain
      .outerRule(dockerDatabase)
      .around(seeder);

  @Test
  public void test_dockerdb() throws SQLException {

    String sql = "SELECT name FROM customers WHERE id = 1";
    try (Connection conn = seeder.getDataSource().getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {
      assertThat(rs.next(), equalTo(true));
      assertThat(rs.getString("name"), equalTo("McLaren"));
    }
  }

}

```

## Issue Troubleshooting
* Verify that you are logged into [Artifactory](https://maddogtechnology.artifactoryonline.com/ "Artifactory") with docker by running the following (and using the same credentials as Artifactory)

```javascript
docker login maddogtechnology-docker-test.jfrog.io
docker login maddogtechnology-docker-develop.jfrog.io
docker login maddogtechnology-docker-ext.jfrog.io
````

* Verify that `~/.docker/config.json` is as follows:

```javascript
{
  "credsStore" : "osxkeychain",
  "HttpHeaders" : {
    "User-Agent" : "Docker-Client/18.09.0 (darwin)"
  },
  "stackOrchestrator" : "swarm",
  "credSstore" : "osxkeychain",
  "auths" : {
    "https://maddogtechnology-docker-ext.jfrog.io" : {

    },
    "maddogtechnology-docker-test.jfrog.io" : {

    },
    "https://maddogtechnology-docker-develop.jfrog.io" : {

    },
    "maddogtechnology-docker-ext.jfrog.io" : {

    },
    "https://maddogtechnology-docker-test.jfrog.io" : {

    },
    "maddogtechnology-docker-develop.jfrog.io" : {

    }
  }
}

```

* If you get the following error: `Caused by: java.io.IOException: Cannot run program "docker-credential-osxkeychain": error=2, No such file or directory`, then verify that `~/.bashrc` contains:

````javascript
export PATH="/usr/local/bin:$PATH"

alias eclipse='nohup /Applications/Eclipse\ JEE.app/Contents/MacOS/eclipse >/dev/null 2>&1 &'

````

NOTE: You may need to change `Eclipse\ J2EE.app` to match the actual directory that Eclipse is installed in. 

* That you are starting Eclipse via the alias above (so that if you did a `System.out.println(System.getenv("PATH"));` that it contains `/usr/local/bin`)   
