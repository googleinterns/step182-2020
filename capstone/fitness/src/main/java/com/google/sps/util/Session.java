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
import java.io.Serializable;

/*
 * Mock Session object. 
 * Meant to hold workout done by the user during a scheduled event.
 */
public class Session implements Serializable {

  private Exercise[] workout;

  public Session(Exercise[] workout) {
    this.workout = workout;
  }

  public Exercise[] getWorkout() {
    return workout;
  } 
}
