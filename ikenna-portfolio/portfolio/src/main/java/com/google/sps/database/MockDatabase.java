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

package com.google.sps.database;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.sps.data.Comment;
import java.lang.*;
import java.util.*;

public class MockDatabase implements DatabaseInterface {
  private List<Entity> comments = new ArrayList<>();
  private int size = 0;
  @Override
  public void deleteEntity(long id) {
    if((int)id > size || comments.get((int)id) == null) return;
    comments.set((int)id, null);
    size--;
  }

  @Override
  public void deleteAllEntities() {
    comments.clear();
    size = 0;
  }
  
  @Override
  public List<Entity> getContents(String sortAttr, boolean ascending, int batchSize, int page) {
    List<Entity> results = new ArrayList<>();
    for(Entity e : comments)
      if(e != null)
        results.add(e); 
    return results;
  }
  
  @Override
  public long storeEntity(Comment c) {
    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("name", c.getName());
    commentEntity.setProperty("text", c.getText());
    commentEntity.setProperty("timestamp", c.getTimestamp());
    comments.add(commentEntity);
    size++;     
    return comments.size() - 1;
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public int getMaxPages(int batchSize) {
    int remainder = size % batchSize != 0 ? 1 : 0;
    int pageCount = (int)Math.floor((float)size/batchSize);
    return pageCount + remainder;
  }
}
