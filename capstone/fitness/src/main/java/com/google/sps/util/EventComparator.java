package com.google.sps.util;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import java.io.*; 
import java.lang.*; 
import java.util.*;

// Comparator for Event class. Orders Event instances by milliseconds.
class EventComparator implements Comparator<Event> 
{ 
  public int compare(Event a, Event b) {
    return Long.compare(a.getStart().getDateTime().getValue(), b.getStart().getDateTime().getValue());
  }
}