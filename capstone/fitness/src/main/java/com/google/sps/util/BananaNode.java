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

  private HashMap<String, PeelQueue> peels;

  public BananaNode() {
    complete = false;
    prevNode = null;
    nextNode = null;
    peels = new HashMap<>();
  }

  /*
  * addPeels - Adds PeelQueues to BananaNode. Overwrites existing mappings.
  *
  * @param peels - String mapping of PeelQueues to be added to current BananaNode.
  */
  public void addPeels(HashMap<String, PeelQueue> peels) {
    this.peels.putAll(peels);
  }

  public HashMap<String, PeelQueue> getPeels() {
    return peels;
  }

  /*
  * removePeels - Clears the PeelQueue String mappings.
  */
  public void removePeels() {
    peels.clear();
  }

  /*
  * addPeelQueue - Adds String mapping to given PeelQueue. Converts tag to lowercase.
  * 
  * @param peelQueueTag - Tag to access PeelQueue.
  * @param peelQueue - PeelQueue to add.
  */
  public void addPeelQueue(String peelQueueTag, PeelQueue peelQueue) {
    peels.put(peelQueueTag.toLowerCase(), peelQueue);
  }

  /*
  * removePeelQueue - Removes String mapping to stored PeelQueue given a tag. Converts tag to lowercase.
  * 
  * @param peelQueueTag - Tag to associated PeelQueue.
  */
  public void removePeelQueue(String peelQueueTag) {
    peels.remove(peelQueueTag.toLowerCase());
  }

  /*
  * peelQueueExists - Returns true if String mapping for PeelQueue exists.
  * 
  * @param peelQueueTag - Tag to associated PeelQueue.
  */
  public boolean peelQueueExists(String peelQueueTag) {
    return peels.containsKey(peelQueueTag);
  }

  /*
  * getPeelQueue - Returns PeelQueue given its tag.
  * 
  * @param peelQueueTag - Tag to associated PeelQueue.
  */
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
