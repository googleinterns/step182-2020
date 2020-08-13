// // Copyright 2019 Google LLC
// //
// // Licensed under the Apache License, Version 2.0 (the "License");
// // you may not use this file except in compliance with the License.
// // You may obtain a copy of the License at
// //
// //     https://www.apache.org/licenses/LICENSE-2.0
// //
// // Unless required by applicable law or agreed to in writing, software
// // distributed under the License is distributed on an "AS IS" BASIS,
// // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// // See the License for the specific language governing permissions and
// // limitations under the License.

// package com.google.sps;
// import com.google.sps.util.*;
// import java.util.*;
// import org.junit.*;
// import static org.junit.Assert.*;


// public class EasySchedulerTest { 
//   EasyScheduler scheduler= new EasyScheduler(30);

  
//   // tests a scenario where there is space after all of the events and before maxSpan. 
//   @Test
//   public void SpaceAfterEventsInWindow() {
//     Collection<EasyEvent> events = new ArrayList<EasyEvent>();
//     events.add(new EasyEvent(15, 50));
//     events.add(new EasyEvent(70,100));

//     EasyEvent time = this.scheduler.GetFreeTime(10,200,events);
//     assertTrue(time.getStart() == 100 );
//     assertTrue(time.getEnd() == 130);
//   }

//   // tests a scenario where there is space for an exercise between two events. 
//   @Test
//   public void SpaceBetweenTwoEvents() {
//     Collection<EasyEvent> events = new ArrayList<EasyEvent>();
//     events.add(new EasyEvent(15, 50));
//     events.add(new EasyEvent(60,110));
//     events.add(new EasyEvent(150,180)); 
//     events.add(new EasyEvent(190,200));
    
//     EasyEvent time = this.scheduler.GetFreeTime(10,200,events);
//     assertTrue(time.getStart() == 110 );
//     assertTrue(time.getEnd() == 140);
//   }

//   // tests a scenario where the user has no events whatsoever.
//   @Test
//   public void NoEvents() {
//     Collection<EasyEvent> events = new ArrayList<EasyEvent>();
    
//     EasyEvent time = this.scheduler.GetFreeTime(10,200,events);
//     assertTrue(time.getStart() == 10 );
//     assertTrue(time.getEnd() == 40);
//   }
  
//   // tests a scenario where the user has two events scheduled at the same time with different durations.
//   @Test
//   public void TravelBackCase() {
//     Collection<EasyEvent> events = new ArrayList<EasyEvent>();
//     events.add(new EasyEvent(15, 50));
//     events.add(new EasyEvent(60,110));
//     events.add(new EasyEvent(60,70)); 
//     events.add(new EasyEvent(130,200));
//     EasyEvent time = this.scheduler.GetFreeTime(10,200,events);
//     assertTrue(time.getStart() == 70);
//     assertTrue(time.getEnd() == 100);
//   }

//   // tests scenario where there's no space for events.  
//   @Test
//   public void NoSpace() {
//     Collection<EasyEvent> events = new ArrayList<EasyEvent>();
//     events.add(new EasyEvent(15, 50));
//     events.add(new EasyEvent(60,110));
//     events.add(new EasyEvent(130,200));
//     EasyEvent time = this.scheduler.GetFreeTime(10,200,events);
//     assertTrue(time.getDescription() == "no space");
//   }
// }

