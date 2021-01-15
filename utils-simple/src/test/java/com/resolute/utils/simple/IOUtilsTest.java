package com.resolute.utils.simple;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import com.resolute.resources.Resource;

public class IOUtilsTest {

  @Test
  public void test_copy() throws IOException {
    String inputText = "Resolute Building Intelligence xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
    try (ByteArrayInputStream input = new ByteArrayInputStream(inputText.getBytes(UTF_8));
        ByteArrayOutputStream output = new ByteArrayOutputStream()) {
      IOUtils.copy(input, output);
      byte[] bytes = output.toByteArray();
      String outputText = new String(bytes, UTF_8);
      assertThat(outputText).isEqualTo(inputText);
    }
  }

  @Test
  public void test_to_byte_array() throws IOException {
    String inputText = "Resolute Building Intelligence xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
    try (ByteArrayInputStream input = new ByteArrayInputStream(inputText.getBytes(UTF_8))) {
      byte[] bytes = IOUtils.toByteArray(input);
      String outputText = new String(bytes, UTF_8);
      assertThat(outputText).isEqualTo(inputText);
    }
  }

  @Test
  public void test_compress_and_decompress() throws IOException {
    String inputText = "Resolute Building Intelligence xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
    byte[] inputBytes = inputText.getBytes(UTF_8);
    byte[] compressedBytes = IOUtils.compress(inputBytes);
    assertThat(compressedBytes).isNotNull();
    assertThat(compressedBytes.length).isNotEqualTo(inputBytes.length);
    System.out.println("Uncompressed length: " + inputBytes.length);
    System.out.println("Compressed length: " + compressedBytes.length);
    byte[] decompressedBytes = IOUtils.decompress(compressedBytes);
    String decompressedText = new String(decompressedBytes, UTF_8);
    assertThat(decompressedText).isEqualTo(inputText);
  }

  @Test
  public void test_resource_to_string() throws IOException {
    String text = IOUtils.resourceToString("resource.txt", StandardCharsets.UTF_8, Resource.class);
    assertThat(text).isEqualTo("Resolute");

    text = IOUtils.resourceToString("com/resolute/resources/resource.txt", StandardCharsets.UTF_8);
    assertThat(text).isEqualTo("Resolute");
  }


}
