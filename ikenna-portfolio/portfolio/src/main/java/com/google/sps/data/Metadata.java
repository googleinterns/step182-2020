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

import java.io.Serializable;

/**
* The Metadata class stores information that represents how the comment container
* will look like.
*/
public class Metadata implements Serializable {

  private static final long serialVersionUID = 9118902220109880243L;     

  /* Default Values */
  private static final int COUNT = 10;
  private static final int PAGE = 0;
  private static final int MAX_PAGES = 1;
  private static final Sort SORT = Sort.OLDEST;

  public enum Sort {
    OLDEST("timestamp", true),
    NEWEST("timestamp", false),
    NAME_A_Z("name", true),
    NAME_Z_A("name", false);

    String attribute;
    boolean ascending;

    Sort(String attr, boolean asc) {
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
  protected final Sort sort;
  protected final String sortLabel;

  public Metadata() {
    this(COUNT, PAGE, MAX_PAGES, SORT);
  }

  public Metadata(int count, int page, int maxPages, Sort sort) {
    this.count = count;
    this.page = page;
    this.maxPages = maxPages;
    this.sort = sort;
    this.sortLabel = sort.name();
  }

  public Metadata(Metadata metadata) {
    this.count = metadata.count;
    this.page = metadata.page;
    this.maxPages = metadata.maxPages;
    this.sort = metadata.sort;
    this.sortLabel = metadata.sortLabel;
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

  public Sort getSort() {
    return sort;
  }
  
  public String getSortLabel() {
    return sortLabel;
  }
}
