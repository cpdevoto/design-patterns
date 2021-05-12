package com.resolute.dataset.cloner.log;

import static java.util.Objects.requireNonNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.function.Consumer;

import com.resolute.database.crawler.model.Graph;

public class LogFileParser {
  private final Graph graph;

  public static LogFileParser forGraph(Graph graph) {
    return new LogFileParser(graph);
  }

  private LogFileParser(Graph graph) {
    this.graph = requireNonNull(graph);
  }

  public void parse(File file, Consumer<LogFile> consumer) {
    parse(file, 5000, consumer);
  }

  public void parse(File file, int executionThreshold, Consumer<LogFile> consumer) {
    requireNonNull(file, "file cannot be null");
    requireNonNull(consumer, "consumer cannot be null");
    LogFile.Builder logFileBuilder = LogFile.builder(graph, executionThreshold, consumer);
    ParserState state = ParserState.getInitialState();
    try (BufferedReader in = new BufferedReader(new FileReader(file))) {
      for (String line = in.readLine(); line != null; line = in.readLine()) {
        if (line.trim().isEmpty()) {
          continue;
        }
        state = state.processLine(line, logFileBuilder);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    if (!logFileBuilder.getKeysInserted().isEmpty()) {
      LogFile logFile = logFileBuilder.build();
      consumer.accept(logFile);
    }
  }

}
