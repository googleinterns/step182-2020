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
  @Override
  public void deleteEntity(long id) {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Key key = KeyFactory.createKey("Comment", id);
    datastore.delete(key);
  }

  @Override
  public void deleteAllEntities() {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query("Comment");
    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {
      long id = entity.getKey().getId();
      Key key = KeyFactory.createKey("Comment", id);
      datastore.delete(key);
    }
  }
  
  @Override
  public List<Entity> getContents(String sort_attr, boolean ascending, int batch_size, int page) {
    Query query = new Query("Comment");
    
    if(ascending) query.addSort(sort_attr, Query.SortDirection.ASCENDING);
    else query.addSort(sort_attr, Query.SortDirection.DESCENDING);
   
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    FetchOptions fetch_options = FetchOptions.Builder.withLimit(batch_size);
    Cursor cursor = null;
	int count  = 0;
    QueryResultList<Entity> result_list = null;

	do {
	  if(cursor != null)
		fetch_options.startCursor(cursor);		
	  result_list = datastore.prepare(query).asQueryResultList(fetch_options);
	  if (result_list.size() < batch_size)
	    break;
	  cursor = result_list.getCursor();
      count++;
	} while (count <= page);

    return result_list;
  }
  
  @Override
  public long storeEntity(Comment c) {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity comment_entity = new Entity("Comment");
    comment_entity.setProperty("name", c.getName());
    comment_entity.setProperty("text", c.getText());
    comment_entity.setProperty("timestamp", c.getTimestamp());
    return datastore.put(comment_entity).getId();
  }

  @Override
  public int size() {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query("Comment");
    PreparedQuery results = datastore.prepare(query);
    int counter = 0;
    for (Entity entity : results.asIterable()) counter++;
    return counter;
  }

  @Override
  public int getMaxPages(int batch_size) {
    int size = size();
    int remainder = size % batch_size != 0 ? 1 : 0;
    int page_count = (int)Math.floor((float)size/batch_size);
    System.out.println("Remainder: " + remainder);
    System.out.println("Page Count: " + page_count);    
    return page_count + remainder;
  }
}
