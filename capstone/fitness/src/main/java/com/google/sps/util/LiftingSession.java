package com.google.sps.util;

public class LiftingSession {
    
  private long timestamp;
  private int reps;
  private float weight;
  private String date;
  /**
  * Constructor for Marathon Session.
  *
  * @param  timestamp   The timestamp generated when the user completed the session.
  * @param  date        The date that the user completed the session. It will be in format YYYY/MM/DD.
  * @return             A Marathon session object.
  */
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