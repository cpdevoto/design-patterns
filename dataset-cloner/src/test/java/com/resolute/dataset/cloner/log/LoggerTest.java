package com.resolute.dataset.cloner.log;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.Scanner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.resolute.dataset.cloner.log.Logger;

public class LoggerTest {

  @TempDir
  static File tempDir;

  @Test
  void test_lines_written_even_if_app_crashes() throws Exception {
    File outputFile = new File(tempDir, "output.log");
    try (Logger log = new Logger(outputFile)) {
      try {
        log.println("line 1");
        log.println("line 2");
        log.println("line 3");
        throw new AssertionError("The application is crashing without closing the output file!");
      } finally {
        // Confirm that all of the lines were written to the output file.
        try (Scanner in = new Scanner(outputFile)) {
          assertThat(in.hasNextLine()).isTrue();
          assertThat(in.nextLine()).isEqualTo("line 1");
          assertThat(in.hasNextLine()).isTrue();
          assertThat(in.nextLine()).isEqualTo("line 2");
          assertThat(in.hasNextLine()).isTrue();
          assertThat(in.nextLine()).isEqualTo("line 3");
          assertThat(in.hasNextLine()).isFalse();
        }
      }
    } catch (AssertionError e) {
      // This is expected
    }

  }
}
