package com.google.sps.util;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataHelper {

  // Constants used for datastore.
  public final static String USER_ENTITY = "user";
  public final static String PROGRESS_PROPERTY = "sessions";
  public final static String NAME_PROPERTY = "name";
  public final static String AGE_PROPERTY = "age";
  public final static String MARATHON_LENGTH_PROPERTY = "marathonLength";
  public final static String WEEKS_PROPERTY = "weeksToPrepare";
  public final static String INITIAL_TIME_PROPERTY = "initialTime";
  public final static String GOAL_TIME_PROPERTY = "goalTime";

  static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

 /**
  * equalKeys checks if an email matches the key of a entity.
  * Entity keys are in the format user("key"), so using the 
  * substring gets rid of the 'user("' and '")' at both ends.
  * The complexity of this is O(1)
  *
  * @param email - the email of the current user
  * @param key - the key in the above mentioned format
  * @return if the email matches the key.
  */
  public static boolean equalKeys(String  email, String key) {
    return key.substring(6, key.length()-2).equals(email);
  }

 /**
  * getDate returns a data in the format yyyy/MM/dd
  * using the timestamp provided
  *
  * @param timestamp - the timestamp generated
  * @return a String data in the format yyyy/MM/dd
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

    // Find user in datastore 
    Query query = new Query(DataHelper.USER_ENTITY);
    PreparedQuery results = datastore.prepare(query);
    loopThroughComments: 
    for(Entity e : results.asIterable()) {
        String key = e.getKey().toString();
        if(!(key == null) && equalKeys(userEmail, key)) {
          return e;
        }
    }
    System.err.println("Could not find user");
    return null;
  }
}