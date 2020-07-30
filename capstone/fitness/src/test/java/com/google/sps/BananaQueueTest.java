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

public class BananaQueueTest {
  
  private final String peelQueueTag = "test";

  @Test
  public void testEnqueuePeel() {
    BananNode bq = new BananaQueue();

    // Try to add Peel without PeelQueue or BananaNode to attach to.
    assertFalse(bq.enqueuePeel(peelQueueTag, new PeelNode()));
    assertFalse(bq.getPeelSize(peelQueueTag).isPresent());
    
    // Try to add Peel without PeelQueue to attach to.
    assertTrue(bq.enqueueBanana(new BananaNode()));
    assertFalse(bq.enqueuePeel(peelQueueTag, new PeelNode()));
    assertFalse(bq.getPeelSize(peelQueueTag).isPresent());
    
    // Add Peel.
    assertTrue(bq.addPeelQueue(peelQueueTag));
    assertTrue(bq.enqueuePeel(peelQueueTag, new PeelNode()));
    assertTrue(bq.getPeelSize(peelQueueTag).get() == 1);
  }

  @Test
  public void testDequeuePeel() {
    BananaQueue bq = new BananaQueue();
    PeelNode pn = new PeelNode();

    // Try to remove Peel without PeelQueue or BananaNode to remove from.
    assertTrue(bq.dequeuePeel(peelQueueTag) == null);
    assertFalse(bq.getPeelSize(peelQueueTag).isPresent());
    
    // Try to remove Peel without PeelQueue to remove from.
    assertTrue(bq.enqueueBanana(new BananaNode()));
    assertTrue(bq.dequeuePeel(peelQueueTag) == null);
    assertFalse(bq.getPeelSize(peelQueueTag).isPresent());
    
    // Try to remove Peel From PeelQueue that contains no elements.
    assertTrue(bq.addPeelQueue(peelQueueTag));
    assertTrue(bq.dequeuePeel(peelQueueTag) == null);
    assertTrue(bq.getPeelSize(peelQueueTag).get() == 0);

    // Remove Peel.
    assertTrue(bq.enqueuePeel(peelQueueTag, pn));
    assertTrue(bq.getPeelSize(peelQueueTag).get() == 1);
    assertTrue(Objects.deepEquals(bq.dequeuePeel(peelQueueTag), pn));
    assertTrue(bq.getPeelSize(peelQueueTag).get() == 0);
  }

  @Test
  public void testAddPeelQueue() {
    String peelQueueTag2 = peelQueueTag + "2";
    BananaQueue bq = new BananaQueue();
    PeelQueue pq = new PeelQueue();
    
    // Try to add PeelQueues without BananaNode to attach to.
    assertFalse(bq.addPeelQueue(peelQueueTag));
    assertFalse(bq.getPeelSize(peelQueueTag).isPresent());
    assertFalse(bq.addPeelQueue(peelQueueTag2, pq));
    assertFalse(bq.getPeelSize(peelQueueTag2).isPresent());

    assertTrue(bq.enqueueBanana(new BananaNode()));

    // Add PeelQueues.
    assertTrue(bq.addPeelQueue(peelQueueTag));
    assertTrue(bq.addPeelQueue(peelQueueTag2, pq));
    assertTrue(bq.getPeelSize(peelQueueTag).get() == 0);
    assertTrue(bq.getPeelSize(peelQueueTag2).get() == 0);
    assertTrue(bq.peekBanana().getPeels().size() == 2);
  }

  @Test
  public void testAddPeelQueueUpperCase() {
    String peelQueueTagUpper = peelQueueTag.toUpperCase();
    BananaQueue bq = new BananaQueue();

    assertTrue(bq.enqueueBanana(new BananaNode()));

    // Add PeelQueues.
    assertTrue(bq.addPeelQueue(peelQueueTag));
    assertFalse(bq.addPeelQueue(peelQueueTagUpper));

    // Test that they are the same.
    assertTrue(bq.getPeelSize(peelQueueTag).get() == 0);
    assertTrue(bq.getPeelSize(peelQueueTagUpper).get() == 0);
    assertTrue(bq.peekBanana().getPeels().size() == 1);
  }

