package com.resolute.utils.simple.pojo_generator;


/**
 * A wrapper around a StringBuilder that is used to generate source code. It offers some convenience
 * methods to the improve readability and prevent errors for common operations.
 * 
 * @author cdevoto
 *
 */
final class SourceBuffer {
  private static final String NEWLINE = "\n";
  private static final String INDENT = "  ";

  private final StringBuilder buf = new StringBuilder();
  private int indentLevel = 0;

  SourceBuffer print(String text) {
    buf.append(text);
    return this;
  }

  SourceBuffer println(String text) {
    buf.append(text).append(NEWLINE);
    return this;
  }

  SourceBuffer println() {
    buf.append(NEWLINE);
    return this;
  }

  SourceBuffer increaseIndent() {
    indentLevel += 1;
    return this;
  }

  SourceBuffer decreaseIndent() {
    indentLevel = Math.max(0, indentLevel - 1);
    return this;
  }

  SourceBuffer indentAndPrint(String text) {
    indent().print(text);
    return this;
  }

  SourceBuffer indentAndPrintln(String text) {
    indent().println(text);
    return this;
  }

  private SourceBuffer indent() {
    for (int i = 0; i < indentLevel; i++) {
      buf.append(INDENT);
    }
    return this;
  }

  @Override
  public String toString() {
    return buf.toString();
  }

}
