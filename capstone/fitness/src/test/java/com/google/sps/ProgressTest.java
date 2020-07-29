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
import com.google.sps.fit.FitnessSet.SetType;
import com.google.sps.progress.*;
import com.google.sps.util.*;

public class ProgressTest {

  private final FitnessSet start = new FitnessSet("test", 1, SetType.DISTANCE, SetType.DURATION, new float[] {2}, new float[] {600});
  private final FitnessSet goal = new FitnessSet("test", 2, SetType.DISTANCE, SetType.DURATION, new float[] {4, 4}, new float[] {300, 300});
  private final int daysAvailable = 8;
  private final Data data = new Data(null, null, start, goal, daysAvailable);

  @Test
  public void testUpdateProgressModelNoModel() {
    // TODO(ijelue): Add actual assertions rather than prints.

    Progress progress = new Progress();
    ProgressModel model = progress.getUpdatedProgressModel(data);
    System.out.println(model);
  }

  @Test
  public void testUpdateProgressModelWithModel() {
    // TODO(ijelue): Add actual assertions rather than prints. Use Mockito to eliminate randomness.

    Progress progress = new Progress();
    ProgressModel model = progress.getUpdatedProgressModel(data);
    System.out.println(model);
    
    Session sess = new Session(new FitnessSet[] {model.getCurrentMainMilestone().getFitnessSet()});
    Data data2 = new Data(sess, model, null, null, daysAvailable);
    
    model = progress.getUpdatedProgressModel(data2); 
    System.out.println(model);

    sess = new Session(new FitnessSet[] {model.getCurrentMainMilestone().getFitnessSet()});
    data2 = new Data(sess, model, null, null, daysAvailable);
    
    model = progress.getUpdatedProgressModel(data2); 
    System.out.println(model);
  }
}