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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.mockito.Mockito;
import java.io.*;
import javax.servlet.http.*;
import org.junit.Test;
import com.google.sps.database.*;
import com.google.sps.data.Comment;

public class DatabaseFunctionsTest extends Mockito {

  @Test
  public void testDatabaseDeleteEntity() throws Exception {
    DatabaseInterface database = new MockDatabase();
    Comment comment = new Comment("Comment Name", "Comment Text", 0);
    Comment comment2 = new Comment("Comment Name2", "Comment Text2", 2);
    assertTrue(database.size() == 0);
    long id1 = database.storeEntity(comment);
    long id2 = database.storeEntity(comment2);
    assertTrue(database.size() == 2);
    database.deleteEntity(id1);
    assertTrue(database.size() == 1);
    assertTrue((long)database.getContents("", true, 0, 0).get(0).getProperty("timestamp") == 2);
  }

  @Test
  public void testDatabaseDeleteAllEntities() throws Exception {
    DatabaseInterface database = new MockDatabase();
    Comment comment = new Comment("Comment Name", "Comment Text", 0);
    assertTrue(database.size() == 0);
    database.storeEntity(comment);
    database.storeEntity(comment);
    database.storeEntity(comment);
    database.storeEntity(comment);
    assertTrue(database.size() == 4);
    database.deleteAllEntities();
    assertTrue(database.size() == 0);
  }
  
  @Test
  public void testDatabaseStoreEntityAndGetContents() throws Exception {
    DatabaseInterface database = new MockDatabase();
    Comment comment = new Comment("Comment Name", "Comment Text", 0);
    assertTrue(database.size() == 0);
    assertTrue(database.storeEntity(comment) != -1);
    assertTrue(database.size() == 1);
  }
}