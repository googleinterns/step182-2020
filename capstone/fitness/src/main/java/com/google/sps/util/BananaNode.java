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

package com.google.sps.util;

import java.util.*;

public abstract class BananaNode {
  private boolean complete;
  private BananaNode prevNode, nextNode;
  private HashMap<String, PeelQueue> peels;

  public BananaNode() {
    complete = false;
    prevNode = null;
    nextNode = null;
    peels = new HashMap<>();
  }

  public void addPeels(HashMap<String, PeelQueue> peels) {
    this.peels.putAll(peels);
  }

  public HashMap<String, PeelQueue> getPeels() {
    return peels;
  }

  public void removePeels() {
    peels.clear();
  }

  public void addPeelQueue(String peelQueueTag, PeelQueue peelQueue) {
    peels.put(peelQueueTag.toLowerCase(), peelQueue);
  }

  public void removePeelQueue(String peelQueueTag) {
    peels.remove(peelQueueTag);
  }

  public boolean peelQueueExists(String peelQueueTag) {
    return peels.containsKey(peelQueueTag);
  }

  public PeelQueue getPeelQueue(String peelQueueTag) {
    return peels.get(peelQueueTag);
  }

  public boolean isComplete() {
    return complete;
  }
  
  public void setComplete(boolean complete) {
    this.complete = complete;
  }

  public BananaNode getPrev() {
    return prevNode;
  }

  public void setPrev(BananaNode prevNode) {
    this.prevNode = prevNode;
  }
  
  public BananaNode getNext() {
    return nextNode;
  }
  
  public void setNext(BananaNode nextNode) {
    this.nextNode = nextNode;
  }

}
