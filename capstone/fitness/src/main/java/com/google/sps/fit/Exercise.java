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
import java.util.*;

/* Representation of exercise with specific quantitative data. */
public class Exercise implements Serializable {
  
  public enum SetType {
    DISTANCE,
    DURATION_INC,
    DURATION_DEC,
    REPS,
    WEIGHT;
    
    /**
     * Certain measurements for exercises want to decrease in number whereas others don't. This addresses that by
     * returning a "greater than" comparison for two numbers depending on if more of the stat is better.
     *
     * @param src starting float
     * @param comp float to compare to
     * @return if the starting float is "greater than" its comparison.
     */
    public boolean greaterThan(float src, float comp) {
      if(this.name().contains("_DEC")) {
        return src < comp;
      }
      return src > comp;
    }
  }

  /* Exercise name. */
  private String name;

  /* Sets per exercise. */
  private int sets;

  /* Set type mappings to values. */
  private HashMap<SetType, float[]> setValues;

  public Exercise(String name, SetType setType, float[] setTypeValues) {
    this(name, setType, null, setTypeValues, null);
  }

  public Exercise(String name, SetType setType1, SetType setType2, float[] setType1Values, float[] setType2Values) {
    this.name = name;
    this.sets = setType1Values.length;
    setValues = new HashMap<>();
    setValues.put(setType1, setType1Values);
    if(setType2 != null) {
      setValues.put(setType2, setType2Values);
    }
    if(!validateSetValuesLength()) {
      throw new ArithmeticException("Set values are different lengths.");
    } 
  }

  public Exercise(String name, HashMap<SetType, float[]> setValues) {
    this.name = name;
    int sets = 0;
    for(SetType type : setValues.keySet()) {
      sets = setValues.get(type).length;
      break;
    }
    this.sets = sets;
    this.setValues = setValues;
    if(!validateSetValuesLength()) {
      throw new ArithmeticException("Set values are different lengths.");
    } 
  }

  private boolean validateSetValuesLength() {
    float prevLength = -1;
    for(SetType type : setValues.keySet()) {
      if(prevLength == -1) {
        prevLength = getSetValues(type).length;
        continue;
      }
      if(prevLength != getSetValues(type).length) {
        return false;
      }
    } 
    return true;
  }

  /**
   * Returns true if average of Exercise values are greater than the given Exercise's average 
   * values in each type.
   * Note: Uses average to ignore set count.
   * 
   * @param exercise Exercise to compare to.
   * @return whether this Exercise's average values are greater than the given one's.
   */
  public boolean greaterThan(Exercise exercise) {
    for(SetType type : setValues.keySet()) {
      if(!type.greaterThan(avg(getSetValues(type)), avg(exercise.getSetValues(type)))) {
        return false;
      }
    }
    return true;
  }

  /**
   * Returns Optional object holding true if average of Exercise values are greater than the given Exercise's average values for 
   * the specific set type. An empty Optional object means the type wasn't in the set values hashmap or 0's were logged.
   * Note: Uses average to ignore set count.
   * 
   * @param exercise Exercise to compare to.
   * @param setType SetType to compare to.
   * @return whether this Exercise's average values are greater than the given one's for the specific set type.
   */
  public Optional<Boolean> greaterThan(Exercise exercise, SetType setType) {
    float srcAvg = avg(getSetValues(setType));
    float compAvg = avg(exercise.getSetValues(setType));
    Optional<Boolean> opt = setType.greaterThan(srcAvg, compAvg) ? opt = Optional.of(true) : Optional.of(false);
    
    //  If at least one is 0, then the type didn't exist or 0's were logged which is a user error.
    return srcAvg == 0 ? Optional.empty() : opt;
  }

  /**
   * Returns true if Exercise is equal to given Exercise in terms of average value.
   * Note: Uses average to ignore set count.
   * 
   * @param exercise Exercise to compare to.
   * @return whether this Exercise is equal to the given one.
   */
  public boolean equalTo(Exercise exercise) {
    for(SetType type : setValues.keySet()) {
      if(avg(getSetValues(type)) != avg(exercise.getSetValues(type))) {
        return false;
      }
    }
    return true;
  }

  /**
   * Returns Optional object holding true if Exercise is equal to given Exercise in terms of average value. 
   * An empty Optional object means the type wasn't in the set values hashmap or 0's were logged.
   * Note: Uses average to ignore set count.
   * 
   * @param exercise Exercise to compare to.
   * @param setType SetType to compare to.
   * @return whether this Exercise is equal to the given one.
   */
  public Optional<Boolean> equalTo(Exercise exercise, SetType setType) {
    float srcAvg = avg(getSetValues(setType));
    float compAvg = avg(exercise.getSetValues(setType));
    Optional<Boolean> opt = srcAvg == compAvg ? opt = Optional.of(true) : Optional.of(false);
    
    //  If at least one is 0, then the type didn't exist or 0's were logged which is a user error.
    return srcAvg == 0 ? Optional.empty() : opt;
  }

  private float avg(float[] src) {
    if(src == null) {
      return 0;
    }
    
    float sum = 0;
    for(int i = 0; i < src.length; i++) {
      sum += src[i];
    }
    return sum/src.length;
  }

  public int getSets() {
    return sets;
  }

  public HashMap<SetType, float[]> getSetValues() {
    return setValues;
  }

  /**
   * Return set type values for the given set type.
   * 
   * @param type SetType tag
   * @return set type values
   */
  public float[] getSetValues(SetType type) {
    return setValues.get(type);
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    String formattedSetValues = "";
    for(SetType type : setValues.keySet()) {
      formattedSetValues += String.format("Type: %s, Value: %s\n", type.name(), Arrays.toString(getSetValues(type)));
    }
    return String.format("Name: %s\nSets: %d\n%s", name, sets, formattedSetValues);
  }
}
