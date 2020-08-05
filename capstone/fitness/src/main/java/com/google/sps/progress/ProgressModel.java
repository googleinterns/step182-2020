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

public class ProgressModel {

  private GoalStep head;
  private int size;

  public ProgressModel(Data data) {
    head = data.getCurrentMainGoalStep();
    if(head == null) {
      buildMainGoalSteps(data);
    }
    else {
      updateGoalStep(data, head);
      size = updateSize(head);
    }
  }

  private void buildMainGoalSteps(Data data) {
    int changeCount = data.getDaysAvailable();
    Exercise start = data.getStart();
    Exercise goal = data.getGoal();

    // Values to change sets by.
    // Subtracts "1" to account for start being in progress model.
    HashMap<SetType, Float> setValuesDelta = getValuesChangeBy(changeCount - 1, start, goal);

    // Start ProgressModel with starting exercise and build supplemental goal steps on top of it.
    GoalStep current = buildSupplementalGoalSteps(new GoalStep(start));
    head = current;

    // Build model.
    for(int i = 1; i < changeCount - 1; i++) {
      Exercise marker = createMarker(current.getMarker(), goal, setValuesDelta);
      
      // Stop creating exercises if no more exercises can be created (early exit).
      if(marker == null || marker.betterThan(goal) || marker.equalTo(goal)) {
        System.out.println("Stops!!");
        break;
      }

      GoalStep next = buildSupplementalGoalSteps(new GoalStep(marker));
      addMainGoalStep(next);
      current = next;
    }

    // Add goal only if it's not the current last one (can happen with a late exit).
    if(!current.getMarker().betterThan(goal) && !current.getMarker().equalTo(goal)) {
      GoalStep last = buildSupplementalGoalSteps(new GoalStep(goal));
      addMainGoalStep(last);
    }
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

  private Exercise createMarker(Exercise src, Exercise goal, HashMap<SetType, Float> setValuesChangeBy) {
    // If nothing from the src can change in relation to the goal, then we shoudln't create a new exercise.
    if(src.betterThan(goal) || src.equalTo(goal)) {
      return null;
    }
    
    // Prepare new Exercise variables.
    String name = goal.getName();
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
            setValues.put(type, incrementSet(src.getSetValues(type), setValuesChangeBy.get(type), goal.getSetValues(type)[0], type));
            
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
    return new Exercise.Builder(name)
        .addSetValues(setValues)
        .build();
  }

  private float[] copyAndAddElement(float[] setValues) {
    // Return array copy with an extra element to at the end that's equal to the original's last element.
    float[] copy = Arrays.copyOf(setValues, setValues.length + 1);
    copy[copy.length - 1] = copy[copy.length - 2];
    return copy;
  }

  private float[] incrementSet(float[] setValues, float setValuesChangeBy, float clamp, SetType type) {
    // Invariant: setValues is ordered.
    float[] copy = setValues.clone();
    if(copy[0] == copy[copy.length - 1]) {
      if(type.isDec()) {
        copy[0] = clamp(copy[0] + setValuesChangeBy, clamp, copy[0] + setValuesChangeBy); 
      }
      else {
        copy[0] = clamp(copy[0] + setValuesChangeBy, copy[0] + setValuesChangeBy, clamp);
      }
    }
    else {
      int i = 1;
      while(copy[0] == copy[i]) {
        i++;
      }
      if(type.isDec()) {
        copy[i] = clamp(copy[i] + setValuesChangeBy, clamp, copy[i] + setValuesChangeBy); 
      }
      else {
        copy[i] = clamp(copy[i] + setValuesChangeBy, copy[i] + setValuesChangeBy, clamp);
      }
    }
    return copy;
  }

  private float clamp(float value, float min, float max) {
    if(value < min) {
      return min;
    }
    if(value > max) {
      return max;
    }
    return value;
  }

  private SetType[] objArrToSetTypeArr(Object[] setTypes) {
    SetType[] types = new SetType[setTypes.length];
    for(int i = 0; i < types.length; i++) {
      types[i] = (SetType) setTypes[i];
    }
    return types;
  }

  private void updateGoalStep(Data data, GoalStep goalStep) {
    // Set up model and lastest session.
    Set<String> supplementalGoalSteps = goalStep.getPeels().keySet();
    Exercise[] workout = data.getLastSession().getWorkout();
    
    // Progress model based on lastest session.
    for(Exercise exercise : workout) {
      if(supplementalGoalSteps != null && supplementalGoalSteps.contains(exercise.getName())) {
        progressSupplementalGoalStep(exercise);
      }
      else if(goalStep.getName().equals(exercise.getName())) {
        progressMainGoalStep(exercise);
      }
    }
  }

  /**
   * Advances to the next supplemental goal step if the given Exercise is greater than or equal to the current goal step.
   *
   * @param userExercise Exercise to evaluate.
   * @return if the operation was successful.
   */
  public boolean progressSupplementalGoalStep(Exercise userExercise) {
    return false;
  }

  /**
   * Advances to the next goal step if the given Exercise is greater than or equal to the current goal step.
   *
   * @param userExercise Exercise to evaluate.
   * @return if the operation was successful.
   */
  public boolean progressMainGoalStep(Exercise userExercise) {
    Exercise marker = head.getMarker();
    if(userExercise.betterThan(marker) || userExercise.equalTo(marker)) {
      progressMain();
      return true;
    }
    return false;
  }

  public BananaNode progressMain() {
    BananaNode oldHead = head.dequeue();
    if(oldHead != null) {
      size--;
      head = (GoalStep) head.getNext();
    }
    return oldHead;
  }

  public boolean addMainGoalStep(GoalStep goalStep) {
    boolean success = head.enqueue(goalStep);
    if(success) {
      size++;
    }
    return success;
  }

  public GoalStep getCurrentMainGoalStep() {
    return head;
  }

  public int getSize() {
    return size;
  }

  public GoalStep getLast() {
    BananaNode last = head;
    while(last.getNext() != null) {
        last = last.getNext();
    }
    return (GoalStep) last;
  }

  private int updateSize(BananaNode start) {
    // "start" counts as part of the size.
    int size = 1;
    BananaNode current = start;
    while(current.getNext() != null) {
      current = current.getNext();
      size++;
    }
    return size;
  }

  /**
   * Array of all the main goal steps in the progress model.
   * 
   * @return array of all the main goal steps in the progress model.
   */
  public BananaNode[] toArray() {
    // Get size based on uncompleted nodes.
    int length = size;
    BananaNode firstGoalStep = head;

    // Get the true size of queue based on the first ever node.
    while(firstGoalStep.getPrev() != null) {
      firstGoalStep = firstGoalStep.getPrev();
      length++;
    }

    // Add all nodes that ever existed in queue to array.
    BananaNode[] goalSteps = new BananaNode[length];
    for(int i = 0; i < length; i++) {
      goalSteps[i] = firstGoalStep;
      firstGoalStep = firstGoalStep.getNext();
    }
    return goalSteps;
  }

  @Override
  public String toString() {
    String str = String.format("Progress Model For %s\nSize: %d\n", head.getName(), size);
    BananaNode[] arr = toArray();
    for(int i = 0; i < arr.length; i++) {
      str += arr[i] + "\n\n";
    }
    return str;
  }
}
