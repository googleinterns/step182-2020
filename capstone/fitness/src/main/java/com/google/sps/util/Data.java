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

import com.google.sps.fit.*;
import com.google.sps.progress.*;

/* Mock Data Handler. */
public class Data {

  protected final Session lastSession;
  protected final GoalStep[] goalSteps;
  protected final Exercise start;
  protected final Exercise goal;
  protected final int daysAvailable;

  public Data(Session lastSession, GoalStep[] goalSteps, Exercise start, Exercise goal, int daysAvailable) {
    this.lastSession = lastSession;
    this.goalSteps = goalSteps;
    this.start = start;
    this.goal = goal;
    this.daysAvailable = daysAvailable;
  }

  public Session getLastSession() {
    return lastSession;
  }

  public GoalStep[] getGoalSteps() {
    return goalSteps;
  }

  /**
   * Returns days available from the start to the goal inclusive.
   *
   * @return days available from the start to the goal inclusive.
   */
  public int getDaysAvailable() {
    return daysAvailable;
  }

  public Exercise getStart() {
    return start;
  }

  public Exercise getGoal() {
    return goal;
  }

}
