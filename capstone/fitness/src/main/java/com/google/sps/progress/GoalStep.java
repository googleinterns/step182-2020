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
  private static final long serialVersionUID = -2478810042129355868L;     

  private final Exercise marker;

  public GoalStep(Exercise marker) {
    super();
    this.marker = marker;
  }

  public GoalStep(JsonGoalStep jgs) {
    super();
    this.marker = new Exercise.Builder(jgs.getName())
                      .addSetValues(jgs.getSetValues())
                      .build();
    setComplete(jgs.isComplete());
  }

  public Exercise getMarker() {
    return marker;
  }

  public String getName() {
    return marker.getName();
  }

  @Override
  public String toString() {
    return String.format("Goal Step\nComplete? %b\n{\n%s}", isComplete(), marker.toString());
  }
}
