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
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Event;
import com.google.sps.util.*;
import java.util.*;
import static org.hamcrest.Matchers.*;
import org.junit.*;
import static org.junit.Assert.*;


 
public class SchedulerTest { 
  long duration = 30;
  Scheduler scheduler= new Scheduler(duration);

    public static long getMillisecondsBetween(EventDateTime a, EventDateTime b){
      return b.getDateTime().getValue() - a.getDateTime().getValue();
  }

  // Tests a scenario where there is space after all of the events and before maxSpan. 
  @Test
  public void spaceAfterEventsInWindow() {
    Collection<Event> events = new ArrayList<Event>();
 
    Event b = new Event();
    b.setStart(new EventDateTime().setDateTime(new DateTime("2020-08-10T10:00:00-04:00")));
    b.setEnd(new EventDateTime().setDateTime(new DateTime("2020-08-10T10:30:00-04:00")));
 
    Event e = new Event();
    e.setStart(new EventDateTime().setDateTime(new DateTime("2020-08-10T10:45:00-04:00")));
    e.setEnd(new EventDateTime().setDateTime(new DateTime("2020-08-10T13:30:00-04:00")));
   
    events.add(b);
    events.add(e);
    
    DateTime minSpan = new DateTime("2020-08-10T10:00:00-04:00");
    DateTime maxSpan = new DateTime("2020-08-11T00:00:00-04:00");

    Event exerciseTime = this.scheduler.getFreeTime(minSpan,maxSpan,events);
    
    //Tests that exercise starts at the end of the last event.
    assertEquals(exerciseTime.getStart() ,e.getEnd());
    
    //Tests that exercise ends 30 minutes after it starts. 
    assertEquals(this.getMillisecondsBetween(exerciseTime.getStart(),exerciseTime.getEnd()), duration * Time.minutesToMilliseconds);
    
    // Tests that exercise is not after and maxSpan.
    assertTrue(exerciseTime.getEnd().getDateTime().getValue() < maxSpan.getValue());

    
    // Tests that exercise is not before and minSpan.
    assertTrue(exerciseTime.getStart().getDateTime().getValue() > minSpan.getValue());

  }
 
//   Tests a scenario where there is space for an exercise between two events. 
  @Test
  public void spaceBetweenTwoEvents() {
    Collection<Event> events = new ArrayList<Event>();
 
 
    Event a = new Event();
    a.setStart(new EventDateTime().setDateTime(new DateTime("2020-08-10T10:00:00-04:00")));
    a.setEnd(new EventDateTime().setDateTime(new DateTime("2020-08-10T10:30:00-04:00")));
 
    Event v = new Event();
    v.setStart(new EventDateTime().setDateTime(new DateTime("2020-08-10T12:45:00-04:00")));
    v.setEnd(new EventDateTime().setDateTime(new DateTime("2020-08-10T13:30:00-04:00")));
   
    events.add(a);
    events.add(v);
    
    DateTime minSpan = new DateTime("2020-08-10T10:00:00-04:00");
    DateTime maxSpan = new DateTime("2020-08-11T00:00:00-04:00");
 
    Event exerciseTime = this.scheduler.getFreeTime(minSpan,maxSpan,events);
    
    //Tests that exercise starts at the end of the first event.
    assertTrue(exerciseTime.getStart().getDateTime().getValue() == a.getEnd().getDateTime().getValue());
    
    //Tests that exercise ends 30 minutes after it starts. 
    assertTrue(exerciseTime.getEnd().getDateTime().getValue() == exerciseTime.getStart().getDateTime().getValue()+ (duration * 60000));
 
    
    // Tests that exercise is not after and maxSpan.
    assertTrue(exerciseTime.getEnd().getDateTime().getValue() < maxSpan.getValue());

    
    // Tests that exercise is not before and minSpan.
    assertTrue(exerciseTime.getStart().getDateTime().getValue() > minSpan.getValue());
 
  }
 
  // Tests a scenario where the user has no events whatsoever.
  @Test
  public void noEvents() {
    Collection<Event> events = new ArrayList<Event>();
    
    DateTime minSpan = new DateTime("2020-08-10T10:00:00-04:00");
    DateTime maxSpan = new DateTime("2020-08-11T00:00:00-04:00");
 
    Event exerciseTime = this.scheduler.getFreeTime(minSpan,maxSpan,events);
    // Tests that the exercise begins at minSpan since the user has no events.
    assertTrue(exerciseTime.getStart().getDateTime().getValue() == minSpan.getValue());
  }
 
  // Tests scenario where there's no space for events.  
  @Test
  public void noSpace() {
    Collection<Event> events = new ArrayList<Event>();
    
    DateTime minSpan = new DateTime("2020-08-10T10:00:00-04:00");
    DateTime maxSpan = new DateTime("2020-08-11T00:00:00-04:00");
    
 
    Event h = new Event();
    h.setStart(new EventDateTime().setDateTime(new DateTime("2020-08-10T10:10:00-04:00")));
    h.setEnd(new EventDateTime().setDateTime(new DateTime("2020-08-10T23:55:00-04:00")));
    events.add(h);
 
    Event exerciseTime = this.scheduler.getFreeTime(minSpan,maxSpan,events);
    assertNull(exerciseTime);
  }

