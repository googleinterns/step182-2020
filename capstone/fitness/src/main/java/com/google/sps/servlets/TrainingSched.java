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
}
