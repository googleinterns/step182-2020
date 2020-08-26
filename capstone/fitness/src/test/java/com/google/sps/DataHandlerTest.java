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

import org.junit.*;
import static org.junit.Assert.*;
import com.google.sps.util.*;
import java.util.Objects;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class DataHandlerTest {

  private final LocalServiceTestHelper helper =
    new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  @Before
  public void setUp() {
    helper.setUp();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void testGetData() throws Exception {

    DataHandler dh = new DataHandler();

    Entity user = new Entity(dh.USER_ENTITY, "hopethisisnottaken@gmail.com");
    Entity workout = new Entity(dh.WORKOUT_ENTITY, "running");
    
    // Set user properties
    user.setProperty(dh.NAME_PROPERTY, "John");
    user.setProperty(dh.AGE_PROPERTY, 18);
    user.setProperty(dh.WORKOUT_LIST_PROPERTY, "[\"running\"]");
    user.setProperty(dh.CALENDAR_ID_PROPERTY, "15");
    user.setProperty(dh.EVENT_IDS_PROPERTY, "[154,256]");
    
    // Set workout properties
    workout.setProperty(dh.WORKOUT_NAME_PROPERTY, "running");
    workout.setProperty(dh.TYPE_PROPERTY, "marathon");
    workout.setProperty(dh.WEEKS_TO_TRAIN_PROPERTY, 5);
    workout.setProperty(dh.GOAL_STEPS_PROPERTY, "[goalstep]");
    workout.setProperty(dh.PROGRESS_PROPERTY, "[I am speed]");
    // Set up marathon properties
    workout.setProperty(dh.MARATHON_LENGTH_PROPERTY, 5.0);
    workout.setProperty(dh.INITIAL_TIME_PROPERTY, 2.0);
    workout.setProperty(dh.GOAL_TIME_PROPERTY, 1.0);
    workout.setProperty(dh.MILE_TIME_PROPERTY, 0.2);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(user);
    datastore.put(workout);

    assertTrue(dh.getUserData(dh.NAME_PROPERTY, user).equals("John"));
    assertTrue(dh.getUserData(dh.AGE_PROPERTY, user).equals("18"));
    assertTrue(dh.getUserData(dh.WORKOUT_LIST_PROPERTY, user).equals("[\"running\"]"));
    assertTrue(dh.getUserData(dh.CALENDAR_ID_PROPERTY, user).equals("15"));
    assertTrue(dh.getUserData(dh.EVENT_IDS_PROPERTY, user).equals("[154,256]"));

    assertTrue(dh.getWorkoutData(dh.WORKOUT_NAME_PROPERTY, workout).equals("running"));
    assertTrue(dh.getWorkoutData(dh.TYPE_PROPERTY, workout).equals("marathon"));
    assertTrue(dh.getWorkoutData(dh.WEEKS_TO_TRAIN_PROPERTY, workout).equals("5"));
    assertTrue(dh.getWorkoutData(dh.GOAL_STEPS_PROPERTY, workout).equals("[goalstep]"));
    assertTrue(dh.getWorkoutData(dh.PROGRESS_PROPERTY, workout).equals("[I am speed]"));
    assertTrue(dh.getWorkoutData(dh.MARATHON_LENGTH_PROPERTY, workout).equals("5.0"));
    assertTrue(dh.getWorkoutData(dh.INITIAL_TIME_PROPERTY, workout).equals("2.0"));
    assertTrue(dh.getWorkoutData(dh.GOAL_TIME_PROPERTY, workout).equals("1.0"));
    assertTrue(dh.getWorkoutData(dh.MILE_TIME_PROPERTY, workout).equals("0.2"));

    datastore.delete(user.getKey());
    datastore.delete(workout.getKey());
  }
}