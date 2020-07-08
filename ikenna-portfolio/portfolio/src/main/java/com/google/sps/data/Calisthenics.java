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

package com.google.sps.data;

import java.io.Serializable;

/**
* The Calisthenics class stores information that represents how the calisthenics container
* will look like.
*/
public class Calisthenics implements Serializable {
  private final String goalImg;
  private final String currentAblityImg;
  private final String goalName;
  private final String currentAblityName;

  public Calisthenics(String goalImg, String currentAblityImg, String goalName, String currentAblityName) {
    this.goalImg = goalImg;
    this.currentAblityImg = currentAblityImg;
    this.goalName = goalName;
    this.currentAblityName = currentAblityName;
  }
}
