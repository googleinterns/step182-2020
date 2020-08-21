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

package com.google.sps.util;

import java.io.Serializable;

/**
* The Metadata class stores information that represents how the are arranged.
*/
public class Metadata implements Serializable { 

  private static final long serialVersionUID = -1908559582650257447L;

  /* Default Values */
  private static final int COUNT = 10;
  private static final int PAGE = 0;
  private static final Sort SORT = Sort.UNCOMPLETE;
  private static final int START_INDEX = 0;
  private static final int MAX_PAGES = 1;
  private static final int GOAL_STEPS = 0;
  
  public enum Sort {
    UNCOMPLETE, ALL, COMPLETE;
  }

  private final int count;
  private final Sort sort;
  private final String sortLabel;

  private int page;
  private int startIndex;
  private int maxPages;
  private int goalSteps;

  public Metadata() {
    this(COUNT, PAGE, SORT);
  }

  public Metadata(int count, int page, Sort sort) {
    this.count = count;
    this.page = page;
    this.sort = sort;
    this.sortLabel = sort.name();
    this.startIndex = START_INDEX;
    this.maxPages = MAX_PAGES;
    this.goalSteps = GOAL_STEPS;
  }

  public int getCount() {
    return count;
  }
  
  public int getPage() {
    return page;
  }

  public void setPage(int page) {
    this.page = page;
  }

  public Sort getSort() {
    return sort;
  }

  public void setMaxPages(int maxPages) {
    this.maxPages = maxPages;
  }

  public void setGoalSteps(int goalSteps) {
    this.goalSteps = goalSteps;
  }

  public void setStartIndex(int startIndex) {
    this.startIndex = startIndex;
  }
}