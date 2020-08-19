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

package com.google.sps.fit;

import com.google.sps.fit.Exercise.SetType;
import com.google.sps.progress.*;
import java.util.*;

/* Json version of exercise. */
public class JsonExercise {

  private final String tag;
  private final String name;
  private final HashMap<SetType, float[]> setValues;

  public JsonExercise(String tag, GoalStep gs) {
    this(tag, gs.getMarker());
  }

  public JsonExercise(String tag, Exercise e) {
    this.tag = tag;
    name = e.getName();
    setValues = e.getSetValues();
  }

  public String getTag() {
    return tag;
  }

  public String getName() {
    return name;
  }

  public HashMap<SetType, float[]> getSetValues() {
    return setValues;
  }
}