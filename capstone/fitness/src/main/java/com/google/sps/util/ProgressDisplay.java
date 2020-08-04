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

import com.google.sps.fit.Exercise.SetType;
import com.google.sps.progress.*;
import java.util.*;

public class ProgressDisplay {

  private final String name;
  private final boolean complete;
  private final SetType[] setTypes;
  private final float[][] setTypeValues;

  public ProgressDisplay(GoalStep gs) {
    this.name = gs.getName();
    this.complete = gs.isComplete();
    Set<SetType> types = gs.getExercise().getSetValues().keySet();
    SetType[] setTypes = new SetType[types.size()];
    float[][] setTypeValues = new float[setTypes.length][gs.getExercise().getSets()];
    int i = 0;
    for(SetType setType : types) {
      setTypes[i] = setType;
      setTypeValues[i] = gs.getExercise().getSetValues(setType);
      i++;
    }  
    this.setTypes = setTypes;
    this.setTypeValues = setTypeValues;
  }
}
