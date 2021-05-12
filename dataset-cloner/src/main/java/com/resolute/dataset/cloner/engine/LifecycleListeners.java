package com.resolute.dataset.cloner.engine;

public class LifecycleListeners {

  @FunctionalInterface
  public static interface BeforeAllListener {
    public void onEvent();
  }

  @FunctionalInterface
  public static interface AfterAllListener {
    public void onEvent(long elapsedTime);
  }

  @FunctionalInterface
  public static interface BeforeEachListener {
    public void onEvent(int copyNumber);
  }

  @FunctionalInterface
  public static interface AfterEachListener {
    public void onEvent(long elapsedTime, int copyNumber);
  }
}
