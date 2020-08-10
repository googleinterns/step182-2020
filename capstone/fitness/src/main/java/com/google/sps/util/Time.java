package com.google.sps.util;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Event;
import java.util.*;

// time class contains methods that help with handling time in other Calender-related classes.
public class Time{
  // using a private constructor since no instance of this object should be made.
  private Time(){}
  public static final int minutesToMilliseconds = 60000;

  public static long eventDateTimeToMilliseconds(EventDateTime e){
    return e.getDateTime().getValue();
  }

  public static long getMillisecondsBetween(EventDateTime a, EventDateTime b){
    return b.getDateTime().getValue() - a.getDateTime().getValue();
  }
}