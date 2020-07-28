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

package com.google.sps.progress;

import com.google.sps.util.*;
import java.util.*;

public class Milestone extends BananaNode {

  private final int level;
  private final FitnessSet fSet;

  // Variable for considered alternatives
  private final ProgressModel model;

  public Milestone(int level, FitnessSet fSet, ProgressModel model) {
    this.level = level;
    this.fSet = fSet;
    this.model = model;
  }

  // Method for considered alternatives
  public boolean progressSupplementalMilestone(FitnessSet fSet) {
    return false;
  }

  // Method for considered alternatives
  public Milestone progressMainMilestone(FitnessSet fSet) {
    if(this.fSet.avgGreaterThan(fSet) || this.fSet.equalTo(fSet)) {
      model.progressMain();
      return model.getCurrentMainMilestone();
    }
    return this;
  }

  public HashMap<String, SupplementalMilestone> getSupplementalMilestones() {
    return null;
  }
  
  public int getLevel() {
    return level;
  }

}
