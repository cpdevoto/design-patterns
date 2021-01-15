package com.resolute.utils.simple;

import static com.resolute.utils.simple.StringUtils.padWithZeroes;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

public class TimezoneUtils {
  private static final Map<String, TimeZone> TIMEZONES_BY_ID;
  private static final Map<String, TimeZone> TIMEZONES_BY_LABEL;
  private static final Map<TimeZone, String> LABELS_BY_TIMEZONE;
  private static final Set<String> TIMEZONE_LABELS;
  private static final Map<String, String> TIMEZONE_OFFSETS_BY_LABEL;
  private static final Map<TimeZone, TimeZone> TIMEZONE_ALIASES;

  static {
    Map<String, TimeZone> timezonesById = Maps.newTreeMap();
    for (String id : TimeZone.getAvailableIDs()) {
      timezonesById.put(id.toLowerCase(), TimeZone.getTimeZone(id));
    }
    TIMEZONES_BY_ID = ImmutableMap.copyOf(timezonesById);

    // Map data comes from http://api.rubyonrails.org/classes/ActiveSupport/TimeZone.html
    // TODO: This list seems to be more comprehensive: https://gist.github.com/jpmckinney/767070
    Map<String, TimeZone> timezonesByLabel = Maps.newTreeMap();
    timezonesByLabel.put("Abu Dhabi", TimeZone.getTimeZone("Asia/Muscat"));
    timezonesByLabel.put("Adelaide", TimeZone.getTimeZone("Australia/Adelaide"));
    timezonesByLabel.put("Alaska", TimeZone.getTimeZone("America/Juneau"));
    timezonesByLabel.put("Almaty", TimeZone.getTimeZone("Asia/Almaty"));
    timezonesByLabel.put("American Samoa", TimeZone.getTimeZone("Pacific/Pago_Pago"));
    timezonesByLabel.put("Amsterdam", TimeZone.getTimeZone("Europe/Amsterdam"));
    timezonesByLabel.put("Arizona", TimeZone.getTimeZone("America/Phoenix"));
    timezonesByLabel.put("Astana", TimeZone.getTimeZone("Asia/Dhaka"));
    timezonesByLabel.put("Athens", TimeZone.getTimeZone("Europe/Athens"));
    timezonesByLabel.put("Atlantic Time (Canada)", TimeZone.getTimeZone("America/Halifax"));
    timezonesByLabel.put("Auckland", TimeZone.getTimeZone("Pacific/Auckland"));
    timezonesByLabel.put("Azores", TimeZone.getTimeZone("Atlantic/Azores"));
    timezonesByLabel.put("Baghdad", TimeZone.getTimeZone("Asia/Baghdad"));
    timezonesByLabel.put("Baku", TimeZone.getTimeZone("Asia/Baku"));
    timezonesByLabel.put("Bangkok", TimeZone.getTimeZone("Asia/Bangkok"));
    timezonesByLabel.put("Beijing", TimeZone.getTimeZone("Asia/Shanghai"));
    timezonesByLabel.put("Belgrade", TimeZone.getTimeZone("Europe/Belgrade"));
    timezonesByLabel.put("Berlin", TimeZone.getTimeZone("Europe/Berlin"));
    timezonesByLabel.put("Bern", TimeZone.getTimeZone("Europe/Zurich"));
    timezonesByLabel.put("Bogota", TimeZone.getTimeZone("America/Bogota"));
    timezonesByLabel.put("Brasilia", TimeZone.getTimeZone("America/Sao_Paulo"));
    timezonesByLabel.put("Bratislava", TimeZone.getTimeZone("Europe/Bratislava"));
    timezonesByLabel.put("Brisbane", TimeZone.getTimeZone("Australia/Brisbane"));
    timezonesByLabel.put("Brussels", TimeZone.getTimeZone("Europe/Brussels"));
    timezonesByLabel.put("Bucharest", TimeZone.getTimeZone("Europe/Bucharest"));
    timezonesByLabel.put("Budapest", TimeZone.getTimeZone("Europe/Budapest"));
    timezonesByLabel.put("Buenos Aires", TimeZone.getTimeZone("America/Argentina/Buenos_Aires"));
    timezonesByLabel.put("Cairo", TimeZone.getTimeZone("Africa/Cairo"));
    timezonesByLabel.put("Canberra", TimeZone.getTimeZone("Australia/Melbourne"));
    timezonesByLabel.put("Cape Verde Is.", TimeZone.getTimeZone("Atlantic/Cape_Verde"));
    timezonesByLabel.put("Caracas", TimeZone.getTimeZone("America/Caracas"));
    timezonesByLabel.put("Casablanca", TimeZone.getTimeZone("Africa/Casablanca"));
    timezonesByLabel.put("Central America", TimeZone.getTimeZone("America/Guatemala"));
    timezonesByLabel.put("Central Time (US & Canada)", TimeZone.getTimeZone("America/Chicago"));
    timezonesByLabel.put("Chatham Is.", TimeZone.getTimeZone("Pacific/Chatham"));
    timezonesByLabel.put("Chennai", TimeZone.getTimeZone("Asia/Kolkata"));
    timezonesByLabel.put("Chihuahua", TimeZone.getTimeZone("America/Chihuahua"));
    timezonesByLabel.put("Chongqing", TimeZone.getTimeZone("Asia/Chongqing"));
    timezonesByLabel.put("Copenhagen", TimeZone.getTimeZone("Europe/Copenhagen"));
    timezonesByLabel.put("Darwin", TimeZone.getTimeZone("Australia/Darwin"));
    timezonesByLabel.put("Dhaka", TimeZone.getTimeZone("Asia/Dhaka"));
    timezonesByLabel.put("Dublin", TimeZone.getTimeZone("Europe/Dublin"));
    timezonesByLabel.put("Eastern Time (US & Canada)", TimeZone.getTimeZone("America/New_York"));
    timezonesByLabel.put("Edinburgh", TimeZone.getTimeZone("Europe/London"));
    timezonesByLabel.put("Ekaterinburg", TimeZone.getTimeZone("Asia/Yekaterinburg"));
    timezonesByLabel.put("Fiji", TimeZone.getTimeZone("Pacific/Fiji"));
    timezonesByLabel.put("Georgetown", TimeZone.getTimeZone("America/Guyana"));
    timezonesByLabel.put("Greenland", TimeZone.getTimeZone("America/Godthab"));
    timezonesByLabel.put("Guadalajara", TimeZone.getTimeZone("America/Mexico_City"));
    timezonesByLabel.put("Guam", TimeZone.getTimeZone("Pacific/Guam"));
    timezonesByLabel.put("Hanoi", TimeZone.getTimeZone("Asia/Bangkok"));
    timezonesByLabel.put("Harare", TimeZone.getTimeZone("Africa/Harare"));
    timezonesByLabel.put("Hawaii", TimeZone.getTimeZone("Pacific/Honolulu"));
    timezonesByLabel.put("Helsinki", TimeZone.getTimeZone("Europe/Helsinki"));
    timezonesByLabel.put("Hobart", TimeZone.getTimeZone("Australia/Hobart"));
    timezonesByLabel.put("Hong Kong", TimeZone.getTimeZone("Asia/Hong_Kong"));
    timezonesByLabel.put("Indiana (East)", TimeZone.getTimeZone("America/Indiana/Indianapolis"));
    timezonesByLabel.put("International Date Line West", TimeZone.getTimeZone("Pacific/Midway"));
    timezonesByLabel.put("Irkutsk", TimeZone.getTimeZone("Asia/Irkutsk"));
    timezonesByLabel.put("Islamabad", TimeZone.getTimeZone("Asia/Karachi"));
    timezonesByLabel.put("Istanbul", TimeZone.getTimeZone("Europe/Istanbul"));
    timezonesByLabel.put("Jakarta", TimeZone.getTimeZone("Asia/Jakarta"));
    timezonesByLabel.put("Jerusalem", TimeZone.getTimeZone("Asia/Jerusalem"));
    timezonesByLabel.put("Kabul", TimeZone.getTimeZone("Asia/Kabul"));
    timezonesByLabel.put("Kaliningrad", TimeZone.getTimeZone("Europe/Kaliningrad"));
    timezonesByLabel.put("Kamchatka", TimeZone.getTimeZone("Asia/Kamchatka"));
    timezonesByLabel.put("Karachi", TimeZone.getTimeZone("Asia/Karachi"));
    timezonesByLabel.put("Kathmandu", TimeZone.getTimeZone("Asia/Kathmandu"));
    timezonesByLabel.put("Kolkata", TimeZone.getTimeZone("Asia/Kolkata"));
    timezonesByLabel.put("Krasnoyarsk", TimeZone.getTimeZone("Asia/Krasnoyarsk"));
    timezonesByLabel.put("Kuala Lumpur", TimeZone.getTimeZone("Asia/Kuala_Lumpur"));
    timezonesByLabel.put("Kuwait", TimeZone.getTimeZone("Asia/Kuwait"));
    timezonesByLabel.put("Kyiv", TimeZone.getTimeZone("Europe/Kiev"));
    timezonesByLabel.put("La Paz", TimeZone.getTimeZone("America/La_Paz"));
    timezonesByLabel.put("Lima", TimeZone.getTimeZone("America/Lima"));
    timezonesByLabel.put("Lisbon", TimeZone.getTimeZone("Europe/Lisbon"));
    timezonesByLabel.put("Ljubljana", TimeZone.getTimeZone("Europe/Ljubljana"));
    timezonesByLabel.put("London", TimeZone.getTimeZone("Europe/London"));
    timezonesByLabel.put("Madrid", TimeZone.getTimeZone("Europe/Madrid"));
    timezonesByLabel.put("Magadan", TimeZone.getTimeZone("Asia/Magadan"));
    timezonesByLabel.put("Marshall Is.", TimeZone.getTimeZone("Pacific/Majuro"));
    timezonesByLabel.put("Mazatlan", TimeZone.getTimeZone("America/Mazatlan"));
    timezonesByLabel.put("Melbourne", TimeZone.getTimeZone("Australia/Melbourne"));
    timezonesByLabel.put("Mexico City", TimeZone.getTimeZone("America/Mexico_City"));
    timezonesByLabel.put("Mid-Atlantic", TimeZone.getTimeZone("Atlantic/South_Georgia"));
    timezonesByLabel.put("Midway Island", TimeZone.getTimeZone("Pacific/Midway"));
    timezonesByLabel.put("Minsk", TimeZone.getTimeZone("Europe/Minsk"));
    timezonesByLabel.put("Monrovia", TimeZone.getTimeZone("Africa/Monrovia"));
    timezonesByLabel.put("Monterrey", TimeZone.getTimeZone("America/Monterrey"));
    timezonesByLabel.put("Montevideo", TimeZone.getTimeZone("America/Montevideo"));
    timezonesByLabel.put("Moscow", TimeZone.getTimeZone("Europe/Moscow"));
    timezonesByLabel.put("Mountain Time (US & Canada)", TimeZone.getTimeZone("America/Denver"));
    timezonesByLabel.put("Mumbai", TimeZone.getTimeZone("Asia/Kolkata"));
    timezonesByLabel.put("Muscat", TimeZone.getTimeZone("Asia/Muscat"));
    timezonesByLabel.put("Nairobi", TimeZone.getTimeZone("Africa/Nairobi"));
    timezonesByLabel.put("New Caledonia", TimeZone.getTimeZone("Pacific/Noumea"));
    timezonesByLabel.put("New Delhi", TimeZone.getTimeZone("Asia/Kolkata"));
    timezonesByLabel.put("Newfoundland", TimeZone.getTimeZone("America/St_Johns"));
    timezonesByLabel.put("Novosibirsk", TimeZone.getTimeZone("Asia/Novosibirsk"));
    timezonesByLabel.put("Nuku'alofa", TimeZone.getTimeZone("Pacific/Tongatapu"));
    timezonesByLabel.put("Osaka", TimeZone.getTimeZone("Asia/Tokyo"));
    timezonesByLabel.put("Pacific Time (US & Canada)", TimeZone.getTimeZone("America/Los_Angeles"));
    timezonesByLabel.put("Paris", TimeZone.getTimeZone("Europe/Paris"));
    timezonesByLabel.put("Perth", TimeZone.getTimeZone("Australia/Perth"));
    timezonesByLabel.put("Port Moresby", TimeZone.getTimeZone("Pacific/Port_Moresby"));
    timezonesByLabel.put("Prague", TimeZone.getTimeZone("Europe/Prague"));
    timezonesByLabel.put("Pretoria", TimeZone.getTimeZone("Africa/Johannesburg"));
    timezonesByLabel.put("Quito", TimeZone.getTimeZone("America/Lima"));
    timezonesByLabel.put("Rangoon", TimeZone.getTimeZone("Asia/Rangoon"));
    timezonesByLabel.put("Riga", TimeZone.getTimeZone("Europe/Riga"));
    timezonesByLabel.put("Riyadh", TimeZone.getTimeZone("Asia/Riyadh"));
    timezonesByLabel.put("Rome", TimeZone.getTimeZone("Europe/Rome"));
    timezonesByLabel.put("Samara", TimeZone.getTimeZone("Europe/Samara"));
    timezonesByLabel.put("Samoa", TimeZone.getTimeZone("Pacific/Apia"));
    timezonesByLabel.put("Santiago", TimeZone.getTimeZone("America/Santiago"));
    timezonesByLabel.put("Sapporo", TimeZone.getTimeZone("Asia/Tokyo"));
    timezonesByLabel.put("Sarajevo", TimeZone.getTimeZone("Europe/Sarajevo"));
    timezonesByLabel.put("Saskatchewan", TimeZone.getTimeZone("America/Regina"));
    timezonesByLabel.put("Seoul", TimeZone.getTimeZone("Asia/Seoul"));
    timezonesByLabel.put("Singapore", TimeZone.getTimeZone("Asia/Singapore"));
    timezonesByLabel.put("Skopje", TimeZone.getTimeZone("Europe/Skopje"));
    timezonesByLabel.put("Sofia", TimeZone.getTimeZone("Europe/Sofia"));
    timezonesByLabel.put("Solomon Is.", TimeZone.getTimeZone("Pacific/Guadalcanal"));
    timezonesByLabel.put("Srednekolymsk", TimeZone.getTimeZone("Asia/Srednekolymsk"));
    timezonesByLabel.put("Sri Jayawardenepura", TimeZone.getTimeZone("Asia/Colombo"));
    timezonesByLabel.put("St. Petersburg", TimeZone.getTimeZone("Europe/Moscow"));
    timezonesByLabel.put("Stockholm", TimeZone.getTimeZone("Europe/Stockholm"));
    timezonesByLabel.put("Sydney", TimeZone.getTimeZone("Australia/Sydney"));
    timezonesByLabel.put("Taipei", TimeZone.getTimeZone("Asia/Taipei"));
    timezonesByLabel.put("Tallinn", TimeZone.getTimeZone("Europe/Tallinn"));
    timezonesByLabel.put("Tashkent", TimeZone.getTimeZone("Asia/Tashkent"));
    timezonesByLabel.put("Tbilisi", TimeZone.getTimeZone("Asia/Tbilisi"));
    timezonesByLabel.put("Tehran", TimeZone.getTimeZone("Asia/Tehran"));
    timezonesByLabel.put("Tijuana", TimeZone.getTimeZone("America/Tijuana"));
    timezonesByLabel.put("Tokelau Is.", TimeZone.getTimeZone("Pacific/Fakaofo"));
    timezonesByLabel.put("Tokyo", TimeZone.getTimeZone("Asia/Tokyo"));
    timezonesByLabel.put("UTC", TimeZone.getTimeZone("Etc/UTC"));
    timezonesByLabel.put("Ulaanbaatar", TimeZone.getTimeZone("Asia/Ulaanbaatar"));
    timezonesByLabel.put("Urumqi", TimeZone.getTimeZone("Asia/Urumqi"));
    timezonesByLabel.put("Vienna", TimeZone.getTimeZone("Europe/Vienna"));
    timezonesByLabel.put("Vilnius", TimeZone.getTimeZone("Europe/Vilnius"));
    timezonesByLabel.put("Vladivostok", TimeZone.getTimeZone("Asia/Vladivostok"));
    timezonesByLabel.put("Volgograd", TimeZone.getTimeZone("Europe/Volgograd"));
    timezonesByLabel.put("Warsaw", TimeZone.getTimeZone("Europe/Warsaw"));
    timezonesByLabel.put("Wellington", TimeZone.getTimeZone("Pacific/Auckland"));
    timezonesByLabel.put("West Central Africa", TimeZone.getTimeZone("Africa/Algiers"));
    timezonesByLabel.put("Yakutsk", TimeZone.getTimeZone("Asia/Yakutsk"));
    timezonesByLabel.put("Yerevan", TimeZone.getTimeZone("Asia/Yerevan"));
    timezonesByLabel.put("Zagreb", TimeZone.getTimeZone("Europe/Zagreb"));
    timezonesByLabel.put("Zurich", TimeZone.getTimeZone("Europe/Zurich"));

    TIMEZONE_LABELS = ImmutableSet.copyOf(timezonesByLabel.keySet());

    Map<TimeZone, String> labelsByTimezone = Maps.newHashMap();
    Map<String, TimeZone> timezonesByLowerCaseLabel = Maps.newHashMap();
    for (Entry<String, TimeZone> entry : timezonesByLabel.entrySet()) {
      labelsByTimezone.put(entry.getValue(), entry.getKey());
      timezonesByLowerCaseLabel.put(entry.getKey().toLowerCase(), entry.getValue());
    }
    LABELS_BY_TIMEZONE = ImmutableMap.copyOf(labelsByTimezone);
    TIMEZONES_BY_LABEL = ImmutableMap.copyOf(timezonesByLowerCaseLabel);

    Map<String, String> timezoneOffsetsByLabel = Maps.newHashMap();
    for (String label : TIMEZONE_LABELS) {
      TimeZone timezone = getTimezone(label);
      String offset = formatOffset(timezone.getRawOffset());
      timezoneOffsetsByLabel.put(label.toLowerCase(), offset);
    }

    TIMEZONE_OFFSETS_BY_LABEL = ImmutableMap.copyOf(timezoneOffsetsByLabel);
    
    TIMEZONE_ALIASES = Maps.newHashMap();
    
    TimeZone americaHalifax = TimeZone.getTimeZone("America/Halifax");
    TIMEZONE_ALIASES.put(TimeZone.getTimeZone("America/Puerto_Rico"), americaHalifax);
    TIMEZONE_ALIASES.put(TimeZone.getTimeZone("America/St_Barthelemy"), americaHalifax);
    TIMEZONE_ALIASES.put(TimeZone.getTimeZone("America/St_Kitts"), americaHalifax);
    TIMEZONE_ALIASES.put(TimeZone.getTimeZone("America/St_Lucia"), americaHalifax);
    TIMEZONE_ALIASES.put(TimeZone.getTimeZone("America/St_Thomas"), americaHalifax);
    TIMEZONE_ALIASES.put(TimeZone.getTimeZone("America/St_Vincent"), americaHalifax);
    
    TimeZone americaJuneau = TimeZone.getTimeZone("America/Juneau");
    TIMEZONE_ALIASES.put(TimeZone.getTimeZone("America/Anchorage"), americaJuneau);
    TIMEZONE_ALIASES.put(TimeZone.getTimeZone("America/Metlakatla"), americaJuneau);
    TIMEZONE_ALIASES.put(TimeZone.getTimeZone("America/Nome"), americaJuneau);
    TIMEZONE_ALIASES.put(TimeZone.getTimeZone("America/Sitka"), americaJuneau);
    TIMEZONE_ALIASES.put(TimeZone.getTimeZone("America/Yakutat"), americaJuneau);
    
    TimeZone americaChicago = TimeZone.getTimeZone("America/Chicago");
    TIMEZONE_ALIASES.put(TimeZone.getTimeZone("America/Indiana/Knox"), americaChicago);
    TIMEZONE_ALIASES.put(TimeZone.getTimeZone("America/Indiana/Tell_City"), americaChicago);
    TIMEZONE_ALIASES.put(TimeZone.getTimeZone("America/Menominee"), americaChicago);
    TIMEZONE_ALIASES.put(TimeZone.getTimeZone("America/North_Dakota/Beulah"), americaChicago);
    TIMEZONE_ALIASES.put(TimeZone.getTimeZone("America/North_Dakota/Center"), americaChicago);
    TIMEZONE_ALIASES.put(TimeZone.getTimeZone("America/North_Dakota/New_Salem"), americaChicago);
    
    TimeZone americaNewYork = TimeZone.getTimeZone("America/New_York");
    TIMEZONE_ALIASES.put(TimeZone.getTimeZone("America/Detroit"), americaNewYork);
    TIMEZONE_ALIASES.put(TimeZone.getTimeZone("America/Indiana/Petersburg"), americaNewYork);
    TIMEZONE_ALIASES.put(TimeZone.getTimeZone("America/Indiana/Vincennes"), americaNewYork);
    TIMEZONE_ALIASES.put(TimeZone.getTimeZone("America/Indiana/Winamac"), americaNewYork);
    TIMEZONE_ALIASES.put(TimeZone.getTimeZone("America/Kentucky/Monticello"), americaNewYork);
    TIMEZONE_ALIASES.put(TimeZone.getTimeZone("America/Kentucky/Louisville"), americaNewYork);

    TimeZone americaDenver = TimeZone.getTimeZone("America/Denver");
    TIMEZONE_ALIASES.put(TimeZone.getTimeZone("America/Boise"), americaDenver);
    
    TimeZone americaIndianapolis = TimeZone.getTimeZone("America/Indiana/Indianapolis");
    TIMEZONE_ALIASES.put(TimeZone.getTimeZone("America/Indiana/Marengo"), americaIndianapolis);
    TIMEZONE_ALIASES.put(TimeZone.getTimeZone("America/Indiana/Vevay"), americaIndianapolis);
  }

