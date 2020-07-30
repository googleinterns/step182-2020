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
import com.google.sps.fit.FitnessSet.SetType;
import com.google.sps.util.*;
import java.util.*;

/* Handler for ProgressModel updates. */
public class Progress {
  
  private ProgressModel buildMainMilestones(Data data) {
    int changeCount = data.getDaysAvailable();
    FitnessSet start = data.getStart();
    FitnessSet goal = data.getGoal();

    // Values to change sets by.
    HashMap<SetType, Float> setValuesDelta = getValuesChangeBy(changeCount, start, goal);

    Milestone current = new Milestone(start);
    current = buildSupplementalMilestones(current);
    ProgressModel model = new ProgressModel(current);

    // Build model.
    for(int i = 1; i < changeCount - 1; i++) {
      Milestone next = new Milestone(createFitnessSet(current.getFitnessSet(), goal, setValuesDelta));
      next = buildSupplementalMilestones(next);
      model.addMainMilestone(next);
      current = next;
    }

    // Add goal only if it's not the current last one (result of basic algorithm).
    if(!current.getFitnessSet().equalTo(goal)) {
      Milestone last = new Milestone(goal);
      last = buildSupplementalMilestones(last);
      model.addMainMilestone(last);
    }

    return model;
  }

  private HashMap<SetType, Float> getValuesChangeBy(int changeCount, FitnessSet start, FitnessSet goal) {
    HashMap<SetType, Float> changeBy = new HashMap<>();
    int setDifference = goal.getSets() - start.getSets();
    if(setDifference < 0) {
      throw new ArithmeticException("Difference between goal and start sets is negative");
    }
    
    // With this, the changes in the individual non-set parameters will never exceed the days available.
    float setValuesChangesCount = changeCount/(setDifference  + 1);
    
    // Splits the current available changes if fitness set is based on two quantitative types.
    setValuesChangesCount /= start.getSetValues().size();

    // Sets the change by values based on the first elements in the starter fitness set.
    for(SetType type : start.getSetValues().keySet()) {
      Float setChangeBy = (goal.getSetValues(type)[0] - start.getSetValues(type)[0])/setValuesChangesCount;
      changeBy.put(type, setChangeBy);
    }

    return changeBy;
  } 

  private FitnessSet createFitnessSet(FitnessSet fs, FitnessSet goal, HashMap<SetType, Float> setValuesChangeBy) {
    String name = goal.getName();
    int sets = fs.getSets();
    HashMap<SetType, float[]> setValues = new HashMap<>();
    setValues.putAll(fs.getSetValues());

    // The increment by fitness set is based on randomness (50% set increase, 50% set value increase).
    // Using a switch statement allows priority to trickle down as changes are no longer applicable.
    Random rand = new Random(); 
    boolean randomFinished = false;
    while(!randomFinished) {
      int increment = rand.nextInt(2);
      switch(increment) {
        case 0:
        // Increase sets.
          if(fs.getSets() < goal.getSets()) {
            sets++;
            for(SetType type : setValues.keySet()) {
              setValues.put(type, copyAndAddValue(setValues.get(type)));
            }
            randomFinished = true;
            break;
          }
        case 1:
        // Increase set values.
          Object[] setTypes = setValues.keySet().toArray();
          SetType type = (SetType) setTypes[rand.nextInt(setTypes.length)];
          SetType altType = getAlternativeType(setTypes, type);
          if(!fs.greaterThan(goal, type).orElse(true) && !fs.equalTo(goal, type).orElse(true)) {
            setValues.put(type, incrementSet(fs.getSetValues(type), setValuesChangeBy.get(type)));
            setValues.put(altType, cloneArray(fs.getSetValues(altType)));
            randomFinished = true;
            break;
          }
        default:
          break;
      }
    }
    return new FitnessSet(name, sets, setValues);
  }

  private SetType getAlternativeType(Object[] setTypes, SetType type) {
    for(Object obj : setTypes) {
      SetType setType = (SetType) obj;
      if(!setType.name().equals(type.name())) {
        return setType;
      }
    }
    return null;
  }

  private float[] cloneArray(float[] arr) {
    return arr == null ? null : arr.clone();
  } 

  
  private float[] incrementSet(float[] setValues, float setValuesChangeBy) {
    // Invariant: setValues are in ascending or descending order.

    float[] copy = setValues.clone();
    if(copy[0] == copy[copy.length - 1]) {
      copy[0] += setValuesChangeBy;
    }
    else {
      int i = 1;
      while(copy[0] == copy[i]) {
        i++;
      }
      copy[i] += setValuesChangeBy;
    }
    return copy;
  }
  
  private float[] copyAndAddValue(float[] setValues) {
    float[] copy = Arrays.copyOf(setValues, setValues.length + 1);
    copy[copy.length - 1] = copy[copy.length - 2];
    return copy;
  }

  private Milestone buildSupplementalMilestones(Milestone milestone) {
    // TODO(ijelue): Logic to add relevant static fitness sets.
    return milestone;
  }

  private Milestone updateMilestone(Data data, Milestone milestone) {
    ProgressModel model = new ProgressModel(milestone);
    HashMap<String, PeelQueue> supplementalMilestoneSets = milestone.getSupplementalMilestones();
    FitnessSet[] sessionSets = data.getLastSession().getFitnessSets();
    
    for(FitnessSet sessionSet : sessionSets) {
      if(supplementalMilestoneSets != null && supplementalMilestoneSets.containsKey(sessionSet.getName())) {
        model.progressSupplementalMilestone(sessionSet);
      }
      else if(milestone.getName().equals(sessionSet.getName())) {
        model.progressMainMilestone(sessionSet);
      }
    }

    return model.getCurrentMainMilestone();
  }

  public Milestone getUpdatedMilestone(Data data) {
    Milestone milestone = data.getCurrentMainMilestone();
    if(milestone == null) {
      ProgressModel model = buildMainMilestones(data);
      return model.getCurrentMainMilestone();
    }
    return updateMilestone(data, milestone);
  }
}
