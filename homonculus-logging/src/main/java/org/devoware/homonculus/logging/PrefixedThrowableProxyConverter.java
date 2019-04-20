package org.devoware.homonculus.logging;

import java.util.regex.Pattern;

import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.IThrowableProxy;

/**
 * A {@link ThrowableProxyConverter} which prefixes stack traces with {@code !}.
 */
public class PrefixedThrowableProxyConverter extends ThrowableProxyConverter {

  static final Pattern PATTERN = Pattern.compile("^\\t?", Pattern.MULTILINE);
  static final String PREFIX = "! ";

  @Override
  protected String throwableProxyToString(IThrowableProxy tp) {
    return PATTERN.matcher(super.throwableProxyToString(tp)).replaceAll(PREFIX);
  }
}
