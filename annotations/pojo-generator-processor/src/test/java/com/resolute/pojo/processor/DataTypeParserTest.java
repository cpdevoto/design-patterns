package com.resolute.pojo.processor;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import com.resolute.pojo.processor.types.DataTypeParser;
import com.resolute.pojo.processor.types.ImportExclusion;
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
    DataType dataType = DataTypeParser.parse(input, null);
    assertThat(dataType.getSimpleName()).isEqualTo(expectedOutput);
  }

  @ParameterizedTest
  @MethodSource
  public void test_get_imports(String input, Optional<ImportExclusion> optImportExclusion,
      String[] expectedOutputs) {
    ImportExclusion importExclusion = optImportExclusion.orElse(null);
    DataType dataType = DataTypeParser.parse(input, importExclusion);
    assertThat(dataType.getImports()).containsOnly(expectedOutputs);
  }

  static Stream<Arguments> test_get_imports() {
    return Stream.of(
        Arguments.of("int", Optional.empty(), new String[] {}),
        Arguments.of("int[]", Optional.empty(), new String[] {}),
        Arguments.of("java.lang.String", Optional.empty(), new String[] {}),
        Arguments.of("java.util.Map<java.lang.Integer,java.util.List<java.lang.String>>",
            Optional.empty(), new String[] {"java.util.Map", "java.util.List"}),
        Arguments.of("java.util.List<com.resolute.user.model.Department>",
            Optional.of(new ImportExclusion("com.resolute.user.model", "UserModel")),
            new String[] {"java.util.List"}),
        Arguments.of("java.util.List<com.resolute.user.model.UserModel.Department>",
            Optional.of(new ImportExclusion("com.resolute.user.model", "UserModel")),
            new String[] {"java.util.List"}));
  }



}
