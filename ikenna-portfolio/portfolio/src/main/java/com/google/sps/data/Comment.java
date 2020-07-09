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
  protected final String userid;

  public Comment(String text, long timestamp, String userid) {
    this("Anonymous", text, timestamp, userid);
  }

  public Comment(String name, String text, long timestamp, String userid) {
    this.id = -1;
    if(name == null || name.equals("")) {
        this.name = "Anonymous";
    }
    else {
        this.name = name.replaceAll("<[^>]*>", "Please Don't Inject HTML");
    }
    this.text = text.replaceAll("<[^>]*>", "Please Don't Inject HTML");
    this.timestamp = timestamp;
    this.userid = userid;
  }

  public Comment(Comment c) {
    this.id = c.id;
    this.name = c.name;
    this.text = c.text;
    this.timestamp = c.timestamp;  
    this.userid = c.userid;
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

  public String getUserId() {
    return userid;
  }
}
