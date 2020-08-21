package com.google.sps.util;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.sps.progress.*;
import java.text.SimpleDateFormat;
import java.util.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class DataHandler {

  // Constants used for datastore.
  public final static String USER_ENTITY = "user";
  public static Set<String> USER_PROPERTIES  = new HashSet<>();
  public final static String NAME_PROPERTY = "name";
  public final static String AGE_PROPERTY = "age";
  public final static String WORKOUT_LIST_PROPERTY = "workoutList";
  public final static String CALENDAR_ID_PROPERTY = "calendarId";
  public final static String EVENT_IDS_PROPERTY = "EventIds";

  public final static String WORKOUT_ENTITY = "workout";
  public static Set<String> WORKOUT_PROPERTIES = new HashSet<>();
  public final static String WORKOUT_NAME_PROPERTY = "workoutName";
  public final static String TYPE_PROPERTY = "workoutType";
  public final static String WEEKS_TO_TRAIN_PROPERTY = "weeksToTrain";
  public final static String GOAL_STEPS_PROPERTY = "goalSteps";
  public final static String PROGRESS_PROPERTY = "progress";

  public static Set<String> MARATHON_PROPERTIES = new HashSet<>();
  public final static String MARATHON_LENGTH_PROPERTY = "marathonLength";
  public final static String INITIAL_TIME_PROPERTY = "initialTime";
  public final static String GOAL_TIME_PROPERTY = "goalTime";
  public final static String MILE_TIME_PROPERTY = "mileTime";
  
  
  public static Set<String> LIFTING_PROPERTIES = new HashSet<>();
  public final static String INITIAL_WEIGHT_PROPERTY = "initialWeight";
  public final static String INITIAL_REPS_PROPERTY = "initialReps";
  public final static String GOAL_WEIGHT_PROPERTY = "goalWeight";
  public final static String GOAL_REPS_PROPERTY = "goalReps";

  static {
    USER_PROPERTIES.add(NAME_PROPERTY);
    USER_PROPERTIES.add(AGE_PROPERTY);
    USER_PROPERTIES.add(CALENDAR_ID_PROPERTY);
    USER_PROPERTIES.add(EVENT_IDS_PROPERTY);

    WORKOUT_PROPERTIES.add(TYPE_PROPERTY);
    WORKOUT_PROPERTIES.add(WEEKS_TO_TRAIN_PROPERTY);
    WORKOUT_PROPERTIES.add(WORKOUT_NAME_PROPERTY);
    WORKOUT_PROPERTIES.add(GOAL_STEPS_PROPERTY);

    MARATHON_PROPERTIES.add(MARATHON_LENGTH_PROPERTY);
    MARATHON_PROPERTIES.add(INITIAL_TIME_PROPERTY);
    MARATHON_PROPERTIES.add(GOAL_TIME_PROPERTY);
    MARATHON_PROPERTIES.add(MILE_TIME_PROPERTY);

    LIFTING_PROPERTIES.add(INITIAL_WEIGHT_PROPERTY);
    LIFTING_PROPERTIES.add(INITIAL_REPS_PROPERTY);
    LIFTING_PROPERTIES.add(GOAL_WEIGHT_PROPERTY);
    LIFTING_PROPERTIES.add(GOAL_REPS_PROPERTY);
  }
 

  static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  static Gson gson = new Gson();

  /**
  * equalKeys checks if an email matches the key of a entity.
  * keys are in the format "'type(key)'" so removing the last
  * 2 characters and checking the ending will check if that is
  * the correct jey
  *
  * @param   id      The ID of the entity we are dealing with.
  * @param   key     The key in the above mentioned format.
  * @return          If the id matches the key.
  */
  public static boolean equalKeys(String  id, String key) {
    return key.substring(0,key.length()-2).endsWith(id);
  }


  /**
  * getDate returns a data in the format yyyy/MM/dd
  * using the timestamp provided.
  *
  * @param   timestamp   The timestamp generated.
  * @return              A String data in the format yyyy/MM/dd.
  */
  public static String getDate(long timestamp) {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
    return formatter.format(new Date(timestamp));
  }


  /**
  * getUser return the Entity associated with the email
  * that is signed in at the moment. If nobody is signed in,
  * it returns null.
  *
  * @return     The Entity associated with the email or null.
  */
  public static Entity getUser() {
    // Get the users email.
    UserService userService = UserServiceFactory.getUserService();
    String userEmail = userService.getCurrentUser().getEmail();
    
    // Check if user does not exist.
    if(userEmail == null) {
      return null;
    }

    // Find user in datastore.
    Query query = new Query(USER_ENTITY);
    PreparedQuery results = datastore.prepare(query);
    
    for(Entity e : results.asIterable()) {
        String key = e.getKey().toString();
        if(!(key == null) && equalKeys(userEmail, key)) {
          return e;
        }
    }
    return null;
  }

  /**
  * getWorkout - returns a workout entity given a name(id)
  *
  * @param name the name of the activity
  * @return     the workout entity
  */
  public static Entity getWorkout(String name) {
    // Find user in datastore.
    Query query = new Query(WORKOUT_ENTITY);
    PreparedQuery results = datastore.prepare(query);
    
    for(Entity e : results.asIterable()) {
        String key = e.getKey().toString();
        if(!(key == null) && equalKeys(name, key)) {
          return e;
        }
    }
    return null;
  }

  /**
  * GetData returns a datapoint from datastore for a user.
  *
  * @param  property    The property that we want to get.
  * @param  user        The User entity we are dealing with.
  * @return             The value of the data as a String
  */
  public static String getUserData(String property, Entity user) {
    //Entity user = getUser();
    // Check if user is signed in
    if(user == null) {
      return null;
    }

    String data = (user.getProperty(property)).toString();
    return data; 
  }

  /**
  * GetData returns a datapoint from datastore for a user.
  *
  * @param  property    The property that we want to get.
  * @param  workout     The User entity we are dealing with.
  * @return             The value of the data as a String
  */
  public static String getWorkoutData(String property, Entity workout) {
    //Entity user = getUser();
    // Check if user is signed in
    if(workout == null) {
      return null;
    }

    String data = (workout.getProperty(property)).toString();
    return data; 
  }
  /**
  *getGoalSteps returns the JSON string of the goalsteps of the workout

  * @param workout  the workout we want the info from
  * @return         the goalsteps string
  */
  public static String getGoalSteps(Entity workout) {
    String goalSteps = ((Text) workout.getProperty(GOAL_STEPS_PROPERTY)).getValue();
    return goalSteps; 
  }


  /**
  * setGoalSteps updates the JSON string of the goalsteps of the workout
  *
  * @param goalStepsJson    new new goalStep string
  * @param workout          the workout we want to update
  * @return                 none
  */
  public static void setGoalSteps(String goalStepsJson, Entity workout) {
    workout.setProperty(GOAL_STEPS_PROPERTY, new Text(goalStepsJson));
    datastore.put(workout);
  }

  /**
  * setCalendarID sets the ID for authentication of the calendar

  * @param user the user's whose calendar ID we want to update
  * @param id   the new Calendar ID
  * @return     none
  */
  public static void setCalendarID(Entity user, String id) {
      user.setProperty(CALENDAR_ID_PROPERTY, id);
      datastore.put(user);
  }

  /**
  * addEventID - adds an eventId to the list of eventIDS
  *
  * @param user the user
  * @param id   the ID we are going to add
  * @return     null
  */
  public static void addEventID(Entity user, String id) {
      ArrayList<String> EventIds = gson.fromJson(getUserData(user, EVENT_IDS_PROPERTY), new TypeToken<List<String>>(){}.getType());
      EventIds.add(id);
      user.setProperty(EVENT_IDS_PROPERTY, gson.toJson(EventIds));
      datastore.put(workout);
  }