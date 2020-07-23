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

public abstract class BananaQueue {

  private BananaNode head, foot;
  private int size;

  public BananaQueue() {
    head = null;
    foot = null;
    size = 0;
  }

  public void enqueuePeel(String peelQueueTag, PeelNode peel) {
    if(peel == null || head == null || !head.peelQueueExists(peelQueueTag)) {
      return;
    }

    head.getPeelQueue(peelQueueTag).enqueue(peel);
  }

  public void dequeuePeel(String peelQueueTag) {
    if(head == null || !head.peelQueueExists(peelQueueTag)) {
      return;
    }

    head.getPeelQueue(peelQueueTag).dequeue();
  }

  public void addPeelQueue(String peelQueueTag, PeelQueue peelQueue) {
    if(head == null || head.peelQueueExists(peelQueueTag)) {
      return;
    }

    head.addPeelQueue(peelQueueTag, peelQueue);
  }

  public void removePeelQueue(String peelQueueTag) {
    if(head == null || !head.peelQueueExists(peelQueueTag)) {
      return;
    }

    head.removePeelQueue(peelQueueTag);
  }

  public void enqueueBanana(BananaNode banana) {
    if(banana == null) {
      return;
    }

    if(head == null) {
      head = banana;
    }
    else if(foot == null) {
      foot = banana;
      head.setNext(foot);
      foot.setPrev(head);
    }
    else {
      BananaNode temp = foot;
      foot = banana;
      temp.setNext(foot);
      foot.setPrev(temp);
    }

    size++;
  }
  
  public void dequeueBanana() {
    if(head == null) {
      return;
    }

    head.setComplete(true);
    head.getNext().addPeels(head.getPeels());
    head.removePeels();
    head = head.getNext();
    size--;
  }

  public BananaNode peekBanana() {
    return head;
  }

  public PeelNode peekPeel(String peelQueueTag) {
    if(!head.peelQueueExists(peelQueueTag)) {
      return null;
    }
    return head.getPeelQueue(peelQueueTag).peek(); 
  }
  
  public int getSize() {
    return size;
  }

  public int peelSize(String peelQueueTag) {
    if(!head.peelQueueExists(peelQueueTag)) {
      return -1;
    }
    return head.getPeelQueue(peelQueueTag).getSize();
  }
  
  public void setSize(int size) {
    this.size = size;
  }

  public BananaNode[] toArray() {
    return null;
  }

}
