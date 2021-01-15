package com.resolute.pojo.processor;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import com.resolute.pojo.processor.types.DataTypeParser;
import com.resolute.utils.simple.pojo_generator.DataType;

public class DataTypeParserTest {

  @ParameterizedTest
  @CsvSource({
      "'int', 'int'",
      "'int []', 'int[]'",
      "'java.lang.String', 'String'",
      "'java.util.Map<java.lang.Integer,java.util.List<java.lang.String>>', 'Map<Integer,List<String>>'",

  })
  public void test_get_simple_name(String input, String expectedOutput) {
    DataType dataType = DataTypeParser.parse(input);
    assertThat(dataType.getSimpleName()).isEqualTo(expectedOutput);
  }

  @ParameterizedTest
  @MethodSource
  public void test_get_imports(String input, String[] expectedOutputs) {
    DataType dataType = DataTypeParser.parse(input);
    assertThat(dataType.getImports()).containsOnly(expectedOutputs);
  }

  static Stream<Arguments> test_get_imports() {
    return Stream.of(
        Arguments.of("int", new String[] {}),
        Arguments.of("int[]", new String[] {}),
        Arguments.of("java.lang.String", new String[] {}),
        Arguments.of("java.util.Map<java.lang.Integer,java.util.List<java.lang.String>>",
            new String[] {"java.util.Map", "java.util.List"}));
  }



}
