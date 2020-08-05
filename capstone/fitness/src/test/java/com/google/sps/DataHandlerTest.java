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
  public void testGetDataProper() throws Exception {

    DataHandler dh = new DataHandler();

    Entity e = new Entity("user", "hopethisisnottaken@gmail.com");
    e.setProperty(dh.NAME_PROPERTY, "John");
    e.setProperty(dh.AGE_PROPERTY, 18);
    e.setProperty(dh.WEEKS_TO_TRAIN_PROPERTY, 5);
    e.setProperty(dh.MARATHON_LENGTH_PROPERTY, 5.0);
    e.setProperty(dh.INITIAL_TIME_PROPERTY, 2.0);
    e.setProperty(dh.GOAL_TIME_PROPERTY, 1.0);
    e.setProperty(dh.PROGRESS_PROPERTY, "[]");
    e.setProperty(dh.MILE_TIME_PROPERTY, 0.2);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(e);

    assertTrue(dh.getData(dh.NAME_PROPERTY, e).equals("John"));
    assertTrue(dh.getData(dh.AGE_PROPERTY, e).equals("18"));
    assertTrue(dh.getData(dh.WEEKS_TO_TRAIN_PROPERTY, e).equals("5"));
    assertTrue(dh.getData(dh.MARATHON_LENGTH_PROPERTY, e).equals("5.0"));
    assertTrue(dh.getData(dh.INITIAL_TIME_PROPERTY, e).equals("2.0"));
    assertTrue(dh.getData(dh.GOAL_TIME_PROPERTY, e).equals("1.0"));
    assertTrue(dh.getData(dh.PROGRESS_PROPERTY, e).equals("[]"));
    assertTrue(dh.getData(dh.MILE_TIME_PROPERTY, e).equals("0.2"));

    datastore.delete(e.getKey());
  }

  @Test
  public void testJsonfy() {
      DataHandler dh = new DataHandler();
      String test0 = dh.Jsonfy("prop0","4.0", true);
      assertTrue(test0.equals("\"prop0\":4.0"));
      String test1 = dh.Jsonfy("prop1","John", false);
      assertTrue(test1.equals("\"prop1\":\"John\""));
  }
}