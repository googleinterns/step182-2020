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
 
package com.google.sps;
import com.google.sps.util.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Event;
import static org.hamcrest.Matchers.*;
 
public class SchedulerTest { 
  long duration = 30;
  Scheduler scheduler= new Scheduler(duration);
 
  
  public static long getMillisecondsBetween(EventDateTime a, EventDateTime b){
    return b.getDateTime().getValue() - a.getDateTime().getValue();
  }

  // tests a scenario where there is space after all of the events and before maxSpan. 
  @Test
  public void SpaceAfterEventsInWindow() {
    Collection<Event> events = new ArrayList<Event>();
 
    Event b = new Event();
    b.setStart(new EventDateTime().setDateTime(new DateTime("2020-08-10T10:00:00-04:00")));
    b.setEnd(new EventDateTime().setDateTime(new DateTime("2020-08-10T10:30:00-04:00")));
 
    Event e = new Event();
    e.setStart(new EventDateTime().setDateTime(new DateTime("2020-08-10T10:45:00-04:00")));
    e.setEnd(new EventDateTime().setDateTime(new DateTime("2020-08-10T13:30:00-04:00")));
   
    events.add(b);
    events.add(e);
    
    EventDateTime minSpan = new EventDateTime();
    minSpan.setDateTime(new DateTime("2020-08-10T10:00:00-04:00"));
 
    EventDateTime maxSpan = new EventDateTime();
    maxSpan.setDateTime(new DateTime("2020-08-11T00:00:00-04:00"));
 
    Event exerciseTime = this.scheduler.GetFreeTime(minSpan,maxSpan,events);
    
    //tests that exercise starts at the end of the last event.
    assertEquals(exerciseTime.getStart() ,e.getEnd());
    
    //tests that exercise ends 30 minutes after it starts. 
    assertEquals(this.getMillisecondsBetween(exerciseTime.getStart(),exerciseTime.getEnd()), duration * Time.minutesToMilliseconds);
    
    // tests that exercise is not after and maxSpan.
    assertThat(this.getMillisecondsBetween(exerciseTime.getEnd(),maxSpan), greaterThan(0L));
    
    // tests that exercise is not before and minSpan.
    // assertGreaterThan(Time.eventDateTimeToMilliseconds(exerciseTime.getStart()), Time.eventDateTimeToMilliseconds(minSpan));
    assertThat(this.getMillisecondsBetween(minSpan, exerciseTime.getStart()), greaterThan(0L));
  }
 
//   tests a scenario where there is space for an exercise between two events. 
  @Test
  public void SpaceBetweenTwoEvents() {
    Collection<Event> events = new ArrayList<Event>();
 
 
    Event a = new Event();
    a.setStart(new EventDateTime().setDateTime(new DateTime("2020-08-10T10:00:00-04:00")));
    a.setEnd(new EventDateTime().setDateTime(new DateTime("2020-08-10T10:30:00-04:00")));
 
    Event v = new Event();
    v.setStart(new EventDateTime().setDateTime(new DateTime("2020-08-10T12:45:00-04:00")));
    v.setEnd(new EventDateTime().setDateTime(new DateTime("2020-08-10T13:30:00-04:00")));
   
    events.add(a);
    events.add(v);
    
    EventDateTime minSpan = new EventDateTime();
    minSpan.setDateTime(new DateTime("2020-08-10T10:00:00-04:00"));
 
    EventDateTime maxSpan = new EventDateTime();
    maxSpan.setDateTime(new DateTime("2020-08-11T00:00:00-04:00"));
 
    Event exerciseTime = this.scheduler.GetFreeTime(minSpan,maxSpan,events);
    
    //tests that exercise starts at the end of the first event.
    assertTrue(exerciseTime.getStart().getDateTime().getValue() == a.getEnd().getDateTime().getValue());
    
    //tests that exercise ends 30 minutes after it starts. 
    assertTrue(exerciseTime.getEnd().getDateTime().getValue() == exerciseTime.getStart().getDateTime().getValue()+ (duration * 60000));
 
    // tests that exercise is not after and maxSpan.
    assertTrue(exerciseTime.getEnd().getDateTime().getValue() < maxSpan.getDateTime().getValue());
    
    // tests that exercise is not before and minSpan.
    assertTrue(exerciseTime.getStart().getDateTime().getValue() > minSpan.getDateTime().getValue());
 
  }
 
  // tests a scenario where the user has no events whatsoever.
  @Test
  public void NoEvents() {
    Collection<Event> events = new ArrayList<Event>();
    
    EventDateTime minSpan = new EventDateTime();
    minSpan.setDateTime(new DateTime("2020-08-10T10:00:00-04:00"));
 
    EventDateTime maxSpan = new EventDateTime();
    maxSpan.setDateTime(new DateTime("2020-08-11T00:00:00-04:00"));
 
    Event exerciseTime = this.scheduler.GetFreeTime(minSpan,maxSpan,events);
    // tests that the exercise begins at minSpan since the user has no events.
    assertTrue(exerciseTime.getStart().getDateTime().getValue() == minSpan.getDateTime().getValue());
  }
 
  // tests scenario where there's no space for events.  
  @Test
  public void NoSpace() {
    Collection<Event> events = new ArrayList<Event>();
    
    EventDateTime minSpan = new EventDateTime();
    minSpan.setDateTime(new DateTime("2020-08-10T10:00:00-04:00"));
 
    EventDateTime maxSpan = new EventDateTime();
    maxSpan.setDateTime(new DateTime("2020-08-11T00:00:00-04:00"));
    
 
    Event h = new Event();
    h.setStart(new EventDateTime().setDateTime(new DateTime("2020-08-10T10:10:00-04:00")));
    h.setEnd(new EventDateTime().setDateTime(new DateTime("2020-08-10T23:55:00-04:00")));
    events.add(h);
 
    Event exerciseTime = this.scheduler.GetFreeTime(minSpan,maxSpan,events);
    assertNull(exerciseTime);
  }
}
