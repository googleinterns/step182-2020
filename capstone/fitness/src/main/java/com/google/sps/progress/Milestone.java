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

import com.google.sps.fit.*;
import com.google.sps.util.*;
import java.util.*;

/* Progress marker that holds a FitnessSet. */
public class Milestone extends BananaNode {

  private final FitnessSet fSet;

  public Milestone(FitnessSet fSet) {
    super();
    this.fSet = fSet;
  }

  /**
   * Returns hashmap of supplemental milestones. 
   * Note: Entities obtained from hashmap have to be casted.
   *
   * @return hashmap of supplemental milestones.
   */
  public HashMap<String, PeelQueue> getSupplementalMilestones() {
    return getPeels();
  }

  public String getName() {
    return fSet.getName();
  }

  public FitnessSet getFitnessSet() {
    return fSet;
  }

  @Override
  public String toString() {
    return String.format("Milestone\nComplete? %b\n{\n%s}", isComplete(), fSet.toString());
  }
}
