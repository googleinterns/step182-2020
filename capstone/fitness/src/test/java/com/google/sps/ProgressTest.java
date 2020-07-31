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
import com.google.sps.progress.*;
import com.google.sps.util.*;
import java.util.*;

public class ProgressTest {

  private final int daysAvailable = 9;
  private final String name = "test";

  @Test(expected = ArithmeticException.class)
  public void testUpdateGoalStepAllStaticSets() {
    // Note: This means the feature does not support only increasing in sets.
    
    Exercise start = new Exercise(name, SetType.DISTANCE, SetType.DURATION_DEC, new float[] {2}, new float[] {600});
    Exercise goal = new Exercise(name, SetType.DISTANCE, SetType.DURATION_DEC, new float[] {2, 2}, new float[] {600, 600});
    Data data = new Data(null, null, start, goal, daysAvailable);

    Progress progress = new Progress();
    GoalStep mainGoalStep = progress.getUpdatedGoalStep(data);
  }

  @Test(expected = ArithmeticException.class)
  public void testUpdateGoalStepMismatchSets() {
    Exercise exercise = new Exercise(name, SetType.DISTANCE, SetType.DURATION_DEC, new float[] {2}, new float[] {600, 600});
  }

  @Test(expected = ArithmeticException.class)
  public void testUpdateGoalStepMismatchSetsHashMap() {
    HashMap<SetType, float[]> setValues = new HashMap<>();
    setValues.put(SetType.DISTANCE, new float[] {2}); 
    setValues.put(SetType.DURATION_DEC, new float[] {600, 600}); 
    Exercise exercise = new Exercise(name, setValues);
  }

  @Test(expected = ArithmeticException.class)
  public void testUpdateGoalStepLargerGoalInSets() {
    // Note: This means the feature does not support a reduction in sets as progress.

    Exercise start = new Exercise(name, SetType.DISTANCE, SetType.DURATION_DEC, new float[] {2, 2}, new float[] {600, 600});
    Exercise goal = new Exercise(name, SetType.DISTANCE, SetType.DURATION_DEC, new float[] {4}, new float[] {300});
    Data data = new Data(null, null, start, goal, daysAvailable);

    Progress progress = new Progress();
    GoalStep mainGoalStep = progress.getUpdatedGoalStep(data);
  }

  @Test
  public void testUpdateGoalStepSingle() {
    // TODO(ijelue): Add actual assertions rather than prints.
    Exercise start = new Exercise(name, SetType.DURATION_DEC, new float[] {600});
    Exercise goal = new Exercise(name, SetType.DURATION_DEC, new float[] {300});
    Data data = new Data(null, null, start, goal, daysAvailable);

    Progress progress = new Progress();
    GoalStep mainGoalStep = progress.getUpdatedGoalStep(data);
    // assert first is start
    // assert last is goal
    // assert size
  }

  @Test
  public void testUpdateGoalStepOneStaticSet() {
    // TODO(ijelue): Add actual assertions rather than prints. Use Mockito to eliminate randomness.
    Exercise start = new Exercise(name, SetType.DISTANCE, SetType.DURATION_DEC, new float[] {2}, new float[] {600});
    Exercise goal = new Exercise(name, SetType.DISTANCE, SetType.DURATION_DEC, new float[] {2}, new float[] {300});
    Data data = new Data(null, null, start, goal, daysAvailable);

    Progress progress = new Progress();
    GoalStep mainGoalStep = progress.getUpdatedGoalStep(data);
    // assert first is start
    // assert last is goal
    // assert size
  }

  @Test
  public void testUpdateGoalStepMulitpleSetTypes() {
    // TODO(ijelue): Add actual assertions rather than prints.
    Exercise start = new Exercise(name, SetType.DISTANCE, SetType.DURATION_DEC, new float[] {2}, new float[] {600});
    Exercise goal = new Exercise(name, SetType.DISTANCE, SetType.DURATION_DEC, new float[] {4, 4}, new float[] {300, 300});
    Data data = new Data(null, null, start, goal, daysAvailable);

    Progress progress = new Progress();
    GoalStep mainGoalStep = progress.getUpdatedGoalStep(data);
    // assert first is start
    // assert last is goal
    // assert size
  }

  @Test
  public void testUpdateGoalStepWithPrevious() {
    // TODO(ijelue): Add actual assertions rather than prints. Use Mockito to eliminate randomness.

    Exercise start = new Exercise(name, SetType.DISTANCE, SetType.DURATION_DEC, new float[] {2}, new float[] {600});
    Exercise goal = new Exercise(name, SetType.DISTANCE, SetType.DURATION_DEC, new float[] {4, 4}, new float[] {300, 300});
    Data data = new Data(null, null, start, goal, daysAvailable);

    Progress progress = new Progress();
    GoalStep mainGoalStep = progress.getUpdatedGoalStep(data);
    //System.out.println(new ProgressModel(mainGoalStep));
    // assert first is start
    // assert last is goal
    // assert size

    Session sess = new Session(new Exercise[] {mainGoalStep.getExercise()});
    data = new Data(sess, mainGoalStep, null, null, daysAvailable);
    
    mainGoalStep = progress.getUpdatedGoalStep(data);
    //System.out.println(new ProgressModel(mainGoalStep));
    // assert first is start
    // assert last is goal
    // assert size

    sess = new Session(new Exercise[] {mainGoalStep.getExercise()});
    data = new Data(sess, mainGoalStep, null, null, daysAvailable);
    
    mainGoalStep = progress.getUpdatedGoalStep(data);
    //System.out.println(new ProgressModel(mainGoalStep));

    // assert first is start
    // assert last is goal
    // assert size
  }
}