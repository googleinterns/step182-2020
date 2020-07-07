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
* 
*  
*/
public class Metadata {
  protected final int count;
  protected final int page;
  protected int maxPages;
  protected final boolean ascending;
  protected final String search;
  protected final String filterLabel;

  public Metadata() {
    this(10, 0, -1, true, "timestamp");
  }

  public Metadata(int count, int page, int maxPages, boolean ascending, String search) {
    this.count = count;
    this.page = page;
    this.maxPages = maxPages;
    this.ascending = ascending;
    this.search = search;
    this.filterLabel = getFilter(ascending, search);
  }

  public Metadata(Metadata metadata) {
    this.count = metadata.count;
    this.page = metadata.page;
    this.maxPages = metadata.maxPages;
    this.ascending = metadata.ascending;
    this.search = metadata.search;
    this.filterLabel = metadata.filterLabel;
  }

  private String getFilter(boolean ascending, String search) {
    if(ascending) {
      if(search.equals("timestamp")) {
        return "Oldest";
      }
      if(search.equals("name")) {
        return "Name A-Z";
      }
    }
    else {
      if(search.equals("timestamp")) {
        return "Newest";
      }
      if(search.equals("name")) {
        return "Name Z-A";
      } 
    }
    return "";
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

  public boolean getAscending() {
    return ascending;
  }
  
  public String getSearch() {
    return search;
  }
  
  public String getFilterLabel() {
    return filterLabel;
  }
}
