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
import com.google.sps.data.Comment;
import com.google.sps.data.Metadata.Sort;
import java.util.List;


public interface DatabaseInterface {
  public void deleteEntity(long id);
  public void deleteAllEntities();
  public List<Entity> getContents(Sort sort, int batchSize, int page);
  public long storeEntity(Comment c);
  public int size();
  public int getMaxPages(int batchSize);
}
