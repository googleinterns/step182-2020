package com.google.sps.servlets;
import java.util.*;

public class TrainingSched {
  // User that this schedule belongs to.
  String username;
  // List of workouts that the user is supposed to complete. 
  List workouts;
  public TrainingSched(String username){
    this.username = username;
    this.workouts =  new ArrayList<Workout>(); 
  }

  public void addWorkout(String eventID, String description){
    this.workouts.add(new Workout(eventID, description));
  }
  // using this addWorkout until authentication and datastore are imnplemented.
  public void addWorkout(String description){
    this.workouts.add(new Workout(description));
  }
  
  public String getUsername(){
    return this.username;
  }
  public List getWorkouts(){
    return this.workouts;
  }
  //function createPlan takes in info about user and uses it to populate the TrainingSched's 
  // workouts array with the appropriate workouts
  public void createPlan(int age, int wks, double goal, double currentMileTime){ 
    String restString = "Rest Day. You earned it!"; // string to go with each rest day. 
    int longRunNum;
    int restNum;
      
    //number of how many of each workout in a week. 
    // ratio of runs to rests is dependent on the user's age.
    if (age <25){
      longRunNum = 6;
      restNum =2;}
    else if (age>24 && age<35){
      longRunNum = 5;
      restNum =2;}
    else if (age>34 && age <50){
      longRunNum = 4;
      restNum =3;}
    else {
      longRunNum = 3;
      restNum =4;}
      
    double longRunLength = Math.floor(goal / wks * 100)/100; // length of run to begin with
    if (longRunLength<1){longRunLength = 1;}
    
    // TODO (piercedw@): Create currentDay and increment after each new event is created. 
    for (int i = 0; i<wks; i++){
      for (int b = 0; b<longRunNum; b++){
        this.addWorkout("run " + longRunLength + " miles");
        // TODO (piercedw@): Create as event in calendar and store eventID.
        }
    for (int b = 0; b<restNum; b++){
      this.addWorkout(restString);
      // TODO (piercedw@): Create as event in calendar and store eventID.
    }
    longRunLength +=  Math.floor(goal/wks * 100) / 100; // increment the distance the user runs at the end of each week.
    }
  }
}
