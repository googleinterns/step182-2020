// Need: 
// * comparisons
// * ways to increment randomly
// * ways to copy old (with boolean to increment randomly)
//   * might not work because it should head towards goal
//   * Maybe pseudo linear randomness

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

public class StaticFitnessSet extends FitnessSet {
  
  protected float setMin, setMax;
  protected float setType1Min, setType1Max;
  protected float setType2Min, setType2Max;

  public StaticFitnessSet(String name, int sets, SetType setType1, SetType setType2, float[] setType1Values, float[] setType2Values) {
    super(name, sets, setType1, setType2, setType1Values, setType2Values);
    setMin = 0;
    setMax = 1;
    setType1Min = 0;
    setType1Max = 1;
    setType2Min = 0;
    setType2Max = 1;
  }
}
