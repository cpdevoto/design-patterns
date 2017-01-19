package org.devoware.json.parser;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

import org.devoware.json.model.BooleanValue;
import org.devoware.json.model.JsonArray;
import org.devoware.json.model.JsonObject;
import org.devoware.json.model.NullValue;
import org.devoware.json.model.NumberValue;
import org.devoware.json.model.StringValue;
import org.devoware.json.model.Type;
import org.junit.Test;

public class ParserTest {


  @Test
  public void test_parse () {
    Parser parser = Parser.create();
    
    BooleanValue bool = parser.parse("true");
    assertNotNull(bool);
    assertThat(bool.getType(), equalTo(Type.BOOLEAN));
    assertThat(bool.value(), equalTo(true));
    
    assertThat(bool.toString(), equalTo("true"));
    
    bool = parser.parse("false");
    assertNotNull(bool);
    assertThat(bool.getType(), equalTo(Type.BOOLEAN));
    assertThat(bool.value(), equalTo(false));
    
    assertThat(bool.toString(), equalTo("false"));
    
    NullValue nul = parser.parse("null");
    assertNotNull(nul);
    assertThat(nul.getType(), equalTo(Type.NULL));
    assertNull(nul.value());

    assertThat(nul.toString(), equalTo("null"));
    
    NumberValue num = parser.parse("-1.25");
    assertNotNull(num);
    assertThat(num.getType(), equalTo(Type.NUMBER));
    assertThat(num.value(), equalTo(-1.25));
 
    assertThat(num.toString(), equalTo("-1.25"));
        
    StringValue string = parser.parse("\"metric\"");
    assertNotNull(string);
    assertThat(string.getType(), equalTo(Type.STRING));
    assertThat(string.value(), equalTo("metric"));
 
    assertThat(string.toString(), equalTo("\"metric\""));
    
    JsonArray array = parser.parse("[]");
    assertNotNull(array);
    assertThat(array.getType(), equalTo(Type.ARRAY));
    assertTrue(array.isEmpty());
    
    assertThat(array.toString(), equalTo("[]"));

    
    array = parser.parse("[true]");
    assertNotNull(array);
    assertThat(array.getType(), equalTo(Type.ARRAY));
    assertThat(array.size(), equalTo(1));
    assertNotNull(array.get(0));
    assertThat(array.get(0), equalTo(true));

    assertThat(array.toString(), equalTo("[true]"));
    
    array = parser.parse("[true, false]");
    assertNotNull(array);
    assertThat(array.getType(), equalTo(Type.ARRAY));
    assertThat(array.size(), equalTo(2));
    assertNotNull(array.get(0));
    assertThat(array.get(0), equalTo(true));
    assertNotNull(array.get(1));
    assertThat(array.get(1), equalTo(false));

    assertThat(array.toString(), equalTo("[true,false]"));
    
    JsonObject object = parser.parse("{}");
    assertNotNull(object);
    assertThat(object.getType(), equalTo(Type.OBJECT));
    assertTrue(object.isEmpty());

    assertThat(object.toString(), equalTo("{}"));
    
    object = parser.parse("{\"property1\":\"value1\"}");
    assertNotNull(object);
    assertThat(object.getType(), equalTo(Type.OBJECT));
    assertThat(object.size(), equalTo(1));
    assertTrue(object.keySet().contains("property1"));
    assertThat(object.get("property1"), instanceOf(String.class));
    assertThat(object.get("property1"), equalTo("value1"));
    
    assertThat(object.toString(), equalTo("{\"property1\":\"value1\"}"));

    object = parser.parse("{\"property1\":\"value1\",\"property2\":\"value2\"}");
    assertNotNull(object);
    assertThat(object.getType(), equalTo(Type.OBJECT));
    assertThat(object.size(), equalTo(2));
    assertTrue(object.keySet().contains("property1"));
    assertThat(object.get("property1"), instanceOf(String.class));
    assertThat(object.get("property1"), equalTo("value1"));
    assertTrue(object.keySet().contains("property2"));
    assertThat(object.get("property2"), instanceOf(String.class));
    assertThat(object.get("property2"), equalTo("value2"));
    
    assertThat(object.toString(), equalTo("{\"property1\":\"value1\",\"property2\":\"value2\"}"));
    
    object = parser.parse("{\n"
        + " \"metric\":\"kWh\",\n"
        + " \"tags\":{\n"
        + "   \"tag\":\"dummy\"\n"
        + "  },\n"
        + " \"dps\":[\n"
        + "   145.264,\n"
        + "   143.268,\n"
        + "   -143.268\n"
        + "  ]\n,"
        + " \"extraInfo\": null"
        + "}");
    assertNotNull(object);
    assertThat(object.getType(), equalTo(Type.OBJECT));
    assertThat(object.size(), equalTo(4));
    assertTrue(object.keySet().contains("metric"));
    assertThat(object.get("metric"), instanceOf(String.class));
    assertThat(object.get("metric"), equalTo("kWh"));
    assertTrue(object.keySet().contains("tags"));
    assertThat(object.get("tags"), instanceOf(JsonObject.class));
    JsonObject object2 = object.get("tags");
    assertThat(object2.size(), equalTo(1));
    assertTrue(object2.keySet().contains("tag"));
    assertThat(object2.get("tag"), instanceOf(String.class));
    assertThat(object2.get("tag"), equalTo("dummy"));
    assertTrue(object.keySet().contains("dps"));
    assertThat(object.get("dps"), instanceOf(JsonArray.class));
    array = object.get("dps");
    assertThat(array.size(), equalTo(3));
    assertNotNull(array.get(0));
    assertThat(array.get(0), equalTo(145.264));
    assertNotNull(array.get(1));
    assertThat(array.get(1), equalTo(143.268));
    assertNotNull(array.get(2));
    assertThat(array.get(2), equalTo(-143.268));
    assertTrue(object.keySet().contains("extraInfo"));
    assertThat(object.get("extraInfo"), equalTo(null));
    
    assertThat(object.<JsonObject>get("tags").get("tag"), equalTo("dummy"));
    assertThat(object.<JsonArray>get("dps").get(0), equalTo(145.264));
    
    assertThat(object.toString(), equalTo("{\"metric\":\"kWh\",\"tags\":{\"tag\":\"dummy\"},\"dps\":[145.264,143.268,-143.268],\"extraInfo\":null}"));

  }
  
