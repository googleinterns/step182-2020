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
import com.google.sps.util.*;

public class ProgressModel {

  private Milestone head;
  private int size;

  public ProgressModel(Milestone start) {
    head = start;
    size = updateSize(start);
  }

  private int updateSize(Milestone start) {
    int size = 1;
    BananaNode trueStart = start;
    
    while(!trueStart.isHead()) {
      trueStart = trueStart.getPrev();
    }

    while(trueStart.getNext() != null) {
      size++;
    }
    
    return size;
  }

  public boolean progressSupplementalMilestone(FitnessSet fSet) {
    return false;
  }
  
  /**
   * Advances to the next milestone if the given fitness set is greater on average than the current
   * milestone or if the given fitness set is equal to the current milestone.
   *
   * @param fSet FitnessSet to compare to.
   * @return if the operation was successful.
   */
  public boolean progressMainMilestone(FitnessSet fSet) {
    FitnessSet currentFSet = head.getFitnessSet();
    if(fSet.greaterThan(currentFSet) || fSet.equalTo(currentFSet)) {
      progressMain();
      return true;
    }
    return false;
  }

  public boolean addMainMilestone(Milestone milestone) {
    boolean success = head.enqueue(milestone);
    if(success) {
      size++;
    }
    return success;
  }

  public BananaNode progressMain() {
    BananaNode oldHead = head.dequeue();
    if(oldHead != null) {
      size--;
      head = (Milestone) head.getNext();
    }
    return oldHead;
  }

  public Milestone getCurrentMainMilestone() {
    return head;
  }

  public int getSize() {
    return size;
  }

  /**
   * Array representation of all main milestones in the progress model.
   * 
   * @return array representation of all main milestones in the progress model.
   */
  public BananaNode[] toArray() {
    int length = size;
    BananaNode firstMilestone = head;
    while(firstMilestone.getPrev() != null) {
      firstMilestone = firstMilestone.getPrev();
      length++;
    }
    BananaNode[] milestones = new BananaNode[length];
    for(int i = 0; i < length; i++) {
      milestones[i] = firstMilestone;
      firstMilestone = firstMilestone.getNext();
    }
    return milestones;
  }

  @Override
  public String toString() {
    String str = String.format("Progress Model For %s\nSize: %d\n", head.getName(), size);
    BananaNode[] arr = toArray();
    for(int i = 0; i < arr.length; i++) {
      if(arr[i] != null) {
        str += arr[i] + "\n\n";
      }
    }
    return str;
  }
}
