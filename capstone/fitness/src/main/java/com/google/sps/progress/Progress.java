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

  private ProgressModel buildModel(Data data) {
    ProgressModel model = buildMainMilestones(data);
    if(model != null) {
      model = buildSupplementalMilestones(model);
    }
    return model;
  }
  
  private ProgressModel buildMainMilestones(Data data) {
    int changeCount = data.getDaysAvailable();
    FitnessSet start = data.getStart();
    FitnessSet goal = data.getGoal();

    // Values to change sets by
    float[] setValuesDelta = getValuesChangeBy(changeCount, start, goal);

    ProgressModel model = new ProgressModel();
    model.addMainMilestone(new Milestone(start));
    Milestone current = model.getCurrentMainMilestone();

    // Build model
    for(int i = 1; i < changeCount - 1; i++) {
      Milestone next = new Milestone(createFitnessSet(current.getFitnessSet(), goal, setValuesDelta));
      model.addMainMilestone(next);
      current = next;
    }

    // Add goal only if it's not the current last one (result of basic algorithm)
    if(!current.getFitnessSet().equalTo(goal)) {
      model.addMainMilestone(new Milestone(goal));
    }

    return model;
  }

  private float[] getValuesChangeBy(int changeCount, FitnessSet start, FitnessSet goal) {
    int setDifference = goal.getSets() - start.getSets();
    if(setDifference < 0) {
      throw new ArithmeticException("Difference between goal and start sets is negative");
    }
    
    // With this, the changes in the individual non-set parameters will never exceed the days available
    float setValuesChangesCount = changeCount/ (setDifference  + 1);
    if(start.getSetType(false) != null) {
      // Splits the current available changes if fitness set is based on two quantitative types
      setValuesChangesCount /= 2;
    }

    // Sets the change by values based on the first elements in the starter fitness set
    float setType1Delta = (goal.getSetTypeValues(true)[0] - start.getSetTypeValues(true)[0])/setValuesChangesCount;
    if(start.getSetType(false) != null) {
      float setType2Delta = (goal.getSetTypeValues(false)[0] - start.getSetTypeValues(false)[0])/setValuesChangesCount;
      return new float[] {setType1Delta, setType2Delta};
    }

    return new float[] {setType1Delta};
  } 

  private FitnessSet createFitnessSet(FitnessSet fs, FitnessSet goal, float[] setValuesChangeBy) {
    String name = goal.getName();
    int sets = fs.getSets();
    SetType setType1 = goal.getSetType(true);
    SetType setType2 = goal.getSetType(false);
    float[] setType1Values = null;
    float[] setType2Values = null;

    // The increment by fitness set is based on randomness (50% set increase, 25% set1 value increase, 25% set2 value increase)
    // Using a switch statement allows priority to trickle down as changes are no longer applicable
    Random rand = new Random(); 
    boolean randomFinished = false;
    while(!randomFinished) {
      int increment = rand.nextInt(4);
      switch(increment) {
        case 0:
          // increase sets
        case 1:
          if(fs.getSets() < goal.getSets()) {
            sets++;
            setType1Values = copyAndAddValue(fs.getSetTypeValues(true));
            setType2Values = copyAndAddValue(fs.getSetTypeValues(false));
            randomFinished = true;
            break;
          }
        case 2:
          // increase set1
          if(!Arrays.equals(fs.getSetTypeValues(true), goal.getSetTypeValues(true))) {
            setType1Values = incrementSet(fs.getSetTypeValues(true), setValuesChangeBy[0]);
            setType2Values = cloneArray(fs.getSetTypeValues(false));
            randomFinished = true;
            break;
          }
        case 3:
          // increse set2
          if(setType2 != null && !Arrays.equals(fs.getSetTypeValues(false), goal.getSetTypeValues(false))) {
            setType1Values = cloneArray(fs.getSetTypeValues(true));
            setType2Values = incrementSet(fs.getSetTypeValues(false), setValuesChangeBy[1]);
            randomFinished = true;
            break;
          }
        default:
          break;
      }
    }
    return new FitnessSet(name, sets, setType1, setType2, setType1Values, setType2Values);
  }

  private float[] cloneArray(float[] arr) {
    return arr == null ? null : arr.clone();
  } 

  
  private float[] incrementSet(float[] setValues, float setValuesChangeBy) {
    // Invariant: setValues is in descending order

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

  private ProgressModel buildSupplementalMilestones(ProgressModel model) {
    // TODO(ijelue): Logic to add relevant static fitness sets.
    return model;
  }

  private ProgressModel updateProgressModel(Data data, ProgressModel model) {
    Milestone milestone = model.getCurrentMainMilestone();
    if(milestone == null) {
      throw new NullPointerException("Milestone head in Progress Model is null");
    }
    HashMap<String, PeelQueue> supplementalMilestoneSets = milestone.getSupplementalMilestones();
    FitnessSet[] sessionSets = data.getLastSession().getFitnessSets();
    
    for(FitnessSet sessionSet : sessionSets) {
      if(supplementalMilestoneSets != null && supplementalMilestoneSets.containsKey(sessionSet.getName())) {
        model.progressSupplementalMilestone(sessionSet);
      }
      else if(milestone.getName().equals(sessionSet.getName())) {
        // Rebuilds the supplemental milestones if main milestone was hit  
        boolean progressed = model.progressMainMilestone(sessionSet);
        model = progressed ? buildSupplementalMilestones(model) : model;
      }
    }

    return model;
  }

  /**
   * Returns an updated Progress Model based on given data object or a newly made one if data object does 
   * not currently contain one.
   *
   * @param data data abstraction object
   * @return an updated progress model
   */
  public ProgressModel getUpdatedProgressModel(Data data) {
    ProgressModel model = data.getProgressModel(); 
    if(model == null) {
      return buildModel(data);
    }
    return updateProgressModel(data, model);
  }
}
