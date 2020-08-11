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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.sps.fit.*;
import com.google.sps.fit.Exercise.SetType;
import com.google.sps.util.*;
import java.util.*;

public class ProgressModel {

  private GoalStep head;
  private int size;

  public static class Builder {
    private GoalStep[] goalSteps;
    private Exercise start;
    private Exercise goal;
    private int daysAvailable;

    public Builder() {
      String name = "Running for";
      goalSteps = null;
      // Throw errors instead of default values
      start = new Exercise.Builder(name + " 1 unit")
                  .addSetTypeWithValues(SetType.DURATION_DEC, new float[] {1000})
                  .build();
      goal = new Exercise.Builder(name + " 1 unit")
                  .addSetTypeWithValues(SetType.DURATION_DEC, new float[] {500})
                  .build();
      daysAvailable = 9;
    }

    public Builder setGoal(Exercise goal) {
      if(goal != null) {
        this.goal = goal;
      }
      return this;
    }

    public Builder setStart(Exercise start) {
      if(start != null) {
        this.start = start;
      }
      return this;
    }

    public Builder setDurationDecrementStart(String name, String start) {
      if(start != null) {
        this.start = new Exercise.Builder(name)
                          .addSetTypeWithValues(SetType.DURATION_DEC, new float[] {Float.parseFloat(start)})
                          .build();
      }
      return this;
    }

    public Builder setDurationDecrementGoal(String name, String goal) {
      if(goal != null) {
        this.goal = new Exercise.Builder(name)
                          .addSetTypeWithValues(SetType.DURATION_DEC, new float[] {Float.parseFloat(goal)})
                          .build();
      }
      return this;
    }

    public Builder setDaysAvailable(String weeks) {
      if(weeks != null) {
        this.daysAvailable = Integer.parseInt(weeks) * 3;
      }
      return this;
    }

    public Builder setDaysAvailable(int weeks) {
      this.daysAvailable = weeks * 3;
      return this;
    }

    public Builder setGoalSteps(GoalStep[] goalSteps) {
      this.goalSteps = goalSteps;
      return this;
    }

    public Builder setJsonGoalSteps(String json) {
      Gson gson = new Gson();
      List<JsonGoalStep> jsonGoalSteps = gson.fromJson(json, new TypeToken<List<JsonGoalStep>>(){}.getType());
      GoalStep[] goalSteps = new GoalStep[jsonGoalSteps.size()];
      for(int i = 0; i < goalSteps.length; i++) {
        goalSteps[i] = new GoalStep(jsonGoalSteps.get(i));
      }
      this.goalSteps = goalSteps;
      return this;
    }

    public ProgressModel build() {
      ProgressModel model = new ProgressModel();
      if(goalSteps == null) {
        model.buildMainGoalSteps(daysAvailable, start, goal);
      }
      else {
        model.head = model.establishConnections(goalSteps);
        model.size = model.updateSize();
      }
      return model;
    }
  }

  private ProgressModel() {}

