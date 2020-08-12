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

// class scheduler uses the user's free/busy information and the duration of an exercise to find a time in a user's day when
// the user could do an exercise
public class Scheduler {
  private final long exerciseDuration;

  public Scheduler(long exerciseDuration) {
    this.exerciseDuration =exerciseDuration;
  }
 
  // minSpan and maxSpan are the boundaries for when a workout could be scheduled on a particular day e.g 7:00 AM to 9:00 PM
  // events is the user's free/busy information.
  public Event GetFreeTime( EventDateTime minSpan, EventDateTime maxSpan, Collection<Event> events) {
    
    // initial capacity of eventsQueue is 1 larger than the size of events so that it can handle the case
    // where there are no events. 
    PriorityQueue<Event> eventQueue = new PriorityQueue<Event>((events.size() +1), new EventComparator());
    for (Event evt : events){
        eventQueue.add(evt);
    }
    
    EventDateTime now = minSpan;
    EventDateTime end = maxSpan;


    long exerciseMilliseconds = this.exerciseDuration * Time.minutesToMilliseconds;
    
    // if there is no space for an exercise between 'now' and the next event, then dequeue the first event.
    while (eventQueue.size() > 0 && Time.eventDateTimeToMilliseconds(eventQueue.peek().getStart()) - Time.eventDateTimeToMilliseconds(now) < (exerciseMilliseconds)) {
      now = eventQueue.poll().getEnd();
      }
    // once broken out of the loop (i.e the user has no more events),
    // check that there is enough time, and schedule at now. 
    if (Time.eventDateTimeToMilliseconds(now) > Time.eventDateTimeToMilliseconds(end) - (exerciseMilliseconds)) {
      return null;
     }  
    
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
