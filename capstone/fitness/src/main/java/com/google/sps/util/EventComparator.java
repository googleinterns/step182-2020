package com.google.sps.util;

import java.util.*;
import java.lang.*; 
import java.io.*; 

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;


// Comparator for Event class. Orders Event instances by milliseconds.
class EventComparator implements Comparator<Event> 
{ 
  public int compare(Event a, Event b) {
    return Long.compare(a.getStart().getDateTime().getValue(), b.getStart().getDateTime().getValue());
  }
}