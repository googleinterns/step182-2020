import java.util.*;
// import Workout.java;

public class TrainingSched {
  // User that this schedule belongs to.
  String username;
  // List of workouts that the user is supposed to complete. 
  List workouts;
 
  public TrainingSched(String username){
    this.username = username;
    this.workouts =  new ArrayList<Workout>();
   
  }
  public void addWorkout(Workout workout){
      this.workouts.add(workout);
  }

  public void addWorkout(String eventID, String description){
      this.workouts.add(new Workout(eventID, description));
  }

  //function createPln takes in info about user and uses it to populate the TrainingSched's 
  // workouts array with the appropriate workouts
  public void createPlan(int age, int wks, double goal, double currentMileTime){
      
      //workouts scheduled by week so this will just make it easier
      int daysInAWeek = 7;

      //number of how many of each workout in a week
      int longRunNum; // # of "long runs" per week. Between 3 and 5. Depends on goal and # of weeks. 
      


  }
}
