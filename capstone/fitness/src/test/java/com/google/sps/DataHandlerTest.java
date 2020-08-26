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

    String EMAIL = "hopethisisnottaken@gmail.com";
    String WORKOUT_ID = "running";
    Entity user = new Entity(dh.USER_ENTITY, EMAIL);
    Entity workout = new Entity(dh.WORKOUT_ENTITY, WORKOUT_ID);
    
    String NAME = "John Doe";
    int AGE = 18;
    String WORKOUT_LIST = "[\"running\"]";
    String CALENDAR_ID = "15";
    String EVENT_IDS = "[154, 264]";

    String WORKOUT_NAME = "running";
    String TYPE = "marathon";
    int WEEKS_TO_TRAIN = 5;
    String GOAL_STEPS = "[goalstep]";
    String PROGRESS = "[154,256]";
    float MARATHON_LENGTH = (float) 5.0;
    float INITIAL_TIME = (float) 2.0;
    float GOAL_TIME = (float) 1.0;
    float MILE_TIME = (float) 0.2;

    // Set user properties
    user.setProperty(dh.NAME_PROPERTY, NAME);
    user.setProperty(dh.AGE_PROPERTY, AGE);
    user.setProperty(dh.WORKOUT_LIST_PROPERTY, WORKOUT_LIST);
    user.setProperty(dh.CALENDAR_ID_PROPERTY, CALENDAR_ID);
    user.setProperty(dh.EVENT_IDS_PROPERTY, EVENT_IDS);
    
    // Set workout properties
    workout.setProperty(dh.WORKOUT_NAME_PROPERTY, WORKOUT_NAME);
    workout.setProperty(dh.TYPE_PROPERTY, TYPE);
    workout.setProperty(dh.WEEKS_TO_TRAIN_PROPERTY, WEEKS_TO_TRAIN);
    workout.setProperty(dh.GOAL_STEPS_PROPERTY, GOAL_STEPS);
    workout.setProperty(dh.PROGRESS_PROPERTY, PROGRESS);
    // Set up marathon properties
    workout.setProperty(dh.MARATHON_LENGTH_PROPERTY, MARATHON_LENGTH);
    workout.setProperty(dh.INITIAL_TIME_PROPERTY, INITIAL_TIME);
    workout.setProperty(dh.GOAL_TIME_PROPERTY, GOAL_TIME);
    workout.setProperty(dh.MILE_TIME_PROPERTY, MILE_TIME);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(user);
    datastore.put(workout);

    assertTrue(dh.getUserData(dh.NAME_PROPERTY, user).equals(NAME));
    assertTrue(dh.getUserData(dh.AGE_PROPERTY, user).equals(String.valueOf(AGE)));
    assertTrue(dh.getUserData(dh.WORKOUT_LIST_PROPERTY, user).equals(WORKOUT_LIST));
    assertTrue(dh.getUserData(dh.CALENDAR_ID_PROPERTY, user).equals(CALENDAR_ID));
    assertTrue(dh.getUserData(dh.EVENT_IDS_PROPERTY, user).equals(EVENT_IDS));


    assertTrue(dh.getWorkoutData(dh.WORKOUT_NAME_PROPERTY, workout).equals(WORKOUT_NAME));
    assertTrue(dh.getWorkoutData(dh.TYPE_PROPERTY, workout).equals(TYPE));
    assertTrue(dh.getWorkoutData(dh.WEEKS_TO_TRAIN_PROPERTY, workout).equals(String.valueOf(WEEKS_TO_TRAIN)));
    assertTrue(dh.getWorkoutData(dh.GOAL_STEPS_PROPERTY, workout).equals(GOAL_STEPS));
    assertTrue(dh.getWorkoutData(dh.PROGRESS_PROPERTY, workout).equals(PROGRESS));
    assertTrue(dh.getWorkoutData(dh.MARATHON_LENGTH_PROPERTY, workout).equals(String.valueOf(MARATHON_LENGTH)));
    assertTrue(dh.getWorkoutData(dh.INITIAL_TIME_PROPERTY, workout).equals(String.valueOf(INITIAL_TIME)));
    assertTrue(dh.getWorkoutData(dh.GOAL_TIME_PROPERTY, workout).equals(String.valueOf(GOAL_TIME)));
    assertTrue(dh.getWorkoutData(dh.MILE_TIME_PROPERTY, workout).equals(String.valueOf(MILE_TIME)));

    datastore.delete(user.getKey());
    datastore.delete(workout.getKey());
  }
}