package com.resolute.utils.simple;

public final class ExceptionUtils {

  /**
   * 
   * Extracts all of the throwable's messages (including root causes to 3 levels deep),
   * as well any com.resolute related stack trace elements.
   * 
   * @param errorMessagePrefix A messsage to prepend to the combined error message string
   * @param t1 Throwable to process
   * @return
   */
  public static String extractReason(Throwable t1) {
    return extractReason(null, t1);
  }
  
  /**
   * 
   * Extracts all of the throwable's messages (including root causes to 3 levels deep),
   * as well any com.resolute related stack trace elements.
   * 
   * @param t1 Throwable to process
   * @return
   */
  public static String extractReason(String errorMessagePrefix, Throwable t1) {
    
    if (t1 == null) {
      throw new IllegalArgumentException("t1 cannot be null");
    }

    StringBuilder sb = new StringBuilder();
    if (errorMessagePrefix != null) {
      sb.append(errorMessagePrefix);
      sb.append(" ");
    }
    sb.append(t1.getMessage());
    sb.append("  ");
    Throwable t = t1;
    Throwable t2 = t1.getCause();
    if (t2 != null) {
      sb.append(t2.getMessage());
      sb.append("  ");
      t = t2;
      Throwable t3 = t2.getCause();
      if (t3 != null) {
        sb.append(t3.getMessage());
        sb.append("  ");
        t = t3;
        sb.append(t3.getMessage());
      }
    }
    
    sb.append("STACKTRACE:");
    StackTraceElement[] stackTraceElementArray = t.getStackTrace();
    for (int i=0; i < stackTraceElementArray.length; i++) {
      
      StackTraceElement stackTraceElement = stackTraceElementArray[i];
      if (stackTraceElement.getClassName().toLowerCase().contains("com.resolute")) {
        sb.append("  ");
        sb.append(stackTraceElement);
      }
    }
    return sb.toString();
  }
  
  private ExceptionUtils() {}
}
