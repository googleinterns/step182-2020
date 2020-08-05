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

/* Nodes in the PeelQueue. */
public class PeelNode implements Serializable {
  private boolean complete;

  /* Uses Linked List model. */
  private PeelNode prevNode, nextNode;

  public PeelNode() {
    complete = false;
    prevNode = null;
    nextNode = null;
  }

  public boolean isComplete() {
    return complete;
  }
  
  public void setComplete(boolean complete) {
    this.complete = complete;
  }

  public PeelNode getPrev() {
    return prevNode;
  }

  public void setPrev(PeelNode prevNode) {
    this.prevNode = prevNode;
  }
  
  public PeelNode getNext() {
    return nextNode;
  }
  
  public void setNext(PeelNode nextNode) {
    this.nextNode = nextNode;
  }
}