  private void buildMainGoalSteps(int daysAvailable, Exercise start, Exercise goal) {
    // Values to change sets by.
    // Subtracts "1" to account for start being in progress model.
    HashMap<SetType, Float> setValuesDelta = getValuesChangeBy(daysAvailable - 1, start, goal);

    // Start ProgressModel with starting exercise and build supplemental goal steps on top of it.
    GoalStep current = buildSupplementalGoalSteps(new GoalStep(start));
    head = current;
    size = 1;

    // Build model.
    for(int i = 1; i < daysAvailable - 1; i++) {
      Exercise marker = createMarker(current.getMarker(), goal, setValuesDelta);
      
      // Stop creating exercises if no more exercises can be created (early exit).
      if(marker == null) {
        break;
      }

      GoalStep next = buildSupplementalGoalSteps(new GoalStep(marker));
      addMainGoalStep(next);
      current = next;
    }

    // Add goal only if it's not the current last one (can happen with a late exit).
    if(!current.getMarker().equalTo(goal)) {
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
    if(src.betterThan(goal)) {
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
          if(!src.betterThan(goal, type).orElse(true) && !src.equalTo(goal, type).orElse(true)) {
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

  // TODO(ijelue): Throw error for bad input or look higher for error
  private GoalStep establishConnections(GoalStep[] goalSteps) {
    if(goalSteps.length == 0) {
      return null;
    }

    if(goalSteps.length == 1) {
      return goalSteps[0];
    }

    for(GoalStep goalStep : goalSteps) {
      goalStep.setNext(null);
    }

    for(int i = 1; i < goalSteps.length; i++) {
      goalSteps[0].enqueue(goalSteps[i]);
    }
    
    BananaNode goalStep = goalSteps[0];
    while(!goalStep.isHead()) {
      goalStep = goalStep.getNext();
    }

    return (GoalStep) goalStep;
  }

  public boolean updateGoalStep() {
    Session sess = getLastSession();
    if(head == null || sess == null) {
      return false;
    }

    // Set up model and lastest session.
    Set<String> supplementalGoalSteps = head.getPeels().keySet();
    Exercise[] workout = sess.getWorkout();
    
    // Progress model based on lastest session.
    for(Exercise exercise : workout) {
      if(supplementalGoalSteps != null && supplementalGoalSteps.contains(exercise.getName())) {
        progressSupplementalGoalStep(exercise);
      }
      else if(head.getName().equals(exercise.getName())) {
        progressMainGoalStep(exercise);
      }
    }
    return true;
  }

  public boolean updateGoalStep(Data data) {
    Session sess = data.getLastSession();
    if(head == null || sess == null) {
      return false;
    }

    // Set up model and lastest session.
    Set<String> supplementalGoalSteps = head.getPeels().keySet();
    Exercise[] workout = sess.getWorkout();
    
    // Progress model based on lastest session.
    for(Exercise exercise : workout) {
      if(supplementalGoalSteps != null && supplementalGoalSteps.contains(exercise.getName())) {
        progressSupplementalGoalStep(exercise);
      }
      else if(head.getName().equals(exercise.getName())) {
        progressMainGoalStep(exercise);
      }
    }
    return true;
  }

  private Session getLastSession() {
    String sessionsJson = DataHandler.getData(DataHandler.PROGRESS_PROPERTY, DataHandler.getUser());
    if(sessionsJson != null) {
      ArrayList<MarathonSession> sessions = new Gson().fromJson(sessionsJson, new TypeToken<List<MarathonSession>>(){}.getType());
      if(sessions.isEmpty()) {
        return null;
      }
      return new Session(sessions.get(sessions.size() - 1));
    }
    return new Session(
        new Exercise[] {
          new Exercise.Builder("Marathon Session")
              .addSetTypeWithValues(SetType.DURATION_DEC, new float[] {500})
              .build()}); 
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
      System.out.println(progressMain());
      System.out.println(toString());
      return true;
    }
    return false;
  }

  public BananaNode progressMain() {
    if(head == null) {
     return null;
    }
    BananaNode oldHead = head.dequeue();
    if(oldHead != null) {
      size--;
      head = (GoalStep) oldHead.getNext();
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

  private int updateSize() {
    if(head == null) {
      return 0;
    }

    // "head" counts as part of the size.
    int size = 1;
    BananaNode current = head;
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
  public GoalStep[] toArray() {
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
    return bananaArrToGoalsArr(goalSteps);
  }

  private GoalStep[] bananaArrToGoalsArr(BananaNode[] src) {
    GoalStep[] goalSteps = new GoalStep[src.length];
    for(int i = 0; i < goalSteps.length; i++) {
      goalSteps[i] = (GoalStep) src[i];
    }
    return goalSteps;
  }

  public String toJson() {
    GoalStep[] goalSteps = toArray();
    List<JsonGoalStep> jsonGoalSteps = new ArrayList<>();
    for(GoalStep goalStep : goalSteps) {
      jsonGoalSteps.add(new JsonGoalStep(goalStep));
    }
    Gson gson = new Gson();
    String json = gson.toJson(jsonGoalSteps);
    return json;
  }

  @Override
  public String toString() {
    String str = String.format("Progress Model For %s\nSize: %d\n", head.getName(), size);
    GoalStep[] arr = toArray();
    for(int i = 0; i < arr.length; i++) {
      str += arr[i] + "\n\n";
    }
    return str;
  }
}
