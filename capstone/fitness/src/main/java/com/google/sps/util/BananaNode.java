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

/* Nodes in the BananaQueue. Holds references to PeelQueues. */
public class BananaNode {
  private boolean complete;
  
  /* Uses Linked List model. */
  private BananaNode prevNode, nextNode;

  /* String tag mappings for PeelQueue's. */
  private HashMap<String, PeelQueue> peels;

  public BananaNode() {
    complete = false;
    prevNode = null;
    nextNode = null;
    peels = new HashMap<>();
  }

  /**
   * Adds PeelQueues to BananaNode. Overwrites existing mappings.
   *
   * @param peels String mapping of PeelQueues to be added to current BananaNode.
   */
  public void addPeels(HashMap<String, PeelQueue> peels) {
    this.peels.putAll(peels);
  }

  public HashMap<String, PeelQueue> getPeels() {
    return peels;
  }

  /**
   * Clears the PeelQueue String mappings.
   */
  public void removePeels() {
    peels.clear();
  }

  /**
   * Adds String mapping to given PeelQueue. Converts tag to lowercase.
   * 
   * @param peelQueueTag Tag to access PeelQueue.
   * @param peelQueue PeelQueue to add.
   */
  public void addPeelQueue(String peelQueueTag, PeelQueue peelQueue) {
    peels.put(peelQueueTag.toLowerCase(), peelQueue);
  }

  /**
   * Removes String mapping to stored PeelQueue given a tag. Converts tag to lowercase.
   * 
   * @param peelQueueTag Tag to associated PeelQueue.
   * @return removed PeelQueue
   */
  public PeelQueue removePeelQueue(String peelQueueTag) {
    return peels.remove(peelQueueTag.toLowerCase());
  }

  /**
   * Returns true if String mapping for PeelQueue exists. Converts tag to lowercase.
   * 
   * @param peelQueueTag Tag to associated PeelQueue.
   * @return if String mapping for PeelQueue exists.
   */
  public boolean peelQueueExists(String peelQueueTag) {
    return peels.containsKey(peelQueueTag.toLowerCase());
  }

  /**
   * Returns PeelQueue given its tag. Converts tag to lowercase.
   * 
   * @param peelQueueTag Tag to associated PeelQueue.
   * @return PeelQueue mapped to tag
   */
  public PeelQueue getPeelQueue(String peelQueueTag) {
    return peels.get(peelQueueTag.toLowerCase());
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
