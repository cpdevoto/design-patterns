package org.devoware.homonculus.logging;

import org.devoware.homonculus.logging.async.AsyncAppenderFactory;
import org.devoware.homonculus.logging.filter.LevelFilterFactory;
import org.devoware.homonculus.logging.layout.LayoutFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.OutputStreamAppender;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.core.sift.AppenderFactory;
import ch.qos.logback.core.spi.DeferredProcessingAware;

/**
 * A base implementation of {@link AppenderFactory} producing an appender based on
 * {@link OutputStreamAppender}.
 */
public abstract class AbstractOutputStreamAppenderFactory<E extends DeferredProcessingAware>
    extends AbstractAppenderFactory<E> {

  protected abstract OutputStreamAppender<E> appender(LoggerContext context);

  @Override
  public Appender<E> build(LoggerContext context, String applicationName,
      LayoutFactory<E> layoutFactory,
      LevelFilterFactory<E> levelFilterFactory, AsyncAppenderFactory<E> asyncAppenderFactory) {
    final OutputStreamAppender<E> appender = appender(context);
    final LayoutWrappingEncoder<E> layoutEncoder = new LayoutWrappingEncoder<>();
    layoutEncoder.setLayout(buildLayout(context, layoutFactory));
    appender.setEncoder(layoutEncoder);

    appender.addFilter(levelFilterFactory.build(threshold));
    getFilterFactories().forEach(f -> appender.addFilter(f.build()));
    appender.start();
    return wrapAsync(appender, asyncAppenderFactory);
  }
}
