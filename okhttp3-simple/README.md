# jdbc-simple

**Owner(s):** Carlos Devoto

A lightweight library create to eliminate a lot of the boilerplate code involved with writing HTTP client logic for invoking REST APIs. It is a thin
wrapper providing syntactic sugar over the okhttp3 library. For more details, see the _docs/HttpUtils is Dead.pptx_ Powerpoint presentation file. 

**NOTE:** The classes contained in this library mirror the ones contained in the app-service-common project. I decided to copy these classes into their own library so that they could be used without pulling in a bunch of superfluous transitive dependencies.

## Usage

```java
    // Create a SimpleHttpRequestFactory that is threadsafe and can be reused globally
    SimpleHttpRequestFactory requestFactory =
        SimpleHttpRequestFactory.builder("http://localhost:8080")
            .withConnectTimeout(30, TimeUnit.SECONDS)
            .withReadTimeout(30, TimeUnit.SECONDS)
            .build();
            
    // Execute requests of all types as shown in the following examples:
    
    // GET request that automatically deserializes JSON to a 
    // POJO object of whatever type you choose (in this case, an InviteStatus object)
    InviteStatus status = requestFactory.newRequest()
        .withUrl("/user/invite")
        .execute(InviteStatus.class);
            
    // POST request that automatically serializes a POJO object of whatever type you choose (in this case, a User object)
    // to JSON.
    User user = new User("Carlos", "Devoto");
    requestFactory.newRequest()
        .withUrl("/users")
        .addHeader("Authorization", "Bearer abcdefg0xy")
        .post(user)
        .execute();
    
    // If a request returns a bad status an unchecked BadResponseException is thrown which contains the response body as well as the code
    try {
      requestFactory.newRequest()
        .withUrl("/users")
        .post(user)
        .execute();
    } catch (BadResponseException e) {
      assertThat(e.getResponse().code(), equalTo(401));
      assertThat(e.getResponse().body(), equalTo("You are not authorized"));
    }

```
