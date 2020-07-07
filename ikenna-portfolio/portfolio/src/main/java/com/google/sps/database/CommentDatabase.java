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

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.sps.data.Comment;
import java.lang.*;
import java.util.List;

public class CommentDatabase implements DatabaseInterface {

  private final String COMMENT_TAG = "Comment"; 

  @Override
  public void deleteEntity(long id) {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Key key = KeyFactory.createKey(COMMENT_TAG, id);
    datastore.delete(key);
  }

  @Override
  public void deleteAllEntities() {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query(COMMENT_TAG);
    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {
      long id = entity.getKey().getId();
      Key key = KeyFactory.createKey(COMMENT_TAG, id);
      datastore.delete(key);
    }
  }
  
  @Override
  public List<Entity> getContents(String sortAttr, boolean ascending, int batchSize, int page) {
    Query query = new Query(COMMENT_TAG);
    
    if(ascending) {
      query.addSort(sortAttr, Query.SortDirection.ASCENDING);
    }
    else {
      query.addSort(sortAttr, Query.SortDirection.DESCENDING);
    }

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    FetchOptions fetchOptions = FetchOptions.Builder.withLimit(batchSize);
    Cursor cursor = null;
	int count  = 0;
    QueryResultList<Entity> resultList = null;
    
    /*Moves database cursor based on page and grabs up to batchSize elements from the cursor position*/
	do {
	  if(cursor != null)
		fetchOptions.startCursor(cursor);		
	  resultList = datastore.prepare(query).asQueryResultList(fetchOptions);
	  if (resultList.size() < batchSize)
	    break;
	  cursor = resultList.getCursor();
      count++;
	} while (count <= page);

    return resultList;
  }
  
  @Override
  public long storeEntity(Comment c) {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity commentEntity = new Entity(COMMENT_TAG);
    commentEntity.setProperty("name", c.getName());
    commentEntity.setProperty("text", c.getText());
    commentEntity.setProperty("timestamp", c.getTimestamp());
    return datastore.put(commentEntity).getId();
  }

  @Override
  public int size() {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query(COMMENT_TAG);
    PreparedQuery results = datastore.prepare(query);
    int counter = 0;
    for (Entity entity : results.asIterable()) counter++;
    return counter;
  }

  @Override
  public int getMaxPages(int batchSize) {
    int size = size();
    int remainder = size % batchSize != 0 ? 1 : 0;
    int pageCount = (int)Math.floor((float)size/batchSize);
    return pageCount + remainder;
  }
}
