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
  public Event getFreeTime( EventDateTime minSpan, EventDateTime maxSpan, Collection<Event> currentlyScheduledEvents) {
    
    // Initial capacity of eventsQueue is 1 larger than the size of events so that it can handle the case where there are no events. 
    PriorityQueue<Event> eventQueue = new PriorityQueue<Event>((currentlyScheduledEvents.size() +1), new EventComparator());
    for (Event evt : currentlyScheduledEvents){
      eventQueue.add(evt);
    }
    
    EventDateTime now = minSpan;
    EventDateTime end = maxSpan;
 
 
    long exerciseMilliseconds = this.exerciseDuration * Time.minutesToMilliseconds;
    
    // If there is no space for an exercise between 'now' and the next event, then dequeue the first event.
    while (eventQueue.size() > 0 && Time.eventDateTimeToMilliseconds(eventQueue.peek().getStart()) - Time.eventDateTimeToMilliseconds(now) < (exerciseMilliseconds)) {
      now = eventQueue.poll().getEnd();
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
}
