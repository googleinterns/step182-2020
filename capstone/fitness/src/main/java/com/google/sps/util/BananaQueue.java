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

import java.util.Optional;

import java.io.Serializable;

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
public class BananaQueue {

  private BananaNode head, foot;
  private int size;

  public BananaQueue() {
    head = null;
    foot = null;
    size = 0;
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
    if(peel == null || head == null || !head.peelQueueExists(peelQueueTag)) {
      return false;
    }

    return head.getPeelQueue(peelQueueTag).enqueue(peel);
  }

  /**
   * Dequeues PeelQueue associated with the tag if the BananaQueue's front element has a PeelQueue with the same associated name.
   *
   * @param peelQueueTag Tag to associated PeelQueue.
   * @return dequeued PeelNode.
   */
  public PeelNode dequeuePeel(String peelQueueTag) {
    if(head == null || !head.peelQueueExists(peelQueueTag)) {
      return null;
    }

    return head.getPeelQueue(peelQueueTag).dequeue();
  }

  /**
   * Adds generic PeelQueue to the front element of the BananaQueue with the given tag. 
   *
   * @param peelQueueTag Tag to associated PeelQueue.
   * @return true if add is successful.
   */
  public boolean addPeelQueue(String peelQueueTag) {
    return addPeelQueue(peelQueueTag, new PeelQueue());
  }
  
  /**
   * Adds given PeelQueue to the front element of the BananaQueue with the given tag
   * if it does not exist already. 
   *
   * @param peelQueueTag Tag to access PeelQueue.
   * @param peelQueue PeelQueue to add.
   * @return true if add is successful.
   */
  public boolean addPeelQueue(String peelQueueTag, PeelQueue peelQueue) {
    if(head == null || head.peelQueueExists(peelQueueTag)) {
      return false;
    }

    head.addPeelQueue(peelQueueTag, peelQueue);
    return true;
  }

  /**
   * Removes PeelQueue from the front element of the BananaQueue with the given tag. 
   *
   * @param peelQueueTag Tag to access PeelQueue.
   * @return removed PeelQueue.
   */
  public PeelQueue removePeelQueue(String peelQueueTag) {
    if(head == null || !head.peelQueueExists(peelQueueTag)) {
      return null;
    }

    return head.removePeelQueue(peelQueueTag);
  }

  /**
   * Adds BananaNode to the end of the queue and increments the size.
   * 
   * @param banana BananaNode to add.
   * @return true if enqueue is successful.
   */
  public boolean enqueueBanana(BananaNode banana) {
    if(banana == null) {
      return false;
    }

    if(head == null) {
      head = banana;
    }
    else { 
      BananaNode temp = foot != null ? foot : head;
      foot = banana;
      temp.setNext(foot);
      foot.setPrev(temp);
    }

    size++;
    return true;
  }
  
  /**
   * Marks the front BananaNode in the queue as complete, decrements the size, moves the 
   * front of the queue to the next element (can be null), and copies all the PeelQueues 
   * in the old front to the new front (if not null).
   *
   * @return dequeued BananaNode.
   */
  public BananaNode dequeueBanana() {
    if(head == null) {
      return null;
    }

    head.setComplete(true);
    if(head.getNext() != null) {
      head.getNext().addPeels(head.getPeels());
    }
    head.removePeels();
    BananaNode temp = head;
    head = head.getNext();
    size--;
    return temp;
  }

  /**
   * Returns the front BananaNode of the queue (can be null).
   * 
   * @return front of BananaQueue.
   */
  public BananaNode peekBanana() {
    return head;
  }

  /**
   * Returns the front PeelNode of the queue (can be null) given an associated tag.
   *
   * @param peelQueueTag Tag to associated PeelQueue.
   * @return front of PeelQueue mapped to tag.
   */
  public PeelNode peekPeel(String peelQueueTag) {
    if(head == null || !head.peelQueueExists(peelQueueTag)) {
      return null;
    }
    return head.getPeelQueue(peelQueueTag).peek(); 
  }
  
  /**
   * Returns the number of BananaNodes in the BananaQueue.
   * 
   * @return number of BananaNodes in the BananaQueue.
   */
  public int getSize() {
    return size;
  }

  /**
   * Returns the number of PeelNodes in the PeelQueue of the associated tag.
   *
   * @param peelQueueTag Tag to associated PeelQueue.
   * @return number of PeelNodes in the PeelQueue mapped to tag as an Optional object.
   */
  public Optional<Integer> getPeelSize(String peelQueueTag) {
    Optional<Integer> opt = Optional.empty();
    if(head != null && head.peelQueueExists(peelQueueTag)) {
      opt = Optional.of(head.getPeelQueue(peelQueueTag).getSize());
    }
    return opt;
  }

  /**
   * Returns array representation of queue.
   * TODO(ijelue): Figure out implementation.
   *
   * @return array representation of queue.
   */
  public BananaNode[] toArray() {
    return null;
  }

}
