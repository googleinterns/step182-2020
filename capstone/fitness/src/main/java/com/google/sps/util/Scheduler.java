// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License. 
package com.google.sps.util;
 
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Event;
import java.util.*;
 
// Class scheduler uses the user's free/busy information and the duration of an exercise to find a time in a user's day when the user could do an exercise.
public class Scheduler {
  private final long exerciseDuration;

  public Scheduler(long exerciseDuration) {
    this.exerciseDuration =exerciseDuration;
  }
 
  // Here, minSpan and maxSpan are the boundaries for when a workout could be scheduled on a particular day e.g 7:00 AM to 9:00 PM events is the user's free/busy information.
  // TODO (@piercedw) : change minSpan and maxSpan inputs to Java.util.DateTime objects, and construct EventDateTime objects inside method. This will help decouple fromn the actual Calendar objects. 
  public Event getFreeTime(DateTime minSpan, DateTime maxSpan, Collection<Event> currentlyScheduledEvents) {
    
    // Initial capacity of eventsQueue is 1 larger than the size of events so that it can handle the case where there are no events. 
    PriorityQueue<Event> eventQueue = new PriorityQueue<Event>((currentlyScheduledEvents.size() +1), new EventComparator());

    for (Event evt : currentlyScheduledEvents){
        if (!this.isAllDay(evt)){
        eventQueue.add(evt);}
    }
    
    // Instantiate EventDateTime objects from the DateTime objects given as parameters. 
    EventDateTime now = new EventDateTime();
    now.setDateTime(minSpan);
    EventDateTime end = new EventDateTime();
    end.setDateTime(maxSpan);
 
 
    long exerciseMilliseconds = this.exerciseDuration * Time.minutesToMilliseconds;
    
    // Keeps track of the latest end time found so far. Set to zero to begin with since
    // all DateTime objects will have a value greater than zero. 
    long latestEnd = 0;

    // If there is no space for an exercise between 'now' and the start time of next event, then dequeue the first event.
    while (eventQueue.size() > 0 && 
    Time.eventDateTimeToMilliseconds(eventQueue.peek().getStart()) - Time.eventDateTimeToMilliseconds(now) < (exerciseMilliseconds)) {
      if (Time.eventDateTimeToMilliseconds(eventQueue.peek().getEnd()) > latestEnd){ 
          // Now only needs to get reset if the end time of the current event is later than the latest end time we've found so far. 
          latestEnd = Time.eventDateTimeToMilliseconds(eventQueue.peek().getEnd());
          now = eventQueue.poll().getEnd();
          }
      else{
          // Otherwise, now stays the same and we just remove that top event, 
          // but we don't care about it's end time because we know there's something later that's likely overlapping.
          eventQueue.poll();
      }
      
      }
    // Once broken out of the loop (i.e the user has no more events), check that there is enough time, and schedule at now. 
    if (Time.eventDateTimeToMilliseconds(now) > Time.eventDateTimeToMilliseconds(end) - (exerciseMilliseconds)) {
      return null;
     }  

    // TODO (@piercedw) : Implement builder pattern for creating event here. 
    Event event = new Event();
    event.setStart(now);
    long endValue = Time.eventDateTimeToMilliseconds(now) + exerciseMilliseconds;
    DateTime endDT = new DateTime(endValue);
    EventDateTime e = new EventDateTime();
    e.setDateTime(endDT);
    event.setEnd(e);
    event.setDescription("success"); 
    return event;  
 
  }
  public Boolean isAllDay(Event e){
    return e.getStart().getDate()!=null;
  }
}