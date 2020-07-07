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

package com.google.sps.data;

/**
* The Metadata class stores information that represents how the comment container
* will look like.
*/
public class Metadata {

  public enum Search {
    OLDEST("timestamp", true),
    NEWEST("timestamp", false),
    NAME_A_Z("name", true),
    NAME_Z_A("name", false);

    String attribute;
    boolean ascending;

    Search(String attr, boolean asc) {
      attribute = attr;
      ascending = asc;
    }

    public String getAttribute() {
      return attribute;
    }

    public boolean getAscending() {
      return ascending;
    }
  }

  protected final int count;
  protected final int page;
  protected int maxPages;
  protected final Search search;
  protected final String filterLabel;

  public Metadata() {
    this(10, 0, -1, Search.OLDEST);
  }

  public Metadata(int count, int page, int maxPages, Search search) {
    this.count = count;
    this.page = page;
    this.maxPages = maxPages;
    this.search = search;
    this.filterLabel = search.name();
  }

  public Metadata(Metadata metadata) {
    this.count = metadata.count;
    this.page = metadata.page;
    this.maxPages = metadata.maxPages;
    this.search = metadata.search;
    this.filterLabel = metadata.filterLabel;
  }

  public int getCount() {
    return count;
  }
  
  public int getPage() {
    return page;
  }
  
  public int getMaxPages() {
    return maxPages;
  }

  public void setMaxPages(int maxPages) {
    this.maxPages = maxPages;
  }

  public Search getSearch() {
    return search;
  }
  
  public String getFilterLabel() {
    return filterLabel;
  }
}
