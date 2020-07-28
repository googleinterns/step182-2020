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

public class ProgressModel extends BananaQueue {
  
  public boolean progressSupplementalMilestone(FitnessSet fSet) {
    return false;
  }
  
  public boolean progressMainMilestone(FitnessSet fSet) {
    if(getCurrentMainMilestone.avgGreaterThan(fSet) || this.fSet.equalTo(fSet)) {
      progressMain();
      return true;
    }
    return false;
  }

  // Method for considered alternatives
  public Milestone progressMain() {
    return (Milestone) dequeueBanana();
  }

  public Milestone getCurrentMainMilestone() {
    return (Milestone) peekBanana();
  }

  /**
   * Returns array representation of queue. [Currently deciding best approach]
   *
   * @return array representation of queue.
   */
  public BananaNode[] toArray() {
    return null;
  }

}
