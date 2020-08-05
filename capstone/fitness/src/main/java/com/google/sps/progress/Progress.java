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

  public GoalStep getUpdatedGoalStep(Data data) {
    GoalStep goalStep = data.getCurrentMainGoalStep();
    if(goalStep == null) {
      ProgressModel model = buildMainGoalSteps(data);
      return model.getCurrentMainGoalStep();
    }
    return updateGoalStep(data, goalStep);
  }

  private ProgressModel buildMainGoalSteps(Data data) {
    int changeCount = data.getDaysAvailable();
    Exercise start = data.getStart();
    Exercise goal = data.getGoal();

    // Values to change sets by.
    // Subtracts "1" to account for start being in progress model.
    HashMap<SetType, Float> setValuesDelta = getValuesChangeBy(changeCount - 1, start, goal);

    // Start ProgressModel with starting exercise and build supplemental goal steps on top of it.
    GoalStep current = buildSupplementalGoalSteps(new GoalStep(start));
    ProgressModel model = new ProgressModel(current);
    
    // Build model.
    for(int i = 1; i < changeCount - 1; i++) {
      Exercise exercise = createExercise(current.getExercise(), goal, setValuesDelta);
      // Stop creating exercises if no more exercises can be created (early exit).
      if(exercise == null) {
        break;
      }

      GoalStep next = buildSupplementalGoalSteps(new GoalStep(exercise));
      model.addMainGoalStep(next);
      current = next;
    }

    // Add goal only if it's not the current last one (can happen with a late exit).
    if(!current.getExercise().betterThan(goal) || !current.getExercise().equalTo(goal)) {
      GoalStep last = buildSupplementalGoalSteps(new GoalStep(goal));
      model.addMainGoalStep(last);
    }

    return model;
  }

  private HashMap<SetType, Float> getValuesChangeBy(int changeCount, Exercise src, Exercise goal) {
    HashMap<SetType, Float> changeBy = new HashMap<>();

    // Determine how many changes need to be done to sets. 
    int setDifference = goal.getSetCount() - src.getSetCount();
    if(setDifference < 0) {
      throw new ArithmeticException("Difference between goal and src sets is negative.");
    }
    
    // With this, the changes in the individual non-set parameters will never exceed the days available.
    float setValuesChangesCount = changeCount/(setDifference + 1);
    
    // Splits the current available changes across all set types.
    setValuesChangesCount /= src.getSetValues().size();

    // Counts the number of set types that don't change in value from src to goal.
    // Needed so the algorithm doesn't get stuck on unchanging values.
    int zeros = 0;

    // Sets the change by values based on the first elements in the Exercise's set values.
    for(SetType type : src.getSetValues().keySet()) {
      Float setChangeBy = (goal.getSetValues(type)[0] - src.getSetValues(type)[0])/setValuesChangesCount;
      zeros = setChangeBy == 0 ? zeros + 1 : zeros;
      changeBy.put(type, setChangeBy);
    }
    
    // Ensure there's change amongst the set values.
    if(zeros == changeBy.size()) {
      throw new ArithmeticException("No set values change.");
    }

    // Divide each element by zeros count "+1" to get even distribution.
    if(zeros != 0) {
      for(SetType type : changeBy.keySet()) {
        changeBy.put(type, changeBy.get(type)/(zeros + 1));
      }
    }

    return changeBy;
  } 

  private GoalStep buildSupplementalGoalSteps(GoalStep goalStep) {
    // TODO(ijelue): Logic to add relevant supplemental goal steps.
    return goalStep;
  }

  private Exercise createExercise(Exercise src, Exercise goal, HashMap<SetType, Float> setValuesChangeBy) {
    // If nothing from the src can change in relation to the goal, then we shoudln't create a new exercise.
    if(src.betterThan(goal) || src.equalTo(goal)) {
      return null;
    }
    
    // Prepare new Exercise variables.
    String name = goal.getName();
    int setCount = src.getSetCount();
    HashMap<SetType, float[]> setValues = new HashMap<>();
    setValues.putAll(src.getSetValues());

    // The increment of the Exercise is based on randomness (66.7% set increase, 33.3% set value increase).
    // Using a switch statement allows priority to trickle down as changes are no longer applicable to the src.
    Random rand = new Random(); 
    boolean randomIncrementFinished = false;
    while(!randomIncrementFinished) {
      int increment = rand.nextInt(3);
      switch(increment) {
        case 0:
        // Increase sets.
        case 1:
          if(src.getSetCount() < goal.getSetCount()) {
            setCount++;
            // Update set values to include an extra element.
            for(SetType type : setValues.keySet()) {
              setValues.put(type, copyAndAddElement(setValues.get(type)));
            }
            randomIncrementFinished = true;
            break;
          }
        case 2:
        // Increase set values.
          SetType[] setTypes = objArrToSetTypeArr(setValues.keySet().toArray());
          SetType type = setTypes[rand.nextInt(setTypes.length)];

          // Only increment the specific type if it doesn't equal/"exceed" the goal.
          if(!src.betterThan(goal, type).orElse(true) || !src.equalTo(goal, type).orElse(true)) {
            setValues.put(type, incrementSet(src.getSetValues(type), setValuesChangeBy.get(type)));
            
            // Copy additional set values to avoid array mutations between various objects.
            for(SetType altType : setTypes) {
              if(altType != type) {
                setValues.put(altType, src.getSetValues(altType).clone());
              }
            }
            randomIncrementFinished = true;
            break;
          }
        default:
          break;
      }
    }
    return new Exercise(name, setValues);
  }

  private float[] copyAndAddElement(float[] setValues) {
    // Return array copy with an extra element to at the end that's equal to the original's last element.
    float[] copy = Arrays.copyOf(setValues, setValues.length + 1);
    copy[copy.length - 1] = copy[copy.length - 2];
    return copy;
  }

  private float[] incrementSet(float[] setValues, float setValuesChangeBy) {
    // Invariant: setValues is ordered.
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

  private SetType[] objArrToSetTypeArr(Object[] setTypes) {
    SetType[] types = new SetType[setTypes.length];
    for(int i = 0; i < types.length; i++) {
      types[i] = (SetType) setTypes[i];
    }
    return types;
  }

  private GoalStep updateGoalStep(Data data, GoalStep goalStep) {
    // Set up model and lastest session.
    ProgressModel model = new ProgressModel(goalStep);
    Set<String> supplementalGoalSteps = goalStep.getPeels().keySet();
    Exercise[] workout = data.getLastSession().getWorkout();
    
    // Progress model based on lastest session.
    for(Exercise exercise : workout) {
      if(supplementalGoalSteps != null && supplementalGoalSteps.contains(exercise.getName())) {
        model.progressSupplementalGoalStep(exercise);
      }
      else if(goalStep.getName().equals(exercise.getName())) {
        model.progressMainGoalStep(exercise);
      }
    }

    return model.getCurrentMainGoalStep();
  }
}
