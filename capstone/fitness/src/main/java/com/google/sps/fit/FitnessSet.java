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

import java.io.Serializable;

public class FitnessSet implements Serializable {
  
  protected String name;
  protected int sets;
  protected String setType1;
  protected String setType2;
  protected float[] setType1Values;
  protected float[] setType2Values;

  public FitnessSet(String name, int sets, String setType1, String setType2, float[] setType1Values, float[] setType2Values) {
    this.name = name;
    this.sets = sets;
    this.setType1 = setType1;
    this.setType2 = setType2;
    this.setType1Values = setType1Values;
    this.setType2Values = setType2Values;
  }

  /**
   * Returns true if FitnessSet average values are greater than greater the given FitnessSet’s average 
   * value in one type (types and name have to be the same)
   */
  public boolean avgGreaterThan(FitnessSet fs) {
    return fs.name.equals(name) &&
           fs.setType1.equals(setType1) &&
           ((fs.setType2 == null && setType2 == null) || fs.setType2.equals(setType2)) &&
           avg(setType1Values) > avg(fs.setType1Values) &&
           ((fs.setType2Values == null && setType2Values == null) || avg(setType2Values) > avg(fs.setType2Values));
  }
  
  protected float avg(float[] src) {
    if(src == null) {
      return 0;
    }
    float sum = 0;
    for(int i = 0; i < src.length; i++) {
      sum += src[i];
    }
    return sum/src.length;
  }

  /**
   * Returns true if FitnessSet is equal to given FitnessSet in terms of values, types, and name
   */
  public boolean equalTo(FitnessSet fs) {
    return fs.name.equals(name) &&
           fs.setType1.equals(setType1) &&
           ((fs.setType2 == null && setType2 == null) || fs.setType2.equals(setType2)) &&
           Arrays.equals(setType1Values, fs.setType1Values) &&
           Arrays.equals(setType2Values, fs.setType2Values);
  }

  public int getSets() {
    return sets;
  }

  public String getSetType(boolean type1) {
    return type1 ? setType1 : setType2;
  }

  public float[] getSetTypeValues(boolean type1) {
    return type1 ? setType1Values : setType2Values;
  }
}
