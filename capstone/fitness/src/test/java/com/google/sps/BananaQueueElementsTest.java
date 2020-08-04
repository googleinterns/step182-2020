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

package com.google.sps;

import org.junit.*;
import static org.junit.Assert.*;
import com.google.sps.util.*;
import java.util.Objects;

public class BananaQueueElementsTest {
  
  private final String peelQueueTag = "test";

  @Test
  public void testEnqueuePeel() {
    BananaNode bn = new BananaNode();
    
    // Try to add Peel without PeelQueue to attach to.
    assertFalse(bn.enqueuePeel(peelQueueTag, new PeelNode()));
    assertFalse(bn.getSize(peelQueueTag).isPresent());
    
    // Add Peel.
    assertTrue(bn.addPeelQueue(peelQueueTag));
    assertTrue(bn.enqueuePeel(peelQueueTag, new PeelNode()));
    assertTrue(bn.getSize(peelQueueTag).get() == 1);
  }

  @Test
  public void testDequeuePeel() {
    BananaNode bn = new BananaNode();
    PeelNode pn = new PeelNode();
    
    // Try to remove Peel without PeelQueue to remove from.
    assertTrue(bn.dequeuePeel(peelQueueTag) == null);
    assertFalse(bn.getSize(peelQueueTag).isPresent());
    
    // Try to remove Peel From PeelQueue that contains no elements.
    assertTrue(bn.addPeelQueue(peelQueueTag));
    assertTrue(bn.dequeuePeel(peelQueueTag) == null);
    assertTrue(bn.getSize(peelQueueTag).get() == 0);

    // Remove Peel.
    assertTrue(bn.enqueuePeel(peelQueueTag, pn));
    assertTrue(bn.getSize(peelQueueTag).get() == 1);
    assertTrue(Objects.deepEquals(bn.dequeuePeel(peelQueueTag), pn));
    assertTrue(bn.getSize(peelQueueTag).get() == 0);
  }

  @Test
  public void testAddPeelQueue() {
    String peelQueueTag2 = peelQueueTag + "2";
    BananaNode bn = new BananaNode();
    PeelQueue pq = new PeelQueue();

    // Add PeelQueues.
    assertTrue(bn.addPeelQueue(peelQueueTag));
    assertTrue(bn.addPeelQueue(peelQueueTag2, pq));

    // Test Sizes.
    assertTrue(bn.getSize(peelQueueTag).get() == 0);
    assertTrue(bn.getSize(peelQueueTag2).get() == 0);
    assertTrue(bn.getPeels().size() == 2);
  }

  @Test
  public void testAddPeelQueueUpperCase() {
    String peelQueueTagUpper = peelQueueTag.toUpperCase();
    BananaNode bn = new BananaNode();

    // Add PeelQueues.
    assertTrue(bn.addPeelQueue(peelQueueTag));
    assertFalse(bn.addPeelQueue(peelQueueTagUpper));

    // Test that they are the same.
    assertTrue(bn.getSize(peelQueueTag).get() == 0);
    assertTrue(bn.getSize(peelQueueTagUpper).get() == 0);
    assertTrue(bn.getPeels().size() == 1);
  }

  @Test
  public void testRemovePeelQueue() {
    BananaNode bn = new BananaNode();
    
    // Try to remove PeelQueue without PeelQueue Present to remove from.
    assertFalse(bn.getSize(peelQueueTag).isPresent());
    assertTrue(bn.removePeelQueue(peelQueueTag) == null);
    assertFalse(bn.getSize(peelQueueTag).isPresent());

    // Add PeelQueue to BananaNode.
    assertTrue(bn.addPeelQueue(peelQueueTag));
    assertTrue(bn.getSize(peelQueueTag).get() == 0);

    // Remove PeelQueue from BananaNode.
    assertTrue(bn.removePeelQueue(peelQueueTag) != null);
    assertFalse(bn.getSize(peelQueueTag).isPresent());
    assertTrue(bn.getPeels().size() == 0);
  }

  @Test
  public void testEnqueueBanana() {
    BananaNode bn = new BananaNode();
    BananaNode bn2 = new BananaNode();

    // Test BananaNode behaviour when separate.
    assertTrue(bn.isHead());
    assertTrue(bn2.isHead());

    // Establish BananaQueue with nodes and determine heads and links.
    assertTrue(bn.enqueue(bn2));
    assertTrue(bn.isHead());
    assertFalse(bn2.isHead());
    assertTrue(bn2.getNext() == null);

    // Test enqueue from head. 
    assertTrue(bn.enqueue(new BananaNode()));
    assertTrue(bn.isHead());
    assertFalse(bn2.isHead());
    assertTrue(bn2.getNext() != null);
  }

  @Test
  public void testDequeueBanana() {
    BananaNode bn = new BananaNode();
    BananaNode bn2 = new BananaNode();

    // Establish BananaQueue with nodes and determine heads.
    assertTrue(bn.enqueue(bn2));
    assertTrue(bn.isHead());
    assertFalse(bn2.isHead());

    // Test BananaNode head status after dequeue.
    assertTrue(Objects.deepEquals(bn.dequeue(), bn));
    assertFalse(bn.isHead());
    assertTrue(bn2.isHead());
  }

  @Test
  public void testDequeueBananaWithPeels() {
    String peelQueueTag2 = peelQueueTag + "2";
    BananaNode bn = new BananaNode();
    BananaNode bn2 = new BananaNode();

    // Add 2 PeelQueues to BananaNode head (1st PeelQueue contains one PeelNode).
    assertTrue(bn.addPeelQueue(peelQueueTag));
    assertTrue(bn.enqueuePeel(peelQueueTag, new PeelNode()));
    assertTrue(bn.addPeelQueue(peelQueueTag2));
    assertTrue(bn.getSize(peelQueueTag).get() == 1);
    assertTrue(bn.getSize(peelQueueTag2).get() == 0);
    assertTrue(bn.getPeels().size() == 2);
    assertTrue(bn.isHead());

    assertTrue(bn.enqueue(bn2));
    assertFalse(bn2.isHead());

    // Make sure correct BananaNode is returned from dequeue.
    assertTrue(Objects.deepEquals(bn.dequeue(), bn));
    assertFalse(bn.isHead());

    // Test transfer of PeelNodes and status of BananaNode head is correct.
    assertTrue(bn2.getSize(peelQueueTag).get() == 1);
    assertTrue(bn2.getSize(peelQueueTag2).get() == 0);
    assertTrue(bn2.getPeels().size() == 2);
    assertTrue(bn2.isHead());
  }
}