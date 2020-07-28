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

import com.google.sps.progress.*;

/* Mock Data Handler */
public class Data {

  private Session lastSession;
  private ProgressModel model;
  private Milestone start;
  private Milestone goal;

  public Data(Session lastSession, ProgressModel model, Milestone start, Milestone goal) {
    this.lastSession = lastSession;
    this.model = model;
    this.start = start;
    this.goal = goal;
  }

  public Session getLastSession() {
    return lastSession;
  }

  public ProgressModel getProgressModel() {
    return model;
  }

  public Milestone getCurrentMainMilestone() {
    if(model == null) {
      return null;
    }
    return model.getCurrentMainMilestone();
  }

  /*
  * Returns days available between the start and goal 
  */
  public int getDaysAvailable() {
    return 4;
  }

  public Milestone getStart() {
    return start;
  }

  public Milestone getGoal() {
    return goal;
  }

}
