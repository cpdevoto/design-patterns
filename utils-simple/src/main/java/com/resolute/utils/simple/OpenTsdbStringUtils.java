package com.resolute.utils.simple;

import static java.util.Objects.requireNonNull;

import java.util.regex.Pattern;

/**
 * Another copy (different dependency branch) of this class is in
 * git/cloudfill-common/src/main/java/com/resolute/cloudfill/utils/OpenTsdbStringUtils.java
 *
 * Only the following characters are allowed: a to z, A to Z, 0 to 9, -, _, ., / or Unicode letters
 * (as per the specification)
 *
 * http://opentsdb.net/docs/build/html/user_guide/writing/index.html
 */
public abstract class OpenTsdbStringUtils {

  private static final Pattern VALIDMETRICID_PATTERN = Pattern.compile("[^a-zA-Z0-9-_\\./]");

  /**
   * note this regular expression is different from the VALIDMETRICID_PATTERN just by not allowing
   * forward slash
   */
  private static final Pattern COMPACTMETRICID_PATTERN = Pattern.compile("[^a-zA-Z0-9-_\\.]");

  /**
   * returns a valid OpenTSDB metric
   */
  public static String toValidMetricId(String s) {
    requireNonNull(s, "s cannot be null");
    return VALIDMETRICID_PATTERN.matcher(s).replaceAll("_");
  }

  /**
   * The first customer of this method is to compact a path into one compact name, for example
   *
   * <pre>
  metric from haystack connector
        /Drivers/NiagaraNetwork/Demo/West/Bldg1/Flr1/VAV105/kW
  will become
        _Drivers_NiagaraNetwork_Demo_West_Bldg1_Flr1_VAV105_kW
     and that the caller might prepend it with

       /KMC/_Drivers_NiagaraNetwork_Demo_West_Bldg1_Flr1_VAV105_kW
   *
   * </pre>
   *
   * @param s
   * @return
   */
  public static String toCompactMetricId(String s) {
    requireNonNull(s, "s cannot be null");
    return COMPACTMETRICID_PATTERN.matcher(s).replaceAll("_");
  }


  private OpenTsdbStringUtils() {}

}