  @Test
  public void testRemovePeelQueue() {
    BananaQueue bq = new BananaQueue();
    
    // Try to remove PeelQueue without BananaNode to remove from.
    assertFalse(bq.getPeelSize(peelQueueTag).isPresent());
    assertTrue(bq.removePeelQueue(peelQueueTag) == null);
    assertFalse(bq.getPeelSize(peelQueueTag).isPresent());

    // Add PeelQueue to BananaNode.
    assertTrue(bq.enqueueBanana(new BananaNode()));
    assertTrue(bq.addPeelQueue(peelQueueTag));
    assertTrue(bq.getPeelSize(peelQueueTag).get() == 0);

    // Remove PeelQueue from BananaNode.
    assertTrue(bq.removePeelQueue(peelQueueTag) != null);
    assertFalse(bq.getPeelSize(peelQueueTag).isPresent());
    assertTrue(bq.peekBanana().getPeels().size() == 0);
  }

  @Test
  public void testEnqueueBanana() {
    BananaQueue bq = new BananaQueue();

    assertTrue(bq.getSize() == 0);

    assertTrue(bq.enqueueBanana(new BananaNode()));
    assertTrue(bq.getSize() == 1);

    assertTrue(bq.enqueueBanana(new BananaNode()));
    assertTrue(bq.getSize() == 2);
  }

  @Test
  public void testDequeueBanana() {
    BananaQueue bq = new BananaQueue();
    BananaNode bn = new BananaNode();

    assertTrue(bq.getSize() == 0);
    assertTrue(bq.peekBanana() == null);

    assertTrue(bq.enqueueBanana(bn));
    assertTrue(bq.getSize() == 1);
    assertTrue(bq.peekBanana() != null);

    assertTrue(Objects.deepEquals(bq.dequeueBanana(), bn));
    assertTrue(bq.getSize() == 0);
    assertTrue(bq.peekBanana() == null);
  }

  @Test
  public void testDequeueBananaWithPeels() {
    String peelQueueTag2 = peelQueueTag + "2";
    BananaQueue bq = new BananaQueue();
    BananaNode bn = new BananaNode();

    // Add a BananaNode to BananaQueue with 2 PeelQueues (1st PeelQueue contains one PeelNode).
    assertTrue(bq.enqueueBanana(bn));
    assertTrue(bq.addPeelQueue(peelQueueTag));
    assertTrue(bq.enqueuePeel(peelQueueTag, new PeelNode()));
    assertTrue(bq.addPeelQueue(peelQueueTag2));
    assertTrue(bq.getPeelSize(peelQueueTag).get() == 1);
    assertTrue(bq.getPeelSize(peelQueueTag2).get() == 0);
    assertTrue(bq.peekBanana().getPeels().size() == 2);
    assertFalse(bq.peekBanana().isComplete());
    assertTrue(bq.peekBanana().getPrev() == null);

    assertTrue(bq.enqueueBanana(new BananaNode()));
    assertTrue(bq.getSize() == 2);

    // Make sure correct BananaNode is returned from dequeue.
    assertTrue(Objects.deepEquals(bq.dequeueBanana(), bn));
    assertFalse(Objects.deepEquals(bq.peekBanana(), bn));

    // Test transfer of PeelNodes and status of BananaNode head is correct.
    assertTrue(bq.getPeelSize(peelQueueTag).get() == 1);
    assertTrue(bq.getPeelSize(peelQueueTag2).get() == 0);
    assertTrue(bq.peekBanana().getPeels().size() == 2);
    assertFalse(bq.peekBanana().isComplete());
    assertTrue(bq.peekBanana().getPrev() != null);
    assertTrue(bq.peekBanana().getPrev().isComplete());
  }

  @Test
  public void testToArray() {
    BananaQueue bq = new BananaQueue();
    assertTrue(bq.toArray() == null);
  }
}