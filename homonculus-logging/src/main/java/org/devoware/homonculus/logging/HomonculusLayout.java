package org.devoware.homonculus.logging;

import java.util.TimeZone;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;

public class HomonculusLayout extends PatternLayout {

  public HomonculusLayout(LoggerContext context, TimeZone timeZone) {
    super();
    setOutputPatternAsHeader(false);
    getDefaultConverterMap().put("ex", PrefixedThrowableProxyConverter.class.getName());
    getDefaultConverterMap().put("xEx", PrefixedExtendedThrowableProxyConverter.class.getName());
    getDefaultConverterMap().put("rEx",
        PrefixedRootCauseFirstThrowableProxyConverter.class.getName());
    setPattern("%-5p [%d{ISO8601," + timeZone.getID() + "}] %c: %m%n%rEx");
    setContext(context);
  }

}
