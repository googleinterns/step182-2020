package com.google.sps.util;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
  Creates an instance of a marathon session.
  This object stores a timespamp, time, and date.
  In the progress servet, it gets added to an arraylist, 
  and then GSON converts it into a JSON string of sessions
  to be stored in datastore.
*/
public class MarathonSession {
    
  private long timestamp;
  private float speed;
  private String date;
  /**
  * Constructor for Marathon Session.
  *
  * @param  timestamp   The timestamp generated when the user completed the session.
  * @param  speed       The time that the user took to complete the session (in hours).
  * @param  date        The date that the user completed the session. It will be in format YYYY/MM/DD.
  * @return             A Marathon session object.
  */
  public MarathonSession(long timestamp, float speed, String date) {
    this.timestamp = timestamp;
    this.speed = speed;
    this.date = date;
  }

  public float getSpeed() {
    return speed;
  }

  public String toString() {
    return timestamp + " " + speed + " " + date;
  }

}