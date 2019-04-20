package com.resolute.flume.sinks.capturing;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.flume.Channel;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.Transaction;
import org.apache.flume.sink.AbstractSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.resolute.flume.sinks.capturing.model.CapturedEvent;

public class CapturingSink extends AbstractSink {

  Logger log = LoggerFactory.getLogger(CapturingSink.class);

  private File outputDir = new File("captures");

  @Override
  public Status process() throws EventDeliveryException {
    Status status = null;
    Channel channel = getChannel();
    Transaction transaction = channel.getTransaction();
    transaction.begin();
    try {
      Event event = channel.take();
      if (event != null) {
        process(event);
      }
      status = Status.READY;
      transaction.commit();
    } catch (Throwable t) {
      transaction.rollback();
      log.error("A problem occurred while attempting to process an event", t);
      status = Status.BACKOFF;
      if (t instanceof Error) {
        throw (Error) t;
      }
    } finally {
      transaction.close();
    }
    return status;
  }

  private void process(Event event) throws IOException {
    CapturedEvent captured = new CapturedEvent(event);
    log.info("Event Received: " + captured.getHeaders());
    File f = new File(outputDir, "event-" + System.nanoTime() + ".json");
    try (PrintWriter out = new PrintWriter(f)) {
      out.println(captured.toString());
    }
  }
}
