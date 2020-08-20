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

  private final String name;
  private final String tag;
  private final HashMap<SetType, float[]> setValues;
  private final String exerciseString;

  public JsonExercise(String tag, GoalStep gs) {
    this(tag + String.format(" %s", (gs.isComplete() ? "Complete" : "")), gs.getMarker());
  }

  public JsonExercise(String tag, Exercise e) {
    this(e.getName(), tag, e.getSetValues(), e.toString().replaceAll("\n", "<br>"));
  }
  
  public JsonExercise(String name, String tag, HashMap<SetType, float[]> setValues, String exerciseString) {
    this.name = name;
    this.tag = tag;
    this.setValues = setValues;
    this.exerciseString = exerciseString;
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

  public String toString() {
    return String.format("%s\nTag: %s\n%s", name, tag, setValues);
  }
}