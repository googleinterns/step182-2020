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

public class ProgressModel extends BananaQueue {

  public boolean progressSupplementalMilestone(FitnessSet fSet) {
    return false;
  }
  
  /**
   * Advances to the next milestone if the given fitness set is greater on average than the current
   * milestone or if the given fitness set is equal to the current milestone.
   *
   * @param fSet FitnessSet to compare to.
   * @return if the operation was successful.
   */
  public boolean progressMainMilestone(FitnessSet fSet) {
    FitnessSet currentFSet = ((Milestone) peekBanana()).getFitnessSet();
    if(fSet.avgGreaterThan(currentFSet) || fSet.equalTo(currentFSet)) {
      progressMain();
      return true;
    }
    return false;
  }

  public boolean addMainMilestone(Milestone milestone) {
    return enqueueBanana(milestone);
  }

  public Milestone progressMain() {
    return (Milestone) dequeueBanana();
  }

  public Milestone getCurrentMainMilestone() {
    return (Milestone) peekBanana();
  }

  /**
   * Array representation of all main milestones in the progress model.
   * 
   * @return array representation of all main milestones in the progress model.
   */
  public Milestone[] toArray() {
    int length = getSize();
    Milestone firstMilestone = getCurrentMainMilestone();
    while(firstMilestone.getPrev() != null) {
      firstMilestone = (Milestone) firstMilestone.getPrev();
      length++;
    }
    Milestone[] milestones = new Milestone[length];
    for(int i = 0; i < length; i++) {
      milestones[i] = firstMilestone;
      firstMilestone = (Milestone) firstMilestone.getNext();
    }
    return milestones;
  }

  @Override
  public String toString() {
    String str = String.format("Progress Model For %s\nSize: %d\n", getCurrentMainMilestone().getName(), getSize());
    Milestone[] arr = toArray();
    for(int i = 0; i < arr.length; i++) {
      str += arr[i] + "\n\n";
    }
    return str;
  }
}