  public static TimeZone getTimezone(String label) {
    if (label != null) {
      label = label.toLowerCase();
    }
    TimeZone timezone = TIMEZONES_BY_ID.get(label);
    if (timezone != null) {
      return timezone;
    }
    return TIMEZONES_BY_LABEL.get(label);
  }

  public static String getLabel(TimeZone timezone) {
    String label = LABELS_BY_TIMEZONE.get(timezone);
    if (label == null) {
      label = LABELS_BY_TIMEZONE.get(TIMEZONE_ALIASES.get(timezone));
    }
    return label;
  }

  public static Set<String> getLabels() {
    return TIMEZONE_LABELS;
  }

  public static String getTimezoneOffset(String label) {
    if (label != null) {
      label = label.toLowerCase();
    }
    String offset = TIMEZONE_OFFSETS_BY_LABEL.get(label);
    return offset;
  }

  public static boolean validateLabel(CharSequence label) {
    if (label == null) {
      return false;
    }
    if (!TIMEZONE_LABELS.contains(label.toString())) {
      return false;
    }
    return true;
  }

  public static String formatOffset(long milliseconds) {
    long minutes = milliseconds / 60000;
    long hours = minutes / 60;
    minutes = minutes % 60;
    String result = "GMT" + ((hours < 0) ? "-" : "+") + padWithZeroes(2, hours) + ":"
        + padWithZeroes(2, minutes);
    return result;
  }

  private TimezoneUtils() {}

}
