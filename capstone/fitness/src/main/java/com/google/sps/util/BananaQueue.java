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

public class BananaQueue {

  private BananaNode head, foot;
  private int size;

  public BananaQueue() {
    head = null;
    foot = null;
    size = 0;
  }
  
  /*
  * enqueuePeel - Enqueues given PeelNode to PeelQueue associated with tag if PeelNode has a value and the BananaQueue's front element
  *               has a PeelQueue with the same associated name.
  *
  * @param peelQueueTag - Tag to associated PeelQueue.
  * @param peel - PeelNode to add.
  */
  public void enqueuePeel(String peelQueueTag, PeelNode peel) {
    if(peel == null || head == null || !head.peelQueueExists(peelQueueTag)) {
      return;
    }

    head.getPeelQueue(peelQueueTag).enqueue(peel);
  }

  /*
  * dequeuePeel - Dequeues PeelQueue associated with the tag if the BananaQueue's front element
  *               has a PeelQueue with the same associated name.
  *
  * @param peelQueueTag - Tag to associated PeelQueue.
  */
  public void dequeuePeel(String peelQueueTag) {
    if(head == null || !head.peelQueueExists(peelQueueTag)) {
      return;
    }

    head.getPeelQueue(peelQueueTag).dequeue();
  }

  /*
  * addPeelQueue - Adds generic PeelQueue to the front element of the BananaQueue with the given tag. 
  *
  * @param peelQueueTag - Tag to associated PeelQueue.
  */
  public void addPeelQueue(String peelQueueTag) {
    addPeelQueue(peelQueueTag, new PeelQueue());
  }
  
  /*
  * addPeelQueue - Adds given PeelQueue to the front element of the BananaQueue with the given tag. 
  *
  * @param peelQueueTag - Tag to access PeelQueue.
  * @param peelQueue - PeelQueue to add.
  */
  public void addPeelQueue(String peelQueueTag, PeelQueue peelQueue) {
    if(head == null || head.peelQueueExists(peelQueueTag)) {
      return;
    }

    head.addPeelQueue(peelQueueTag, peelQueue);
  }

  /*
  * removePeelQueue - Removes PeelQueue from the front element of the BananaQueue with the given tag. 
  *
  * @param peelQueueTag - Tag to access PeelQueue.
  */
  public void removePeelQueue(String peelQueueTag) {
    if(head == null || !head.peelQueueExists(peelQueueTag)) {
      return;
    }

    head.removePeelQueue(peelQueueTag);
  }

  /*
  * enqueue - Adds BananaNode to the end of the queue and increments the size.
  * 
  * @param banana - BananaNode to add
  */
  public void enqueueBanana(BananaNode banana) {
    if(banana == null) {
      return;
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
  }
  
  /*
  * dequeue - Marks the front BananaNode in the queue as complete, decrements the size, 
  *           moves the front of the queue to the next element (can be null), and copies
  *           all the PeelQueues in the old front to the new front (if not null).
  */
  public void dequeueBanana() {
    if(head == null) {
      return;
    }

    head.setComplete(true);
    if(head.getNext() != null) {
      head.getNext().addPeels(head.getPeels());
    }
    head.removePeels();
    head = head.getNext();
    size--;
  }

  /*
  * peekBanana - Returns the front BananaNode of the queue (can be null).
  */
  public BananaNode peekBanana() {
    return head;
  }

  /*
  * peekPeel - Returns the front PeelNode of the queue (can be null) given an associated tag.
  *
  * @param peelQueueTag - Tag to associated PeelQueue.
  */
  public PeelNode peekPeel(String peelQueueTag) {
    if(head == null || !head.peelQueueExists(peelQueueTag)) {
      return null;
    }
    return head.getPeelQueue(peelQueueTag).peek(); 
  }
  
  /*
  * getSize - Returns the number of BananaNodes in the BananaQueue.
  */
  public int getSize() {
    return size;
  }

  /*
  * getPeelSize - Returns the number of PeelNodes in the PeelQueue of the associated tag. If not
  *               found, then it returns "-1".
  *
  * @param peelQueueTag - Tag to associated PeelQueue.
  */
  public int getPeelSize(String peelQueueTag) {
    if(head == null || !head.peelQueueExists(peelQueueTag)) {
      return -1;
    }
    return head.getPeelQueue(peelQueueTag).getSize();
  }
  
  public void setSize(int size) {
    this.size = size;
  }

  /*
  * toArray - Returns array representation of queue. [Currently deciding best approach]
  */
  public BananaNode[] toArray() {
    return null;
  }

}
