package com.resolute.utils.simple;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class StateUtils {
  
  private static final Map<String, String> STATE_ABBREVIATIONS;

  static {
    Map<String, String> stateAbbreviations = Maps.newHashMap();
    
    stateAbbreviations.put("ALABAMA", "AL");
    stateAbbreviations.put("ALASKA", "AK");
    stateAbbreviations.put("ARIZONA", "AZ");
    stateAbbreviations.put("ARKANSAS", "AR");
    stateAbbreviations.put("CALIFORNIA", "CA");
    stateAbbreviations.put("COLORADO", "CO");
    stateAbbreviations.put("CONNECTICUT", "CT");
    stateAbbreviations.put("DISTRICT OF COLUMBIA", "DC");
    stateAbbreviations.put("DELAWARE", "DE");
    stateAbbreviations.put("FLORIDA", "FL");
                      
    stateAbbreviations.put("GEORGIA", "GA");
    stateAbbreviations.put("HAWAII", "HI");
    stateAbbreviations.put("IDAHO", "ID");
    stateAbbreviations.put("ILLINOIS", "IL");
    stateAbbreviations.put("INDIANA", "IN");
    stateAbbreviations.put("IOWA", "IA");
    stateAbbreviations.put("KANSAS", "KS");
    stateAbbreviations.put("KENTUCKY", "KY");
    stateAbbreviations.put("LOUISIANA", "LA");
    stateAbbreviations.put("MAINE", "ME");
                     
    stateAbbreviations.put("MARYLAND", "MD");
    stateAbbreviations.put("MASSACHUSETTS", "MA");
    stateAbbreviations.put("MICHIGAN", "MI");
    stateAbbreviations.put("MINNESOTA", "MN");
    stateAbbreviations.put("MISSISSIPPI", "MS");
    stateAbbreviations.put("MISSOURI", "MO");
    stateAbbreviations.put("MONTANA", "MT");
    stateAbbreviations.put("NEBRASKA", "NE");
    stateAbbreviations.put("NEVADA", "NV");
    stateAbbreviations.put("NEW HAMPSHIRE", "NH");
                    
    stateAbbreviations.put("NEW JERSEY", "NJ");
    stateAbbreviations.put("NEW MEXICO", "NM");
    stateAbbreviations.put("NEW YORK", "NY");
    stateAbbreviations.put("NORTH CAROLINA", "NC");
    stateAbbreviations.put("NORTH DAKOTA", "ND");
    stateAbbreviations.put("OHIO", "OH");
    stateAbbreviations.put("OKLAHOMA", "OK");
    stateAbbreviations.put("OREGON", "OR");
    stateAbbreviations.put("PENNSYLVANIA", "PA");
    stateAbbreviations.put("RHODE ISLAND", "RI");
                  
    stateAbbreviations.put("SOUTH CAROLINA", "SC");
    stateAbbreviations.put("SOUTH DAKOTA", "SD");
    stateAbbreviations.put("TENNESSEE", "TN");
    stateAbbreviations.put("TEXAS", "TX");
    stateAbbreviations.put("UTAH", "UT");
    stateAbbreviations.put("VERMONT", "VT");
    stateAbbreviations.put("VIRGINIA", "VA");
    stateAbbreviations.put("WASHINGTON", "WA");
    stateAbbreviations.put("WEST VIRGINIA", "WV");
    stateAbbreviations.put("WISCONSIN", "WI");
              
    stateAbbreviations.put("WYOMING", "WY");
    stateAbbreviations.put("GUAM", "GU");
    stateAbbreviations.put("PUERTO RICO", "PR");
    stateAbbreviations.put("VIRGIN ISLANDS", "VI");
    stateAbbreviations.put("UNITED STATES VIRGIN ISLANDS", "VI");
    
    STATE_ABBREVIATIONS = ImmutableMap.copyOf(stateAbbreviations);
  }

  public static String getStateAbbreviation(String stateName) {
    
    if (stateName == null || stateName.isEmpty()) {
      throw new IllegalArgumentException("State name must be specified");
    }
    return STATE_ABBREVIATIONS.get(stateName.toUpperCase().trim());
  }

  private StateUtils() {}
}