  // Test for an all day event.
  @Test 
  public void allDay(){
    Collection<Event> events = new ArrayList<Event>();
    
    DateTime minSpan = new DateTime("2020-08-10T10:00:00-04:00");
    DateTime maxSpan = new DateTime("2020-08-11T00:00:00-04:00");
    
    
    Event k = new Event();
    k.setStart(new EventDateTime().setDate(new DateTime("2020-08-10")));
    events.add(k);

    Event t = new Event();
    t.setStart(new EventDateTime().setDateTime(new DateTime("2020-08-10T10:10:00-04:00"))); 
    t.setEnd(new EventDateTime().setDateTime(new DateTime("2020-08-10T10:45:30-04:00")));
    events.add(t);

    Event exerciseTime = this.scheduler.getFreeTime(minSpan, maxSpan, events);

    assertTrue(exerciseTime.getStart().getDateTime().getValue()==t.getEnd().getDateTime().getValue());
     }
  
  // Test for overlapping events where one starts and ends within the duration of the other.
  @Test 
  public void overlapOne(){
    Collection<Event> events = new ArrayList<Event>();
    
    DateTime minSpan = new DateTime("2020-08-10T10:00:00-04:00");
    DateTime maxSpan = new DateTime("2020-08-11T00:00:00-04:00");
    
    Event b = new Event();
    b.setStart(new EventDateTime().setDateTime(new DateTime("2020-08-10T10:00:00-04:00")));
    b.setEnd(new EventDateTime().setDateTime(new DateTime("2020-08-10T12:30:00-04:00")));
 
    Event e = new Event();
    e.setStart(new EventDateTime().setDateTime(new DateTime("2020-08-10T10:45:00-04:00")));
    e.setEnd(new EventDateTime().setDateTime(new DateTime("2020-08-10T11:30:00-04:00")));
   
    Event p = new Event();
    p.setStart(new EventDateTime().setDateTime(new DateTime("2020-08-10T14:45:00-04:00")));
    p.setEnd(new EventDateTime().setDateTime(new DateTime("2020-08-10T15:00:00-04:00")));
    
    events.add(b);
    events.add(e);
    events.add(p);


    Event exerciseTime = this.scheduler.getFreeTime(minSpan, maxSpan, events);
    
    assertTrue(exerciseTime.getStart().getDateTime().getValue() == b.getEnd().getDateTime().getValue());
    
    //Tests that exercise ends 30 minutes after it starts. 
    assertTrue(exerciseTime.getEnd().getDateTime().getValue() == exerciseTime.getStart().getDateTime().getValue()+ (duration * 60000));
 
    
    // Tests that exercise is not after and maxSpan.
    assertTrue(exerciseTime.getEnd().getDateTime().getValue() < maxSpan.getValue());

    
    // Tests that exercise is not before and minSpan.
    assertTrue(exerciseTime.getStart().getDateTime().getValue() > minSpan.getValue());

     }

  // Test for overlapping events where one starts during another and ends after the first one ends.
  @Test 
  public void overlapTwo(){
    Collection<Event> events = new ArrayList<Event>();
    
    DateTime minSpan = new DateTime("2020-08-10T10:00:00-04:00");
    DateTime maxSpan = new DateTime("2020-08-11T00:00:00-04:00");
    
    

    Event j = new Event();
    j.setStart(new EventDateTime().setDateTime(new DateTime("2020-08-10T10:10:00-04:00"))); 
    j.setEnd(new EventDateTime().setDateTime(new DateTime("2020-08-10T10:45:30-04:00")));
    events.add(j);

    Event t = new Event();
    t.setStart(new EventDateTime().setDateTime(new DateTime("2020-08-10T10:30:00-04:00"))); 
    t.setEnd(new EventDateTime().setDateTime(new DateTime("2020-08-10T11:00:30-04:00")));
    events.add(t);


    Event exerciseTime = this.scheduler.getFreeTime(minSpan, maxSpan, events);
    
    assertTrue(exerciseTime.getStart().getDateTime().getValue() == t.getEnd().getDateTime().getValue());
    
    //Tests that exercise ends 30 minutes after it starts. 
    assertTrue(exerciseTime.getEnd().getDateTime().getValue() == exerciseTime.getStart().getDateTime().getValue()+ (duration * 60000));
 
    
    // Tests that exercise is not after and maxSpan.
    assertTrue(exerciseTime.getEnd().getDateTime().getValue() < maxSpan.getValue());

    
    // Tests that exercise is not before and minSpan.
    assertTrue(exerciseTime.getStart().getDateTime().getValue() > minSpan.getValue());
     }
  
  // Test for overlapping events where two events start at the same time but have different end times.
  @Test 
  public void overlapThree(){
    Collection<Event> events = new ArrayList<Event>();
    
    DateTime minSpan = new DateTime("2020-08-10T10:00:00-04:00");
    DateTime maxSpan = new DateTime("2020-08-11T00:00:00-04:00");
    
    Event t = new Event();
    t.setStart(new EventDateTime().setDateTime(new DateTime("2020-08-10T10:10:00-04:00"))); 
    t.setEnd(new EventDateTime().setDateTime(new DateTime("2020-08-10T10:45:30-04:00")));
    events.add(t);
    
    Event g = new Event();
    g.setStart(new EventDateTime().setDateTime(new DateTime("2020-08-10T10:10:00-04:00"))); 
    g.setEnd(new EventDateTime().setDateTime(new DateTime("2020-08-10T11:45:30-04:00")));
    events.add(g);
    Event exerciseTime = this.scheduler.getFreeTime(minSpan, maxSpan, events);

    assertTrue(exerciseTime.getStart().getDateTime().getValue()==g.getEnd().getDateTime().getValue());
     }
}