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

import java.io.Serializable;
import java.util.*;

/*
 * Queue with the ability to hold multiple queues at each node.  
 * Each dequeue results in a transfer of queues to the next node.
 * Each node is called a BananaNode.
 * Each extra queue is called a PeelQueue.
 * Every node in a PeelQueue is called a PeelNode.
 *
 * Example Structure:
 *
 *    @1 - @2 - @3
 *   /
 * O - O - O - O - O
 *   \
 *    #1 - #2
 *
 * Example Dequeue:
 *
 *        @1 - @2 - @3
 *       /
 * X - O - O - O - O
 *       \
 *        #1 - #2
 *
 * Key:
 *   X = Completed BananaNode
 *   O = Uncompleted BananaNode
 *   (/, -, \) = Connections
 *   @n = PeelNode
 *   #n = PeelNode
 */

/* Nodes in the BananaQueue. Holds references to PeelQueues. */
public class BananaNode implements Serializable {
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

  /**
   * Enqueues given PeelNode to PeelQueue associated with tag if PeelNode has a value and the BananaQueue's front element
   * has a PeelQueue with the same associated name.
   *
   * @param peelQueueTag Tag to associated PeelQueue.
   * @param peel PeelNode to add.
   * @return true if enqueue is successful.
   */
  public boolean enqueuePeel(String peelQueueTag, PeelNode peel) {
    if(peel == null || !peelQueueExists(peelQueueTag)) {
      return false;
    }

    return getPeelQueue(peelQueueTag).enqueue(peel);
  }
 
  /**
   * Dequeues PeelQueue associated with the tag if the BananaQueue's front element has a PeelQueue with the same associated name.
   *
   * @param peelQueueTag Tag to associated PeelQueue.
   * @return dequeued PeelNode.
   */
  public PeelNode dequeuePeel(String peelQueueTag) {
    if(!peelQueueExists(peelQueueTag)) {
      return null;
    }

    return getPeelQueue(peelQueueTag).dequeue();
  }

  /**
   * Adds BananaNode to the end of the queue and increments the size.
   * 
   * @param banana BananaNode to add.
   * @return true if enqueue is successful.
   */
  public boolean enqueue(BananaNode banana) {
    BananaNode current = this;

    while(current.getNext() != null) {
      current = current.getNext();
    }

    current.setNext(banana);
    banana.setPrev(current);
    return true;
  }
  
  /**
   * Marks the front BananaNode in the queue as complete, decrements the size, moves the 
   * front of the queue to the next element (can be null), and copies all the PeelQueues 
   * in the old front to the new front (if not null).
   *
   * @return dequeued BananaNode.
   */
  public BananaNode dequeue() {
    BananaNode current = this;

    while(!current.isHead()) {
      current = current.getPrev();
    }

    current.setComplete(true);  
    if(current.getNext() != null) {
      current.getNext().addPeels(current.getPeels());
    }
    current.removePeels();
    
    return current;
  }

  /**
   * Returns the front PeelNode of the queue (can be null) given an associated tag.
   *
   * @param peelQueueTag Tag to associated PeelQueue.
   * @return front of PeelQueue mapped to tag.
   */
  public PeelNode peekPeel(String peelQueueTag) {
    if(!peelQueueExists(peelQueueTag)) {
      return null;
    }
    return getPeelQueue(peelQueueTag).peek(); 
  }

  /**
   * Returns the number of PeelNodes in the PeelQueue of the associated tag.
   *
   * @param peelQueueTag Tag to associated PeelQueue.
   * @return number of PeelNodes in the PeelQueue mapped to tag as an Optional object.
   */
  public Optional<Integer> getPeelSize(String peelQueueTag) {
    Optional<Integer> opt = Optional.empty();
    if(peelQueueExists(peelQueueTag)) {
      opt = Optional.of(getPeelQueue(peelQueueTag).getSize());
    }
    return opt;
  }

  public boolean isHead() {
    return prevNode == null || prevNode.isComplete();
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
