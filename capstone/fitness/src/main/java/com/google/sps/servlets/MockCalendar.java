package com.google.sps.servlets;
import java.util.*;

final class MockCalendar implements MyAPI {
  private final ArrayList<Event> events = new ArrayList<>();
  
  public MockCalendar() {
    // Fill in |events| with some fake data to start.
    this.events.add(new Event(new Date( 120, 8, 5, 12, 15, 1), "test event 2", "lunch"));
    this.events.add(new Event(new Date( 120, 8, 5, 8, 5, 55), "test event 1", "meeting with sue"));
  }

  @Override
  public String CreateEvent(String title, String description, Date date) {
    Event event = new Event(date, title, description);
    this.events.add(event);
    return event.getID();
  }

  @Override
  public Event GetEvent(String id) {
    for (Event event : events){
        if(event.getID() == id){
          return event;
        }
    }
    return null; 
  }

  @Override
  public Collection<Event> GetEvents(Date min, Date max) {
    ArrayList conflicts = new ArrayList<Event>();
    // loop thru events in that time and add?
    return conflicts;
  }
}