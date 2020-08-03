package com.google.sps.servlets;

//Workout class. Stores info and eventID, which corresponds to the database.

public class Workout {
  String eventID; //generated ID from API.
  String description; //description of the workout ie, "run x miles".
 
  public Workout(String eventID, String description){
    this.eventID=eventID;
    this.description=description;
  }
  public Workout(String description){
      this.description = description;
  }
 
  public String toString(){
      return this.eventID + ": " + this.description;
  }
 
  public String getEventID(){
      return this.eventID;
  }
 
  public String getDescription(){
      return this.description;
  }
 
  public void setEventID(String eventID){
      this.eventID = eventID;
  }
 
  public void setDescription(String description){
      this.description=description; 
  }
}
