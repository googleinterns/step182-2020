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

public class BananaQueueTest {
  
  @Test
  public void testEnqueuePeel() {
    String peelQueueTag = "test";
    BananaQueue bq = new BananaQueue();
    bq.enqueuePeel(peelQueueTag, new PeelNode());
    assertTrue(bq.getPeelSize(peelQueueTag) == -1);
    
    bq.enqueueBanana(new BananaNode());
    bq.enqueuePeel(peelQueueTag, new PeelNode());
    assertTrue(bq.getPeelSize(peelQueueTag) == -1);
    
    bq.addPeelQueue(peelQueueTag);
    bq.enqueuePeel(peelQueueTag, new PeelNode());
    assertTrue(bq.getPeelSize(peelQueueTag) == 1);
  }

  @Test
  public void testDequeuePeel() {
    String peelQueueTag = "test";
    BananaQueue bq = new BananaQueue();
    bq.dequeuePeel(peelQueueTag);
    assertTrue(bq.getPeelSize(peelQueueTag) == -1);
    
    bq.enqueueBanana(new BananaNode());
    bq.dequeuePeel(peelQueueTag);
    assertTrue(bq.getPeelSize(peelQueueTag) == -1);
    
    bq.addPeelQueue(peelQueueTag);
    bq.dequeuePeel(peelQueueTag);
    assertTrue(bq.getPeelSize(peelQueueTag) == 0);

    bq.enqueuePeel(peelQueueTag, new PeelNode());
    assertTrue(bq.getPeelSize(peelQueueTag) == 1);
    bq.dequeuePeel(peelQueueTag);
    assertTrue(bq.getPeelSize(peelQueueTag) == 0);
  }

  @Test
  public void testAddPeelQueue() {
    String peelQueueTag1 = "test";
    String peelQueueTag2 = "test2";
    BananaQueue bq = new BananaQueue();
    PeelQueue pq = new PeelQueue();
    
    bq.addPeelQueue(peelQueueTag1);
    assertTrue(bq.getPeelSize(peelQueueTag1) == -1);

    bq.addPeelQueue(peelQueueTag2, pq);
    assertTrue(bq.getPeelSize(peelQueueTag2) == -1);

    bq.enqueueBanana(new BananaNode());

    bq.addPeelQueue(peelQueueTag1);
    bq.addPeelQueue(peelQueueTag2, pq);
    assertTrue(bq.getPeelSize(peelQueueTag1) == 0);
    assertTrue(bq.getPeelSize(peelQueueTag2) == 0);
    assertTrue(bq.peekBanana().getPeels().size() == 2);
  }

  @Test
  public void testRemovePeelQueue() {
    String peelQueueTag = "test";
    BananaQueue bq = new BananaQueue();
    
    assertTrue(bq.getPeelSize(peelQueueTag) == -1);
    bq.removePeelQueue(peelQueueTag);
    assertTrue(bq.getPeelSize(peelQueueTag) == -1);

    bq.enqueueBanana(new BananaNode());
    bq.addPeelQueue(peelQueueTag);
    assertTrue(bq.getPeelSize(peelQueueTag) == 0);

    bq.removePeelQueue(peelQueueTag);
    assertTrue(bq.getPeelSize(peelQueueTag) == -1);
    assertTrue(bq.peekBanana().getPeels().size() == 0);
  }

  @Test
  public void testEnqueueBanana() {
    BananaQueue bq = new BananaQueue();

    assertTrue(bq.getSize() == 0);

    bq.enqueueBanana(new BananaNode());
    assertTrue(bq.getSize() == 1);

    bq.enqueueBanana(new BananaNode());
    assertTrue(bq.getSize() == 2);
  }

  @Test
  public void testDequeueBanana() {
    BananaQueue bq = new BananaQueue();

    assertTrue(bq.getSize() == 0);
    assertTrue(bq.peekBanana() == null);

    bq.enqueueBanana(new BananaNode());
    assertTrue(bq.getSize() == 1);
    assertTrue(bq.peekBanana() != null);

    bq.dequeueBanana();
    assertTrue(bq.getSize() == 0);
    assertTrue(bq.peekBanana() == null);
  }

  @Test
  public void testDequeueBananaWithPeels() {
    String peelQueueTag1 = "test";
    String peelQueueTag2 = "test2";
    BananaQueue bq = new BananaQueue();
    
    bq.enqueueBanana(new BananaNode());
    bq.addPeelQueue(peelQueueTag1);
    bq.enqueuePeel(peelQueueTag1, new PeelNode());
    bq.addPeelQueue(peelQueueTag2);
    assertTrue(bq.getPeelSize(peelQueueTag1) == 1);
    assertTrue(bq.getPeelSize(peelQueueTag2) == 0);
    assertTrue(bq.peekBanana().getPeels().size() == 2);
    assertFalse(bq.peekBanana().isComplete());
    assertTrue(bq.peekBanana().getPrev() == null);

    bq.enqueueBanana(new BananaNode());
    bq.addPeelQueue(peelQueueTag1);
    assertTrue(bq.getSize() == 2);

    bq.dequeueBanana();
    assertTrue(bq.getPeelSize(peelQueueTag1) == 1);
    assertTrue(bq.getPeelSize(peelQueueTag2) == 0);
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