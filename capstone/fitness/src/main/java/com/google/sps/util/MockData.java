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
import com.google.sps.progress.*;

public class MockData extends Data {

  public MockData(GoalStep goalStep) {
    super(null, goalStep, null, null, 0);
  }

  @Override
  public Session getLastSession() {
    return new Session(new Exercise[] {goalStep.getMarker()});
  }

  @Override
  public int getDaysAvailable() {
    return 15;
  }

  @Override
  public Exercise getStart() {
    return new Exercise.Builder("test")
        .addSetTypeWithValues(SetType.DISTANCE, new float[] {2})
        .addSetTypeWithValues(SetType.DURATION_DEC, new float[] {600})
        .build();
  }

  @Override
  public Exercise getGoal() {
    return new Exercise.Builder("test")
        .addSetTypeWithValues(SetType.DISTANCE, new float[] {4, 4})
        .addSetTypeWithValues(SetType.DURATION_DEC, new float[] {300, 300})
        .build();
  }

}
