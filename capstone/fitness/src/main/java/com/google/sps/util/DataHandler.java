package com.google.sps.util;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.google.appengine.api.datastore.FetchOptions;
import java.util.HashSet;
import java.util.Set;


public class DataHandler {

  // Constants used for datastore.
  // TODO(@gabrieldg) 
  //  - make private and change to getters and setters.
  //  - Figure out why the identifiers do not work.
  public static Set<String> PROPERTIES  = new HashSet<>();
  public final static String USER_ENTITY = "user";
  public final static String NAME_PROPERTY = "name";
  public final static String AGE_PROPERTY = "age";
  public final static String WEEKS_TO_TRAIN_PROPERTY = "weeksToTrain";
  public final static String MARATHON_LENGTH_PROPERTY = "marathonLength";
  public final static String INITIAL_TIME_PROPERTY = "initialTime";
  public final static String GOAL_TIME_PROPERTY = "goalTime";
  public final static String PROGRESS_PROPERTY = "progress";
  public final static String MILE_TIME_PROPERTY = "mileTime";
  static {
    PROPERTIES.add(NAME_PROPERTY);
    PROPERTIES.add(AGE_PROPERTY);
    PROPERTIES.add(WEEKS_TO_TRAIN_PROPERTY);
    PROPERTIES.add(MARATHON_LENGTH_PROPERTY);
    PROPERTIES.add(INITIAL_TIME_PROPERTY);
    PROPERTIES.add(GOAL_TIME_PROPERTY);
    PROPERTIES.add(PROGRESS_PROPERTY);
    PROPERTIES.add(MILE_TIME_PROPERTY);
  }
 

  static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

 /**
  * equalKeys checks if an email matches the key of a entity.
  * Entity keys are in the format user("key"), so using the 
  * substring gets rid of the 'user("' and '")' at both ends.
  * The complexity of this is O(1).
  *
  * @param email - the email of the current user.
  * @param key - the key in the above mentioned format.
  * @return if the email matches the key.
  */
  public static boolean equalKeys(String  email, String key) {
    return key.substring(6, key.length()-2).equals(email);
  }


 /**
  * getDate returns a data in the format yyyy/MM/dd
  * using the timestamp provided.
  *
  * @param timestamp - the timestamp generated.
  * @return a String data in the format yyyy/MM/dd.
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
  * @return - The Entity associated with the email or null.
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
  * GetData returns a datapoint from datastore for a user.
  *
  * @param property - The property that we want to get.
  * @param user - The User entity we are dealing with.
  * @return the value of the data as a String
  */
  public static String getData(String property, Entity user) throws Exception {
    // Check if property exists
    if(!PROPERTIES.contains(property)) {
      throw new Exception("Invalid Property");
    }
    //Entity user = getUser();
    // Check if user is signed in
    if(user == null) {
      throw new Exception("Not signed in");
    }

    String data = (user.getProperty(property)).toString();
    return data; 
  }

  /**
   * Jsonfy returns a json component in key-value format. It also accounts 
   * for datapoints that are not strings
   *
   * @param property - the key for JSON
   * @param value - the value for JSON
   * @param isNumber - wheter or not the value should be wrapped in quoutes
   * @return the JSON formatted string 
   */
  public static String Jsonfy(String property, String value, Boolean isNumber) {
      // If it is a number we do not put quotes around the value.
      if(isNumber) {
        return "\"" + property + "\":"+value;
      }
      else {
        return "\"" + property + "\":\""+value+"\"";
      }
  }

  /**
   * isNumber returns wheter or not a property is a number of not
   *
   * @param property - the property we are dealing with
   * @return - wether or not it should be a number or string. 
   */
  public static boolean isNumber(String property) {
      if(property.equals(NAME_PROPERTY) || property.equals(PROGRESS_PROPERTY))
        return false;
      return true;
  }
}