# utils-simple

**Owner(s):** Carlos Devoto

A lightweight library containing useful utility classes. 

**NOTE:** The classes contained in this library mirror the ones contained in the resolute-utils project within the resolute-common-utils repository. I decided to copy these classes into their own library so that they could be used without pulling in a bunch of superfluous transitive dependencies. Do not add new utilities to this library if they have additional external dependencies.  Instead, you should place such classes within their own JAR file.

## Utilities

  * **ElapsedTimeUtils:** Contains logic used to format elapsed time represented as a millisecond long value into a human-readable string value.
  * **StringUtils:** Contains logic used to pad strings with spaces, or integers with zeroes.
  * **TimeUnitUtils:** Contains logic used to convert TimeUnit constants into CHronoUnit constants for use with LocalDate and LocalDateTime operations.
  
### TimeUnitUtils Sample Use
The following routine calculates the difference in seconds between the current time and the next pollInterval, aligned to the wall clock (e.g. if the pollInterval is 15 seconds, polling takes place at the top of each hour, at 15 minutes after each hour, at 30 minutes after each hour, and at 45 minutes after each hour).  

```java
  private long computeDelay(Duration pollInterval, boolean stagger) {
    // We want to align the polling schedule around the wall clock, optionally staggered within a
    // minute
    LocalDateTime localNow = LocalDateTime.now();
    ZoneId currentZone = ZoneId.systemDefault();
    ZonedDateTime zonedNow = ZonedDateTime.of(localNow, currentZone);
    ZonedDateTime zonedNext = zonedNow.withMinute(0).withSecond(0);
    while (zonedNow.compareTo(zonedNext) > 0) {
      zonedNext = zonedNext.plus(pollInterval.getQuantity(),
          TimeUnitUtils.convert(pollInterval.getUnit()));
    }
    java.time.Duration duration = java.time.Duration.between(zonedNow, zonedNext);
    long initialDelay = duration.getSeconds();
    return initialDelay;
  }
```  
