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

/* Progress marker that holds an Exercise. */
public class GoalStep extends BananaNode {

  private final Exercise exercise;

  public GoalStep(Exercise exercise) {
    super();
    this.exercise = exercise;
  }

  /**
   * Returns hashmap of supplemental goal steps. 
   *
   * @return hashmap of supplemental goal steps.
   */
  public HashMap<String, PeelQueue> getSupplementalGoalSteps() {
    return getPeels();
  }

  public String getName() {
    return exercise.getName();
  }

  public Exercise getExercise() {
    return exercise;
  }

  @Override
  public String toString() {
    return String.format("Goal Step\nComplete? %b\n{\n%s}", isComplete(), exercise.toString());
  }
}
