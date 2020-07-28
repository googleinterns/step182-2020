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

import com.google.sps.util.*;

/*




In order of priority
- Code Review
- MAKE MOCK DATA CLASS
- Move FitnessSet Logic to Progress Handler
- Consider Milestone only implementation of progressModel
  - or just keep both and replace with whichever works in grand scheme (if milestone works, go with that)
- Create logic to build model






*/

public class Progress {

  private ProgressModel buildModel(Object data) {
    ProgressModel model = buildMainMilestones(data);
    if(model != null) {
      model = buildSupplementalMilestones(model);
    }
    return model;
  }
  
  private ProgressModel buildMainMilestones(Object data) {
    // Logic to build linear progression
    // TODO
    return null;
  }
  
  private ProgressModel buildSupplementalMilestones(ProgressModel model) {
    // Logic to add static fitness sets
    // TODO
    return model;
  }

  private ProgressModel updateProgressModel(Object data, ProgressModel model) {
    Milestone milestone = model.getCurrentMainMilestone();
    if(milestone == null) {
      return null; // Might be better to throw an error
    }
    HashMap<String, SupplementalMilestone> supplementalMilestoneSets = milestone.getSupplementalMilestones();
    FitnessSet[] sessionSets = data.lastSession().getSets();
    
    for(FitnessSet sessionSet : sessionSets) {
      if(supplementalMilestoneSets != null && supplementalMilestoneSets.contains(sessionSet.getName())) {
        model.progressSupplementalMilestone(sessionSet);
      }
      else if(milestone.getName().equals(sessionSet.getName())) {
        boolean progressed = model.progressMainMilestone(sessionSet);
        model = progressed ? buildSupplementalMilestones(model) : model;
      }
    }

    return model;
  }

  public ProgressModel getUpdatedProgressModel(Object data) {
    ProgressModel model = data.getProgressModel(); 
    if(model == null) {
      return buildModel(data);
    }
    return updateProgressModel(data, model);
  }

//---------------------------------------------------------------------------------------
// Alternative implementation which makes it so you dont have to store progress model (theorectically); instead you store milestone only

  private Milestone buildSupplementalMilestones(Milestone milestone) {
    // Logic to add static fitness sets
    // TODO
    // NOTE: Deciding whether node structure is possible for saving data
    return milestone;
  }

  private Milestone updateMilestones(Object data, Milestone milestone) {
    HashMap<String, SupplementalMilestone> supplementalMilestoneSets = milestone.getSupplementalMilestones();
    FitnessSet[] sessionSets = data.lastSession().getSets();
    
    for(FitnessSet sessionSet : sessionSets) {
      if(supplementalMilestoneSets != null && supplementalMilestoneSets.contains(sessionSet.getName())) {
        milestone.progressSupplementalMilestone(sessionSet);
      }
      else if(milestone.getName().equals(sessionSet.getName())) {
        int prevLevel = milestone.getLevel();
        milestone = milestone.progressMainMilestone(sessionSet);
        milestone = milestone.getLevel() > prevLevel ? buildSupplementalMilestones(milestone) : milestone;
      }
    }

    return milestone;
  }

  public Milestone getUpdatedMilestones(Object data) {
    Milestone milestone = data.getCurrentMainMilestone(); 
    if(milestone == null) {
      ProgressModel model = buildModel(data);
      return model.getCurrentMainMilestone();
    }
    return updateMilestones(data, milestone);
  }

}
