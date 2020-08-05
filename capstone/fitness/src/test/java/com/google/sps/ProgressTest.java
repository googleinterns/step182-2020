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

package com.google.sps;

import org.junit.*;
import static org.junit.Assert.*;
import com.google.sps.fit.*;
import com.google.sps.fit.Exercise.SetType;
import com.google.sps.fit.Exercise.Builder;
import com.google.sps.progress.*;
import com.google.sps.util.*;
import java.util.*;

public class ProgressTest {

  /* Large enough to show algorithmic complexity. */
  private final int daysAvailable = 9;
  
  private final String name = "test";

  @Test(expected = ArithmeticException.class)
  public void testUpdateGoalStepAllStaticSets() {
    // Note: This means the feature does not support only increasing in sets.
    
    // Defines a start and goal that do not change in average value.
    Exercise start = new Exercise.Builder(name)
        .addSetTypeWithValues(SetType.DISTANCE, new float[] {2})
        .addSetTypeWithValues(SetType.DURATION_DEC, new float[] {600})
        .build();

    Exercise goal = new Exercise.Builder(name)
        .addSetTypeWithValues(SetType.DISTANCE, new float[] {2, 2})
        .addSetTypeWithValues(SetType.DURATION_DEC, new float[] {600, 600})
        .build();

    Data data = new Data(null, null, start, goal, daysAvailable);

    // Try to build internal ProgressModel, but fail.
    ProgressModel model = new ProgressModel(data);
  }

  @Test(expected = ArithmeticException.class)
  public void testUpdateGoalStepMismatchSets() {
    // Fail to create exercise because the number of sets for one set type does not equal the other.
    Exercise exercise = new Exercise.Builder(name)
        .addSetTypeWithValues(SetType.DISTANCE, new float[] {2})
        .addSetTypeWithValues(SetType.DURATION_DEC, new float[] {600, 600})
        .build();
  }

  @Test(expected = ArithmeticException.class)
  public void testUpdateGoalStepMismatchSetsHashMap() {
    HashMap<SetType, float[]> setValues = new HashMap<>();
    setValues.put(SetType.DISTANCE, new float[] {2}); 
    setValues.put(SetType.DURATION_DEC, new float[] {600, 600}); 
    
    // Fail to create exercise because the number of sets for one set type does not equal the other.
    Exercise exercise = new Exercise.Builder(name)
        .addSetValues(setValues)
        .build();
  }

  @Test(expected = ArithmeticException.class)
  public void testUpdateGoalStepLargerGoalInSets() {
    // Note: This means the feature does not support a reduction in sets as progress.

    // Defines a start and goal that decrease in set count.
    Exercise start = new Exercise.Builder(name)
        .addSetTypeWithValues(SetType.DISTANCE, new float[] {2, 2})
        .addSetTypeWithValues(SetType.DURATION_DEC, new float[] {600, 600})
        .build();

    Exercise goal = new Exercise.Builder(name)
        .addSetTypeWithValues(SetType.DISTANCE, new float[] {4})
        .addSetTypeWithValues(SetType.DURATION_DEC, new float[] {300})
        .build();

    Data data = new Data(null, null, start, goal, daysAvailable);

    // Try to build internal ProgressModel, but fail. 
    ProgressModel model = new ProgressModel(data);
  }

  @Test
  public void testUpdateGoalStepSingle() {
    // Defines a start and goal with only a single set type changing.
    Exercise start = new Exercise.Builder(name)
        .addSetTypeWithValues(SetType.DURATION_DEC, new float[] {600})
        .build();
    
    Exercise goal = new Exercise.Builder(name)
        .addSetTypeWithValues(SetType.DURATION_DEC, new float[] {300})
        .build();
    
    Data data = new Data(null, null, start, goal, daysAvailable);

    // Build.
    ProgressModel model = new ProgressModel(data);
    
    // Test validity of dynamic model.
    assertTrue(model.getCurrentMainGoalStep().getMarker().equalTo(start));
    assertTrue(model.getLast().getMarker().equalTo(goal));
    assertTrue(model.getSize() <= daysAvailable);
  }

  @Test
  public void testUpdateGoalStepOneStaticSet() {
    // Defines a start and goal with a single set type changing and the other remaining the same.
    Exercise start = new Exercise.Builder(name)
        .addSetTypeWithValues(SetType.DISTANCE, new float[] {2})
        .addSetTypeWithValues(SetType.DURATION_DEC, new float[] {600})
        .build();

    Exercise goal = new Exercise.Builder(name)
        .addSetTypeWithValues(SetType.DISTANCE, new float[] {2})
        .addSetTypeWithValues(SetType.DURATION_DEC, new float[] {300})
        .build();

    Data data = new Data(null, null, start, goal, daysAvailable);

    // Build.
    ProgressModel model = new ProgressModel(data);
    
    // Test validity of dynamic model.
    assertTrue(model.getCurrentMainGoalStep().getMarker().equalTo(start));
    assertTrue(model.getLast().getMarker().equalTo(goal));
    assertTrue(model.getSize() <= daysAvailable);
  }

  @Test
  public void testUpdateGoalStepMulitpleSetTypes() {
    // Defines a start and goal with multiple set and set type changes.
    Exercise start = new Exercise.Builder(name)
        .addSetTypeWithValues(SetType.DISTANCE, new float[] {2})
        .addSetTypeWithValues(SetType.DURATION_DEC, new float[] {600})
        .build();
    
    Exercise goal = new Exercise.Builder(name)
        .addSetTypeWithValues(SetType.DISTANCE, new float[] {4, 4})
        .addSetTypeWithValues(SetType.DURATION_DEC, new float[] {300, 300})
        .build();

    Data data = new Data(null, null, start, goal, daysAvailable);

    // Build.
    ProgressModel model = new ProgressModel(data);
    
    // Test validity of dynamic model.
    assertTrue(model.getCurrentMainGoalStep().getMarker().equalTo(start));
    assertTrue(model.getLast().getMarker().equalTo(goal));
    assertTrue(model.getSize() <= daysAvailable);
  }

  @Test
  public void testUpdateGoalStepWithPrevious() {
    // Defines a start and goal with multiple set and set type changes.
    Exercise start = new Exercise.Builder(name)
        .addSetTypeWithValues(SetType.DISTANCE, new float[] {2})
        .addSetTypeWithValues(SetType.DURATION_DEC, new float[] {600})
        .build();
    
    Exercise goal = new Exercise.Builder(name)
        .addSetTypeWithValues(SetType.DISTANCE, new float[] {4, 4})
        .addSetTypeWithValues(SetType.DURATION_DEC, new float[] {300, 300})
        .build();
    
    Data data = new Data(null, null, start, goal, daysAvailable);

    // Build.
    ProgressModel model = new ProgressModel(data);
    GoalStep mainGoalStep = model.getCurrentMainGoalStep();

    // Mock new session.
    Session sess = new Session(new Exercise[] {mainGoalStep.getMarker()});
    data = new Data(sess, mainGoalStep, null, null, daysAvailable);
    
    // Update GoalStep based of off mock session.
    model = new ProgressModel(data);

    // Test validity of updated dynamic model being a progression.
    boolean betterThanInOneType = model.getCurrentMainGoalStep().getMarker().betterThan(start, SetType.DISTANCE).orElse(false) ||
                                   model.getCurrentMainGoalStep().getMarker().betterThan(start, SetType.DURATION_DEC).orElse(false); 
    assertTrue(betterThanInOneType || model.getCurrentMainGoalStep().getMarker().getSetCount() > start.getSetCount());
    assertTrue(model.getSize() <= daysAvailable - 1);
  }
}