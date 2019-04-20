package com.resolutebi.testutils.docker.flume;

import org.apache.flume.Event;

@FunctionalInterface
public interface CapturedEventWatcher {

  public void accept(Event event) throws Exception;

}
