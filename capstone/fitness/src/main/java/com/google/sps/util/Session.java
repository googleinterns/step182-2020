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
import com.google.sps.fit.Exercise.SetType;
import java.io.Serializable;
import java.util.*;

/*
 * Mock Session object. 
 * Meant to hold workout done by the user during a scheduled event.
 */
public class Session implements Serializable {
  private static final long serialVersionUID = 208302144072029362L;     

  private Exercise[] workout;

  public Session(Exercise[] workout) {
    this.workout = workout;
  }

  public Session(MarathonSession ms, String workoutName) {
    Exercise exercise = new Exercise.Builder(workoutName)
                            .addSetTypeWithValues(SetType.DURATION_INC, new float[] {ms.getSpeed()})
                            .build();
    this.workout = new Exercise[] {exercise};
  }

  public Session(LiftingSession ls, String workoutName) {
    Exercise exercise = new Exercise.Builder(workoutName)
                            .addSetTypeWithValues(SetType.REPS, new float[] {ls.getReps()})
                            .addSetTypeWithValues(SetType.WEIGHT, new float[] {ls.getWeight()})
                            .build();
    this.workout = new Exercise[] {exercise};
  }

  public Exercise[] getWorkout() {
    return workout;
  } 

  public String toString() {
    return Arrays.toString(workout);
  }
}