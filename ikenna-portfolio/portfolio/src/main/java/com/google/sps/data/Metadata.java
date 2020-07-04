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

public class Metadata {
  private final int count;
  private final int page;
  private final int max_pages;
  private final boolean ascending;
  private final String search;

  public Metadata(int count, int page, int max_pages, boolean ascending, String search) {
    this.count = count;
    this.page = page;
    this.max_pages = max_pages;
    this.ascending = ascending;
    this.search = search;
  }
}
