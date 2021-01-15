package com.resolute.jackson;


import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;

public class ObjectMappersTest {

  private final String JSON =
      "{\n" +
          "  \"id\" : 1,\n" +
          "  \"age\" : 34,\n" +
          "  \"name\" : \"Chris Hallendy\",\n" +
          "  \"startDate\" : \"2019-03-22\",\n" +
          "  \"addresses\" : [ \"14996 Robinwood Dr\" ]\n" +
          "}";

  @Test
  public void test() throws JsonProcessingException {
    ObjectMapper mapper = ObjectMappers.create();

    User user = User.builder()
        .withId(1)
        .withAge(34)
        .withName("Chris Hallendy")
        .withStartDate(LocalDate.of(2019, 3, 22))
        .withAddresses(ImmutableList.of("14996 Robinwood Dr"))
        .build();

    String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(user);
    assertThat(json).isEqualTo(JSON);

  }



}
