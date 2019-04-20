package org.devoware.homonculus.logging.layout;

import java.util.TimeZone;

import org.devoware.homonculus.logging.HomonculusLayout;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.PatternLayoutBase;

/**
 * Factory that creates a {@link DropwizardLayout}
 */
public class HomonculusLayoutFactory implements LayoutFactory<ILoggingEvent> {
  @Override
  public PatternLayoutBase<ILoggingEvent> build(LoggerContext context, TimeZone timeZone) {
    return new HomonculusLayout(context, timeZone);
  }
}
