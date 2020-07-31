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
import com.google.sps.fit.Exercise.SetType;
import com.google.sps.util.*;
import java.util.*;

/* Handler for ProgressModel updates. */
public class Progress {
  
  private ProgressModel buildMainGoalSteps(Data data) {
    int changeCount = data.getDaysAvailable();
    Exercise start = data.getStart();
    Exercise goal = data.getGoal();

    // Values to change sets by.
    HashMap<SetType, Float> setValuesDelta = getValuesChangeBy(changeCount, start, goal);

    // Start BananaQueue as ProgressModel.
    GoalStep current = new GoalStep(start);
    current = buildSupplementalGoalSteps(current);
    ProgressModel model = new ProgressModel(current);

    // Build model.
    for(int i = 1; i < changeCount - 1; i++) {
      GoalStep next = new GoalStep(createExercise(current.getExercise(), goal, setValuesDelta));
      next = buildSupplementalGoalSteps(next);
      model.addMainGoalStep(next);
      current = next;
    }

    // Add goal only if it's not the current last one (result of algorithm).
    if(!current.getExercise().equalTo(goal)) {
      GoalStep last = new GoalStep(goal);
      last = buildSupplementalGoalSteps(last);
      model.addMainGoalStep(last);
    }

    return model;
  }

  private HashMap<SetType, Float> getValuesChangeBy(int changeCount, Exercise start, Exercise goal) {
    HashMap<SetType, Float> changeBy = new HashMap<>();

    // Determine how many changes needs to be done to sets. 
    int setDifference = goal.getSets() - start.getSets();
    if(setDifference < 0) {
      throw new ArithmeticException("Difference between goal and start sets is negative");
    }
    
    // With this, the changes in the individual non-set parameters will never exceed the days available.
    float setValuesChangesCount = changeCount/(setDifference  + 1);
    
    // Splits the current available changes if Exercise is based on two quantitative types.
    setValuesChangesCount /= start.getSetValues().size();

    // Sets the change by values based on the first elements in the Exercise's set values.
    for(SetType type : start.getSetValues().keySet()) {
      Float setChangeBy = (goal.getSetValues(type)[0] - start.getSetValues(type)[0])/setValuesChangesCount;
      changeBy.put(type, setChangeBy);
    }

    return changeBy;
  } 

  private Exercise createExercise(Exercise src, Exercise goal, HashMap<SetType, Float> setValuesChangeBy) {
    // Prepare new Exercise variables.
    String name = goal.getName();
    int sets = src.getSets();
    HashMap<SetType, float[]> setValues = new HashMap<>();
    setValues.putAll(src.getSetValues());

    // The increment of the Exercise is based on randomness (66.7% set increase, 33.3% set value increase).
    // Using a switch statement allows priority to trickle down as changes are no longer applicable.
    Random rand = new Random(); 
    boolean randomFinished = false;
    while(!randomFinished) {
      int increment = rand.nextInt(3);
      switch(increment) {
        case 0:
        // Increase sets.
        case 1:
          if(src.getSets() < goal.getSets()) {
            sets++;
            for(SetType type : setValues.keySet()) {
              setValues.put(type, copyAndAddValue(setValues.get(type)));
            }
            randomFinished = true;
            break;
          }
        case 2:
        // Increase set values.
          Object[] setTypes = setValues.keySet().toArray();
          SetType type = (SetType) setTypes[rand.nextInt(setTypes.length)];
          SetType altType = getAlternativeType(setTypes, type);
          // Only increment the specific type if it doesn't equal/"exceed" the goal.
          if(!src.greaterThan(goal, type).orElse(true) && !src.equalTo(goal, type).orElse(true)) {
            setValues.put(type, incrementSet(src.getSetValues(type), setValuesChangeBy.get(type)));
            setValues.put(altType, cloneArray(src.getSetValues(altType))); /* Copies array to avoid array mutation in other objects. */
            randomFinished = true;
            break;
          }
        default:
          break;
      }
    }
    return new Exercise(name, sets, setValues);
  }

  private SetType getAlternativeType(Object[] setTypes, SetType type) {
    // Gets first set type that isn't the given set type.
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
    // Return array copy with an extra element to at the end that's equal to the original's last element.
    float[] copy = Arrays.copyOf(setValues, setValues.length + 1);
    copy[copy.length - 1] = copy[copy.length - 2];
    return copy;
  }

  private GoalStep buildSupplementalGoalSteps(GoalStep goalStep) {
    // TODO(ijelue): Logic to add relevant supplemental goal steps.
    return goalStep;
  }

  private GoalStep updateGoalStep(Data data, GoalStep goalStep) {
    // Set up model and lastest session.
    ProgressModel model = new ProgressModel(goalStep);
    HashMap<String, PeelQueue> supplementalGoalSteps = goalStep.getSupplementalGoalSteps();
    Exercise[] workout = data.getLastSession().getWorkout();
    
    // Progress model based on lastest session.
    for(Exercise exercise : workout) {
      if(supplementalGoalSteps != null && supplementalGoalSteps.containsKey(exercise.getName())) {
        model.progressSupplementalGoalStep(exercise);
      }
      else if(goalStep.getName().equals(exercise.getName())) {
        model.progressMainGoalStep(exercise);
      }
    }

    return model.getCurrentMainGoalStep();
  }

  public GoalStep getUpdatedGoalStep(Data data) {
    GoalStep goalStep = data.getCurrentMainGoalStep();
    if(goalStep == null) {
      ProgressModel model = buildMainGoalSteps(data);
      return model.getCurrentMainGoalStep();
    }
    return updateGoalStep(data, goalStep);
  }
}
