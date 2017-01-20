package org.devoware.json.model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class StringValueTest {

  @Test
  public void test_to_string() {
    assertThat(new StringValue("\"").toString(), equalTo("\"\\\"\"")); // escaped quote
    assertThat(new StringValue("\\").toString(), equalTo("\"\\\\\"")); // escaped back-slash
    assertThat(new StringValue("/").toString(), equalTo("\"/\""));   // escaped forward-slash
    assertThat(new StringValue("\t").toString(), equalTo("\"\\t\""));  // tab
    assertThat(new StringValue("\b").toString(), equalTo("\"\\b\""));  // backspace
    assertThat(new StringValue("\f").toString(), equalTo("\"\\f\""));  // formfeed
    assertThat(new StringValue("\n").toString(), equalTo("\"\\n\""));  // linefeed
    assertThat(new StringValue("\r").toString(), equalTo("\"\\r\""));  // carriage return
    assertThat(new StringValue("\u005B").toString(), equalTo("\"[\""));  // unicode escape
    assertThat(new StringValue("\u0019").toString(), equalTo("\"\\u0019\""));  // unicode escape
    
    StringValue s = new StringValue("\\/\t\b\f\n\r\u005Bhello\u005D");
    assertThat(s.toString(), equalTo("\"\\\\/\\t\\b\\f\\n\\r[hello]\""));
    
    assertThat(new StringValue("Kilroy\n\twas here!").toString(), equalTo("\"Kilroy\\n\\twas here!\""));

    
  }
  
}