  @Test
  public void test_parse_invalid_syntax () {
    Parser parser = Parser.create();

    try {
      parser.parse("{");
      fail("Expected a syntax exception");
    } catch (SyntaxException ex) {
      assertTrue("Invalid exception message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 1"));
    }
    
    try {
      parser.parse("{\"tags\":{}");
      fail("Expected a syntax exception");
    } catch (SyntaxException ex) {
      assertTrue("Invalid exception message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 10"));
    }
    try {
      parser.parse("{\"tags\":{}\"metric\":\"kWh\"");
      fail("Expected a syntax exception");
    } catch (SyntaxException ex) {
      assertTrue("Invalid exception message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 10"));
    }
    
    try {
      parser.parse("{\"tags\":{},\"metric\" \"kWh\"");
      fail("Expected a syntax exception");
    } catch (SyntaxException ex) {
      assertTrue("Invalid exception message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 20"));
    }

    try {
      parser.parse("[");
      fail("Expected a syntax exception");
    } catch (SyntaxException ex) {
      assertTrue("Invalid exception message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 1"));
    }

    try {
      parser.parse("[[]");
      fail("Expected a syntax exception");
    } catch (SyntaxException ex) {
      assertTrue("Invalid exception message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 3"));
    }
  
    try {
      parser.parse("[true {}]");
      fail("Expected a syntax exception");
    } catch (SyntaxException ex) {
      assertTrue("Invalid exception message: " + ex.getMessage(), ex.getMessage().contains("line 1, character 6"));
    }
  }
  
}
