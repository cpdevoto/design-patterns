package com.resolute.okhttp3.simple;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import okhttp3.MediaType;

class HttpUtilsHelper {

  public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
  public static final MediaType TEXT = MediaType.parse("text/plain; charset=utf-8");
  public static final ObjectMapper MAPPER = new ObjectMapper();

  static {
    configure(MAPPER);
  }

  private static void configure(ObjectMapper m) {
    JavaTimeModule timeModule = new JavaTimeModule();
    timeModule.addDeserializer(LocalDate.class,
        new LocalDateDeserializer(DateTimeFormatter.ISO_LOCAL_DATE));

    timeModule.addDeserializer(LocalDateTime.class,
        new LocalDateTimeDeserializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

    timeModule.addSerializer(LocalDate.class,
        new LocalDateSerializer(DateTimeFormatter.ISO_LOCAL_DATE));

    timeModule.addSerializer(LocalDateTime.class,
        new LocalDateTimeSerializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

    m.registerModule(new Jdk8Module());
    m.registerModule(new GuavaModule());
    m.registerModule(timeModule);

    m.configOverride(java.util.Date.class)
        .setFormat(JsonFormat.Value.forPattern("yyyy-MM-dd"));

    m.configOverride(Optional.class)
        .setInclude(JsonInclude.Value.construct(JsonInclude.Include.NON_EMPTY, null));

    m.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    m.configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true);

  }

  private HttpUtilsHelper() {}

}
