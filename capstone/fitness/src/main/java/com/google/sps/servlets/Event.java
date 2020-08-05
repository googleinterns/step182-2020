package com.google.sps.servlets;

import java.util.Date;
import java.util.UUID;
import static java.lang.Math.*;

final class Event {
  public final String id;
  
  // something better than date?
  public Date date;
  public String title;
  public String description; 

  public Event(Date date, String title, String description){
    this.date = date;
    this.title=title;
    this.description=description;
    // TODO (@piercedw) : Figure out how to generate ids. Ask Aaron?
    // Do I even need to rlly do this for mock data? 
    this.id = title;
      
  }
  public String getID(){
    return this.id;
  }
  
  public String getTitle(){
    return this.title;
  }
  
  public String getDescription(){
    return this.description;
  }

  public Date getDate(){
    return this.date;
  }
}