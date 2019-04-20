package org.devoware.homonculus.core.lifecycle;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.devoware.homonculus.core.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LifecycleManager {
  public static final String TERMINATION_PORT = "homonculus.terminator.port";
  public static final int DEFAULT_TERMINATION_PORT = 51335;

  private static final int[] TERMINATION_CODE = new int[] {9, 18, 2, 0, 0, 9};
  private static final Logger log = LoggerFactory.getLogger(LifecycleManager.class);

  private final int terminationPort;
  private final int[] terminationCode;
  private final Environment environment;
  private final KeepAliveDaemon keepAliveDaemon;
  private final Runnable stopAction;
  private ServerSocket server;
  private Thread monitorThread;
  private ExecutorService inputProcessingService;
  private volatile boolean terminated;

  public LifecycleManager(int terminationPort, Environment environment, Runnable stopAction) {
    String sPort = System.getProperty(TERMINATION_PORT, String.valueOf(terminationPort));
    this.terminationPort = Integer.parseInt(sPort);
    this.terminationCode = TERMINATION_CODE;
    this.environment = checkNotNull(environment);
    this.keepAliveDaemon = new KeepAliveDaemon();
    this.stopAction = stopAction == null ? () -> System.exit(0) : stopAction;
    environment.manage(this.keepAliveDaemon);
  }

  public int getTerminationPort() {
    return terminationPort;
  }

  public int[] getTerminationCode() {
    return Arrays.copyOf(this.terminationCode, this.terminationCode.length);
  }

  public Environment getEnvironment() {
    return environment;
  }

  public void start() throws Exception {
    Thread keepAliveThread = new Thread(keepAliveDaemon, "Keep-Alive-Thread");
    keepAliveThread.start();
    this.inputProcessingService = Executors.newCachedThreadPool();
    server = new ServerSocket(terminationPort);
    server.setSoTimeout(30000);
    log.info(String.format("The termination sensor is listening on port %d.", terminationPort));
    monitorThread = new Thread(new MonitorTask(), "Termination-Sensor-Thread");
    monitorThread.setDaemon(true);
    monitorThread.start();
    notifyStartListeners();
  }

  public void stop() {
    this.terminated = true;
    this.inputProcessingService.shutdown();
    notifyStopListeners();
    try {
      Thread.sleep(5000);
    } catch (InterruptedException ie) {
    }
    stopAction.run();
  }

  public boolean isTerminated() {
    return terminated;
  }

  private synchronized void notifyStartListeners() {
    for (Managed listener : environment.getManagedResources()) {
      try {
        log.info(String.format("Starting %s...", listener.getClass().getName()));
        listener.start();
        log.info(String.format("%s started", listener.getClass().getName()));
      } catch (Exception ex) {
        log.error("An error occurred during termination", ex);
      }
    }
  }

  private synchronized void notifyStopListeners() {
    for (Managed listener : environment.getManagedResources()) {
      try {
        log.info(String.format("Stopping %s...", listener.getClass().getName()));
        listener.stop();
        log.info(String.format("%s stopped", listener.getClass().getName()));
      } catch (Exception ex) {
        log.error("An error occurred during termination", ex);
      }
    }
  }

  private final class MonitorTask implements Runnable {

    @Override
    public void run() {
      try {
        while (!isTerminated()) {
          try {
            Socket socket = server.accept();
            InputStream in = socket.getInputStream();
            inputProcessingService.submit(new InputProcessorTask(in));
          } catch (SocketTimeoutException ex) {
            // No connection received in 30 seconds, which is just fine; just keep looping.
          } catch (IOException ex) {
            log.error("A problem occurred within the termination sensor.", ex);
            stop();
          }
        }
      } finally {
        try {
          server.close();
        } catch (IOException e) {
        }
      }

    }

  }

  private final class InputProcessorTask implements Runnable {
    private InputStream in;

    public InputProcessorTask(InputStream in) {
      this.in = in;
    }

    @Override
    public void run() {
      try {
        boolean validTerminationCode = true;
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < terminationCode.length; i++) {
          int code = in.read();
          buf.append(String.valueOf(code));
          if (code != terminationCode[i]) {
            validTerminationCode = false;
            break;
          }
        }
        if (validTerminationCode) {
          log.warn("The termination sensor received a valid termination sequence.");
          stop();
        } else {
          log.warn(
              String.format("The termination sensor received an invalid termination sequence: %s",
                  buf.toString()));
        }
      } catch (SocketException ex) {
        // A different thread closed the socket
      } catch (IOException io) {
        log.error("A problem occurred within the termination sensor.", io);
      } finally {
        try {
          in.close();
        } catch (IOException e) {
        }
      }
    }
  }

  private class KeepAliveDaemon implements Managed, Runnable {
    private volatile boolean terminated;
    private volatile Thread contextThread;

    @Override
    public void run() {
      this.contextThread = Thread.currentThread();
      while (!terminated) {
        try {
          Thread.sleep(60000);
        } catch (InterruptedException ex) {
        }
      }
      log.info("Stopping the application...");
      // Grant a 1 second period before exiting so that other threads can terminate themselves
      // gracefully.
      try {
        Thread.sleep(1000);
      } catch (InterruptedException ex) {
      }
      log.info("The application has been stopped.");
    }

    @Override
    public void stop() {
      terminated = true;
      if (contextThread != null) {
        contextThread.interrupt();
      }
    }

    @Override
    public int getPriority() {
      return 1000;
    }

  }

}
