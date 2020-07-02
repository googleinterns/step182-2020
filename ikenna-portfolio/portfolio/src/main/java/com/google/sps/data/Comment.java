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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Comment {
  public long id;
  protected final String name;
  protected final String text;
  protected final long timestamp;

  public Comment(String text, long timestamp) {
    this("Anonymous", text, timestamp);
  }

  public Comment(String name, String text, long timestamp) {
    this.id = -1;
    this.name = name;
    this.text = text;
    this.timestamp = timestamp;
  }

  public Comment(Comment c) {
    this.id = c.id;
    this.name = c.name;
    this.text = c.text;
    this.timestamp = c.timestamp;  
  }

  public String getName() {
    return name;
  }
  
  public String getText() {
    return text;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setId(long id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return String.format("Name: %s, Text: %s, Timestamp: %d", name, text, timestamp); 
  }
}
