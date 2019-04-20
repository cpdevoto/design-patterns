# mock-flume

**Owner(s):** Carlos Devoto

A dockerized version of Apache Flume that can be used for integration testing. The Flume server is configured with a custom sink that JSON serializes each received event and writes it as a file named event-<timestamp>.json to the /apt/apache-flume/captures directory.  This directory can be mapped to an external volume when the docker container is launched so that you can monitor the events as they are received.  An example of the JSON format used to serialize events is shown below (note that the value of the ``body`` attribute is a Base64 encoded byte array):  

```javascript
{
  "headers": {
    "header1": "value1",
    "header2": "value2"
  },
  "body": "aGVsbG8sIHdvcmxkIQ=="
}
```