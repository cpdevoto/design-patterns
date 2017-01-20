package org.devoware.json;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.devoware.json.model.JsonArray;
import org.devoware.json.model.JsonObject;
import org.devoware.json.parser.JsonParser;
import org.junit.Test;

public class IntegrationTest {
  
  private String json = 
      "[\n" +
      " {\n" +
      "   \"metric\": \"8ABCD./Drivers/NiagaraNetwork/ColsSteel/points/ElecMeters/Slitter72/kWH_Int5\",\n" +
      "   \"values\": {\n" +            
      "               \"1470758400\": 10.0,\n" +
      "               \"1470762000\": 20.0,\n" +
      "               \"1470765600\": 30.0,\n" +
      "               \"1470769200\": 40.0,\n" +
      "               \"1470772800\": 50.0,\n" +
      "               \"1470776400\": 60.0\n" +
      "   }\n" +
      " },\n" +
      " {\n" +
      "   \"metric\": \"8ABCD./Drivers/NiagaraNetwork/ColsSteel/points/ElecMeters/Slitter72/coilsOut\",\n" +
      "   \"values\": {\n" +            
      "               \"1470758400\": 5.0,\n" +
      "               \"1470765600\": 0.0,\n" +
      "               \"1470772800\": 20.0\n" +
      "   }\n" +
      " }\n" +
      "]";

  String expected =
      "[" +
       "{" +
         "\"metric\":\"8ABCD./Drivers/NiagaraNetwork/ColsSteel/points/ElecMeters/Slitter72/kWH_Int5\"," +
         "\"values\":{" +            
                     "\"1470758400\":10.0," +
                     "\"1470762000\":20.0," +
                     "\"1470765600\":30.0," +
                     "\"1470769200\":40.0," +
                     "\"1470772800\":50.0," +
                     "\"1470776400\":60.0" +
         "}" +
       "}," +
       "{" +
         "\"metric\":\"8ABCD./Drivers/NiagaraNetwork/ColsSteel/points/ElecMeters/Slitter72/coilsOut\"," +
         "\"values\":{" +            
                     "\"1470758400\":5.0," +
                     "\"1470765600\":0.0," +
                     "\"1470772800\":20.0" +
         "}" +
       "}" +
      "]";
  

  @Test
  public void test_string_to_object () {
    
    JsonArray metrics = JsonParser.parse(json);  // if you don't know the type being returned, use JsonNode instead of JsonArray here, then call getType() on the JsonNode object.
    assertNotNull(metrics);
    assertThat(metrics.size(), equalTo(2));

    assertThat(metrics.<JsonObject>get(0).get("metric"), equalTo("8ABCD./Drivers/NiagaraNetwork/ColsSteel/points/ElecMeters/Slitter72/kWH_Int5"));
    assertThat(metrics.<JsonObject>get(0).<JsonObject>get("values").size(), equalTo(6));
    assertThat(metrics.<JsonObject>get(0).<JsonObject>get("values").get("1470758400"), equalTo(10.0));
    assertThat(metrics.<JsonObject>get(0).<JsonObject>get("values").get("1470762000"), equalTo(20.0));
    assertThat(metrics.<JsonObject>get(0).<JsonObject>get("values").get("1470765600"), equalTo(30.0));
    assertThat(metrics.<JsonObject>get(0).<JsonObject>get("values").get("1470769200"), equalTo(40.0));
    assertThat(metrics.<JsonObject>get(0).<JsonObject>get("values").get("1470772800"), equalTo(50.0));
    assertThat(metrics.<JsonObject>get(0).<JsonObject>get("values").get("1470776400"), equalTo(60.0));

    assertThat(metrics.<JsonObject>get(1).get("metric"), equalTo("8ABCD./Drivers/NiagaraNetwork/ColsSteel/points/ElecMeters/Slitter72/coilsOut"));
    assertThat(metrics.<JsonObject>get(1).<JsonObject>get("values").size(), equalTo(3));
    assertThat(metrics.<JsonObject>get(1).<JsonObject>get("values").get("1470758400"), equalTo(5.0));
    assertThat(metrics.<JsonObject>get(1).<JsonObject>get("values").get("1470765600"), equalTo(0.0));
    assertThat(metrics.<JsonObject>get(1).<JsonObject>get("values").get("1470772800"), equalTo(20.0));
    
    
    assertThat(metrics.toString(), equalTo(expected));
  
  }
  
  @Test
  public void test_object_to_string () {
    JsonArray metrics = JsonArray.builder()
        .withElement(JsonObject.builder()
            .withProperty("metric", "8ABCD./Drivers/NiagaraNetwork/ColsSteel/points/ElecMeters/Slitter72/kWH_Int5")
            .withProperty("values", JsonObject.builder()
                .withProperty("1470758400", 10.0)
                .withProperty("1470762000", 20.0)
                .withProperty("1470765600", 30.0)
                .withProperty("1470769200", 40.0)
                .withProperty("1470772800", 50.0)
                .withProperty("1470776400", 60.0)
                .build())
            .build())
        .withElement(JsonObject.builder()
            .withProperty("metric", "8ABCD./Drivers/NiagaraNetwork/ColsSteel/points/ElecMeters/Slitter72/coilsOut")
            .withProperty("values", JsonObject.builder()
                .withProperty("1470758400", 5.0)
                .withProperty("1470765600", 0.0)
                .withProperty("1470772800", 20.0)
                .build())
            .build())
        .build();

    assertThat(metrics.toString(), equalTo(expected));
  }

}
