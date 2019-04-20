package org.devoware.homonculus.core.shutdown;

import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import org.devoware.homonculus.core.lifecycle.LifecycleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Terminator {
  private static final Logger log = LoggerFactory.getLogger(Terminator.class);

  private final int terminationPort;
  private int[] terminationCode;

  public Terminator(LifecycleManager lifecycleManager) {
    this.terminationPort = lifecycleManager.getTerminationPort();
    this.terminationCode = lifecycleManager.getTerminationCode();
  }

  public void terminate() throws Exception {
    log.info(
        "Sending termination signal to stop the application at port " + terminationPort + "...");
    Socket socket = null;
    try {
      InetAddress server = InetAddress.getByName("localhost");
      socket = new Socket(server, terminationPort);
      OutputStream out = socket.getOutputStream();
      for (int i = 0; i < terminationCode.length; i++) {
        out.write(terminationCode[i]);
      }
      log.info("Termination signal successfully sent.");
    } finally {
      if (socket != null) {
        socket.close();
      }
    }
  }
}
