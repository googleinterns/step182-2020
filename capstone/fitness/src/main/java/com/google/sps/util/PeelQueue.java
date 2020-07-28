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

/* Queues referenced in BananaNodes. Holds PeelNodes. */
public class PeelQueue implements Serializable {

  private PeelNode head, foot;
  private int size;

  public PeelQueue() {
    head = null;
    foot = null;
    size = 0;
  }

  /**
   * Adds PeelNode to the end of the queue and increments the size.
   * 
   * @param peel PeelNode to add
   * @return true if enqueue is successful 
   */
  public boolean enqueue(PeelNode peel) {
    if(peel == null) {
      return false;
    }

    if(head == null) {
      head = peel;
    }
    else { 
      PeelNode temp = foot != null ? foot : head;
      foot = peel;
      temp.setNext(foot);
      foot.setPrev(temp);
    }

    size++;
    return true;
  }
  
  /**
   * Marks the front PeelNode in the queue as complete, decrements the size, and moves the front of 
   * the queue to the next element (can be null).
   *
   * @return dequeued PeelNode (can be null)
   */
  public PeelNode dequeue() {
    if(head == null) {
      return null;
    }

    head.setComplete(true);
    PeelNode temp = head;
    head = head.getNext();
    size--;
    return temp;
  }

  /**
   * Returns the front PeelNode of the queue (can be null).
   *
   * @return front PeelNode of the queue
   */
  public PeelNode peek() {
    return head;
  }
  
  public int getSize() {
    return size;
  }
  
  /**
   * Returns array representation of queue.
   * TODO(ijelue): Figure out implementation.
   *
   * @return array representation of queue.
   */
  public PeelNode[] toArray() {
    return null;
  }

}
