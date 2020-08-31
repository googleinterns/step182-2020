package com.google.sps.util;

public class LiftingSession {
    
  private long timestamp;
  private int reps;
  private float weight;
  private String date;
  
  public LiftingSession(long timestamp, int reps, float weight, String date) {
    this.timestamp = timestamp;
    this.reps = reps;
    this.weight = weight;
    this.date = date;
  }

  public int getReps() {
    return reps;
  }

  public float getWeight() {
    return weight;
  }

}