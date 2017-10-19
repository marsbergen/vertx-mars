package com.vanmarsbergen.mars.core;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public interface DateTime {

  static int getCurrentYear() {
    return Calendar.getInstance().get(Calendar.YEAR);
  }

  /**
   * Get the current DateTime string in "yyyy-MM-dd HH:mm:ss" format.
   *
   * @return DateTime string in "yyyy-MM-dd HH:mm:ss" format
   */
  static String getDateTimeString() {
    return getDateTimeString(Calendar.getInstance().getTime());
  }

  static String getDateTimeString(String date) throws ParseException {
    TimeZone tz = TimeZone.getTimeZone("UTC");
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    dateFormat.setTimeZone(tz);
    return dateFormat.format(dateFormat.parse(date));
  }

  /**
   * Get the current DateTime string in "yyyy-MM-dd HH:mm:ss" format.
   *
   * @return DateTime string in "yyyy-MM-dd HH:mm:ss" format
   */
  static String getDateTimeString(Date date) {
    TimeZone tz = TimeZone.getTimeZone("UTC");
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    dateFormat.setTimeZone(tz);
    return dateFormat.format(date);
  }

}
