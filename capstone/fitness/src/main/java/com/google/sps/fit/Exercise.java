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
    DURATION_DEC,
    DURATION_INC,
    REPS,
    WEIGHT;
    
    public boolean isDec() {
      return this.name().contains("_DEC");
    }

    /**
     * Certain measurements for exercises want to decrease in number whereas others don't. This addresses that by
     * returning a "better than" comparison for two numbers depending on if more of the stat is better.
     *
     * @param src starting float
     * @param comp float to compare to
     * @return if the starting float is better than its comparison.
     */
    public boolean betterThan(float src, float comp) {
      if(isDec()) {
        return src < comp;
      }
      return src > comp;
    }
  }

  /* Exercise name. */
  private String name;

  /* Set count per exercise. */
  private int setCount;

  /* Set type mappings to values. */
  private HashMap<SetType, float[]> setValues;

  public static class Builder {
    private final String name;
    private final HashMap<SetType, float[]> setValues;
    private int setCount;

    public Builder(String name) {
      this.name = name;
      this.setValues = new HashMap<>();
      setCount = 0;
    }

    public Builder addSetTypeWithValues(SetType type, float[] values) {
      if(setValues.isEmpty()) {
        setCount = values.length;
      }

      if(setCount != values.length) {
        throw new ArithmeticException("Set values are different lengths.");
      }
      
      if(type == null || values == null) {
        throw new NullPointerException("Type or values is null.");
      }
      
      setValues.put(type, values);
      return this;
    }

    public Builder addSetValues(HashMap<SetType, float[]> setValues) {
      if(setValues == null) {
        throw new NullPointerException("Type or values is null.");
      }
      
      for(SetType type : setValues.keySet()) {
        float[] values = setValues.get(type);
        
        if(this.setValues.isEmpty()) {
          setCount = values.length;
        }

        if(setCount != values.length) {
          throw new ArithmeticException("Set values are different lengths.");
        }
        
        this.setValues.put(type, values);
      }
      return this;
    }

    public Exercise build() {
      Exercise exercise = new Exercise();
      exercise.name = name;
      exercise.setCount = setCount;
      exercise.setValues = setValues;
      return exercise;
    }
  }

  private Exercise(){}

  /**
   * Returns true if Exercise's values are better than or overall equal to the given Exercise's 
   * values in each type. Uses average if values should decrement, uses summation if not. The equal
   * to demonstrates that an exercise cannot become better in all ways.
   * 
   * @param exercise Exercise to compare to.
   * @return whether this Exercise's average values are better than the given one's.
   */
  public boolean betterThan(Exercise exercise) {
    boolean betterThan = true;
    for(SetType type : setValues.keySet()) {
      float src = avg(getSetValues(type));
      float comp = avg(exercise.getSetValues(type));
      if(!type.isDec()) {
        src *= getSetValues(type).length;
        comp *= exercise.getSetValues(type).length;
      } 
      betterThan &= (type.betterThan(src, comp) || equalTo(exercise, type).orElse(false));
    }
    return betterThan;
  }

  /**
   * Returns Optional object holding true if average of Exercise values are better than the given Exercise's average values for 
   * the specific set type. An empty Optional object means the type wasn't in the set values hashmap or 0's were logged.
   * 
   * @param exercise Exercise to compare to.
   * @param setType SetType to compare to.
   * @return whether this Exercise's average values are better than the given one's for the specific set type.
   */
  public Optional<Boolean> betterThan(Exercise exercise, SetType setType) {
    float srcAvg = avg(getSetValues(setType));
    float compAvg = avg(exercise.getSetValues(setType));
    boolean comparison = setType.betterThan(srcAvg, compAvg);
    Optional<Boolean> opt = comparison ? opt = Optional.of(true) : Optional.of(false);
    
    //  If at least one is 0, then the type didn't exist or 0's were logged which is a user error.
    return srcAvg == 0 ? Optional.empty() : opt;
  }

  /**
   * Returns true if the summation of Exercise values are equal to the given Exercise's summation 
   * values in each type.
   * 
   * @param exercise Exercise to compare to.
   * @return whether this Exercise's average values are equal to the given one's.
   */
  public boolean equalTo(Exercise exercise) {
    for(SetType type : setValues.keySet()) {
      float srcSum = sum(getSetValues(type));
      float compSum = sum(exercise.getSetValues(type));
      if(srcSum != compSum) {
        return false;
      }
    }
    return true;
  }

  /**
   * Returns Optional object holding true if summation of Exercise values are equal to the given Exercise's summation values for 
   * the specific set type. An empty Optional object means the type wasn't in the set values hashmap or 0's were logged.
   * 
   * @param exercise Exercise to compare to.
   * @param setType SetType to compare to.
   * @return whether this Exercise's average values are equal to the given one's for the specific set type.
   */
  public Optional<Boolean> equalTo(Exercise exercise, SetType setType) {
    float srcSum = sum(getSetValues(setType));
    float compSum = sum(exercise.getSetValues(setType));
    boolean comparison = srcSum == compSum;
    Optional<Boolean> opt = comparison ? opt = Optional.of(true) : Optional.of(false);
    
    //  If at least one is 0, then the type didn't exist or 0's were logged which is a user error.
    return srcSum == 0 ? Optional.empty() : opt;
  }

  private float avg(float[] src) {
    return sum(src)/src.length;
  }

  private float sum(float[] src) {
    if(src == null) {
      return 0;
    }
    
    float sum = 0;
    for(int i = 0; i < src.length; i++) {
      sum += src[i];
    }
    return sum;
  }

  public int getSetCount() {
    return setCount;
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

  public float[][] getPairedValues() {
    float[][] pairedValues = new float[setCount][setValues.size()];
    Set<SetType> types = setValues.keySet();
    for(int i = 0; i < setCount; i++) {
      int j = 0;
      for(SetType type : types) {
        pairedValues[i][j] = getSetValues(type)[i];
        j++;
      }
    }
    return pairedValues;
  }

  @Override
  public String toString() {
    String formattedSetValues = "[";
    
    for(SetType type : setValues.keySet()) {
      formattedSetValues += String.format("%s, ", type.name());
    }
    formattedSetValues = formattedSetValues.substring(0, formattedSetValues.length() - 2);
    formattedSetValues += "]";
    float[][] pairedValues = getPairedValues();
    int setCount = 1;
    for(float[] pair : pairedValues) {
      formattedSetValues += String.format("\n Set %d: %s", setCount, Arrays.toString(pair));
      setCount++;
    }
    return String.format("Name: %s\n%s\n", name, formattedSetValues);
  }
}
