package com.resolute.dataset.cloner.log;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Locale;

/**
 * This class wraps a PrintWriter but automatically calls flush after each write operation in order
 * to ensure that everything gets written to the underlying file even if the application crashes or
 * is stopped. We use this class to store information about database operations that may have to be
 * rolled back manually if the dataset cloner crashes.
 * 
 * @author cdevoto
 *
 */
public class Logger implements AutoCloseable {

  private final File file;
  private PrintWriter out;

  public Logger(String filePath) {
    requireNonNull(filePath, "filePath cannot be null");
    this.file = new File(filePath);
  }

  public Logger(File file) {
    this.file = requireNonNull(file, "file cannot be null");
  }

  public File getFile() {
    return file;
  }

  public Logger print(boolean b) {
    initializeOutputStream();
    out.print(b);
    out.flush();
    return this;
  }

  public Logger print(char c) {
    initializeOutputStream();
    out.print(c);
    out.flush();
    return this;
  }

  public Logger print(int i) {
    initializeOutputStream();
    out.print(i);
    out.flush();
    return this;
  }

  public Logger print(long l) {
    initializeOutputStream();
    out.print(l);
    out.flush();
    return this;
  }

  public Logger print(float f) {
    initializeOutputStream();
    out.print(f);
    out.flush();
    return this;
  }

  public Logger print(double d) {
    initializeOutputStream();
    out.print(d);
    out.flush();
    return this;
  }

  public Logger print(char[] s) {
    initializeOutputStream();
    out.print(s);
    out.flush();
    return this;
  }

  public Logger print(String s) {
    initializeOutputStream();
    out.print(s);
    out.flush();
    return this;
  }

  public Logger print(Object obj) {
    initializeOutputStream();
    out.print(obj);
    out.flush();
    return this;
  }

  public Logger println() {
    initializeOutputStream();
    out.println();
    out.flush();
    return this;
  }

  public Logger println(boolean x) {
    initializeOutputStream();
    out.println(x);
    out.flush();
    return this;
  }

  public Logger println(char x) {
    initializeOutputStream();
    out.println(x);
    out.flush();
    return this;
  }

  public Logger println(int x) {
    initializeOutputStream();
    out.println(x);
    out.flush();
    return this;
  }

  public Logger println(long x) {
    initializeOutputStream();
    out.println(x);
    out.flush();
    return this;
  }

  public Logger println(float x) {
    initializeOutputStream();
    out.println(x);
    out.flush();
    return this;
  }

  public Logger println(double x) {
    initializeOutputStream();
    out.println(x);
    out.flush();
    return this;
  }

  public Logger println(char[] x) {
    initializeOutputStream();
    out.println(x);
    out.flush();
    return this;
  }

  public Logger println(String x) {
    initializeOutputStream();
    out.println(x);
    out.flush();
    return this;
  }

  public Logger println(Object x) {
    initializeOutputStream();
    out.println(x);
    out.flush();
    return this;
  }

  public Logger printf(String format, Object... args) {
    initializeOutputStream();
    out.printf(format, args);
    out.flush();
    return this;
  }

  public Logger printf(Locale l, String format, Object... args) {
    initializeOutputStream();
    out.printf(l, format, args);
    out.flush();
    return this;
  }

  @Override
  public void close() throws Exception {
    initializeOutputStream();
    out.close();
  }

  private void initializeOutputStream() {
    if (this.out == null) {
      try {
        this.out = new PrintWriter(this.file);
      } catch (FileNotFoundException e) {
        throw new RuntimeException(e);
      }
    }
  }


}
