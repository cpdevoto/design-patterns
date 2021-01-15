package com.resolute.utils.simple;

import static java.util.Objects.requireNonNull;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class IOUtils {

  private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
  private static final int EOF = -1;

  public static byte[] toByteArray(final InputStream input) throws IOException {
    requireNonNull(input, "input cannot be null");
    try (final ByteArrayOutputStream output = new ByteArrayOutputStream()) {
      copy(input, output);
      return output.toByteArray();
    }
  }

  public static long copy(final InputStream input, final OutputStream output) throws IOException {
    requireNonNull(input, "input cannot be null");
    requireNonNull(output, "input cannot be null");
    final byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
    long count = 0;
    int n;
    while (EOF != (n = input.read(buffer))) {
      output.write(buffer, 0, n);
      count += n;
    }
    return count;
  }

  public static long copy(final Reader input, final Writer output) throws IOException {
    requireNonNull(input, "input cannot be null");
    requireNonNull(output, "input cannot be null");
    final char[] buffer = new char[DEFAULT_BUFFER_SIZE];
    long count = 0;
    int n;
    while (EOF != (n = input.read(buffer))) {
      output.write(buffer, 0, n);
      count += n;
    }
    return count;
  }

  public static void copy(final InputStream input, final Writer output, final Charset inputEncoding)
      throws IOException {
    final InputStreamReader in = new InputStreamReader(input, inputEncoding);
    copy(in, output);
  }

  public static byte[] compress(final byte[] bytes) throws IOException {
    requireNonNull(bytes, "bytes cannot be null");
    try (final ByteArrayInputStream input = new ByteArrayInputStream(bytes)) {
      return compress(input);
    }
  }

  public static byte[] compress(final InputStream input) throws IOException {
    requireNonNull(input, "input cannot be null");
    try (final ByteArrayOutputStream ba = new ByteArrayOutputStream()) {
      try (final GZIPOutputStream output = new GZIPOutputStream(ba)) {
        output.write(toByteArray(input));
      }
      return ba.toByteArray();
    }
  }

  public static byte[] decompress(final byte[] bytes) throws IOException {
    requireNonNull(bytes, "bytes cannot be null");
    try (ByteArrayInputStream input = new ByteArrayInputStream(bytes)) {
      return decompress(input);
    }
  }

  public static byte[] decompress(final InputStream input) throws IOException {
    requireNonNull(input, "input cannot be null");
    try (GZIPInputStream gzipInput = new GZIPInputStream(input)) {
      return toByteArray(gzipInput);
    }
  }

  public static String resourceToString(final String name, final Charset encoding)
      throws IOException {
    return resourceToString(name, encoding, null);
  }

  public static String resourceToString(final String name, final Charset encoding,
      final Class<?> clazz) throws IOException {
    try (
        InputStream input =
            (clazz == null ? IOUtils.class.getClassLoader().getResourceAsStream(name)
                : clazz.getResourceAsStream(name));
        final StringBuilderWriter sw = new StringBuilderWriter()) {
      copy(input, sw, encoding);
      return sw.toString();
    }
  }

  public static void fastCopy(final InputStream input, final OutputStream output) throws IOException {
    
    final ReadableByteChannel inputChannel = Channels.newChannel(input);
    final WritableByteChannel outputChannel = Channels.newChannel(output);
    fastCopy(inputChannel, outputChannel);
  }
  
  public static void fastCopy(final ReadableByteChannel input, final WritableByteChannel output) throws IOException {
    
    final ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);
    while (input.read(buffer) != -1) {
      buffer.flip();
      output.write(buffer);
      buffer.compact();
    }
    
    buffer.flip();
    
    while(buffer.hasRemaining()) {
      output.write(buffer);
    }
  }

  private IOUtils() {}
}
