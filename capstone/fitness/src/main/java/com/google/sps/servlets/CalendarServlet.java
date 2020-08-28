package com.google.sps.servlets;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.util.DateTime;
import com.google.api.client.extensions.appengine.auth.oauth2.AbstractAppEngineAuthorizationCodeServlet;
import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.sps.fit.*;
import com.google.sps.util.*;
import com.google.sps.progress.*;
import com.google.sps.fit.*;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;   
import java.util.*;

// CalendarServlet initializes the OAuth process and does calendar functions. 
@WebServlet("/calendar-servlet")
public class CalendarServlet extends AbstractAppEngineAuthorizationCodeServlet {
  private static String APPLICATION_NAME = "GetIn' Progress";
  private static String runningColorId = "4";
  private static String liftingColorId = "8";
  private static String unknownWorkoutColorId = "10";
  private static long exerciseDuration = 30;  
  private static DateTimeFormatter myDtf = DateTimeFormatter.ofPattern("YYYY-MM-dd");  
  private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/YYYY");
  private static String liftingType = "lifting";
  private static String marathonType = "marathon";  
  private static String nextDayStartTime = "T11:00:00+00:00";
  private static String nextDayEndTime = "T23:00:00+00:00";
  Gson gson = new Gson();
  Calendar calendar; 
  int scheduledLength = 0;
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    // Build calendar. 
    String userId = getUserId(request);
    Credential credential = Utils.newFlow().loadCredential(userId);
    calendar= new Calendar.Builder(new UrlFetchTransport(), new JacksonFactory(), credential).setApplicationName(APPLICATION_NAME).build();

    Scheduler scheduler = new Scheduler(exerciseDuration);
    
    // Store user entity for calling other DataHandler methods. 
    Entity user = DataHandler.getUser();

    List<String> workoutList = this.getWorkoutList(user);
    
    if (workoutList.size() > scheduledLength){ 
      for (int b = scheduledLength; b <workoutList.size(); b++){
        String type = this.getWorkoutType(workoutList.get(b));
        List<String> exercises = this.getExercises(workoutList.get(b));
        int weeksToTrain = Integer.parseInt(this.getWeeksToTrain(workoutList.get(b)));
        int daysAvailable = weeksToTrain * Time.weeksToDays;
        int timesPerWeek = daysAvailable/ exercises.size(); 

        // Sets minSpan to 7:00 AM the next day, and maxSpan to 7PM the next day.
        LocalDateTime now = LocalDateTime.now();  
        String nextDayOfMonth = myDtf.format(now.plusDays(1));
        String minRCF3339 = nextDayOfMonth + nextDayStartTime;
        String maxRCF3399 = nextDayOfMonth + nextDayEndTime;
        DateTime minSpan = new DateTime(minRCF3339);
        DateTime maxSpan = new DateTime(maxRCF3399);

        // Use the scheduler to schedule one exercise per day. 
        int y = 0;
    
        for (int x = 0; x < daysAvailable; x += timesPerWeek){
          if (y >= exercises.size()){
            break;
            }
          else{
            List<Event> currentlyScheduledEvents = this.getEventsInTimespan(minSpan, maxSpan);
            // User scheduler to get free time and return corresponding event. 
            Event exerciseEvent = scheduler.getFreeTime(minSpan, maxSpan, currentlyScheduledEvents);
            exerciseEvent = this.configureEvent(exerciseEvent, type, exercises.get(y));
            this.insertEvent(exerciseEvent);
            
            // Store each event's eventID in datastore for display later.
            // TODO (@piercedw) : Storing the events as the entire string for now until I can work throught the event Id issue we discussed in podsync.
            String eventDescription = type + " at " + exerciseEvent.getStart().getDateTime();
            DataHandler.addEventID(user,eventDescription);

            // Increment minSpan and maxSpan by one day. 
            minSpan = this.incrementDay(minSpan, timesPerWeek);
            maxSpan = this.incrementDay(maxSpan, timesPerWeek);
            y++;}}
        scheduledLength++; }
      // Store calendar ID. 
      DataHandler.setCalendarID(user, this.getCalendarId());}
    response.sendRedirect("/calendar.html"); }
 
  @Override
  protected String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {
    return Utils.getRedirectUri(req);
  }
 
  @Override
  protected AuthorizationCodeFlow initializeFlow() throws IOException {
    return Utils.newFlow();
  }
  
  private List<Event> getEventsInTimespan(DateTime minSpan, DateTime maxSpan) throws IOException{
    Events events = calendar.events().list("primary").setTimeMin(minSpan).setTimeMax(maxSpan).setOrderBy("startTime").setSingleEvents(true).execute();
    List<Event> items = events.getItems();
    return items;
  }

  // Method for putting an event on the user's calendar. 
  private void insertEvent(Event event) throws IOException{
    Event myNewEvent = calendar.events().insert("primary", event).execute();
  }
  
  // Getting the user's primary calendar Id to display in html. 
  private String getCalendarId() throws IOException{
    com.google.api.services.calendar.Calendar.CalendarList.List listRequest = calendar.calendarList().list();
    listRequest.setFields("items(id)").setMaxResults(1);
    CalendarList feed = listRequest.execute();
   
    ArrayList<String> result = new ArrayList<String>();

    if (feed.getItems() != null) {
      for (CalendarListEntry entry : feed.getItems()) {
        result.add(entry.getId());}}
    return result.get(0); 
  }
  
  // Getting all of the user's workout names to iterate through and create exercises. 
  private List<String> getWorkoutList(Entity user){
    String workoutsString = DataHandler.getUserData("workoutList", user);
    ArrayList<String> workoutNamesList = gson.fromJson(workoutsString, new TypeToken<List<String>>(){}.getType());
    return workoutNamesList;    
  }

  // Gets exercises from a workout. 
  private List<String> getExercises(String workoutName){
    String goalSteps = DataHandler.getGoalSteps(DataHandler.getWorkout(workoutName));
    ArrayList<JsonExercise> goalStepsArray = gson.fromJson(goalSteps, new TypeToken<List<JsonExercise>>(){}.getType());
    
    List<String> exercises = new ArrayList<String>();
    for(JsonExercise goal: goalStepsArray){
      exercises.add(goal.getName());
    }
    return exercises; 
  }

  // Gets the # of weeks from a workout. 
  private String getWeeksToTrain(String workoutName){
      String weekNum = DataHandler.getWorkoutData("weeksToTrain", DataHandler.getWorkout(workoutName));
      return weekNum;
  }

  private String getWorkoutType(String workoutName){
      String workoutType = DataHandler.getWorkoutData("workoutType", DataHandler.getWorkout(workoutName));
      return workoutType;
  }
  
  // Increments day in scheduling flow.
  private DateTime incrementDay(DateTime moment, int timesPerWeek){
    DateTime incremented = new DateTime(moment.getValue() + (Time.millisecondsPerDay * timesPerWeek));
    return incremented;
  }

  private Event configureEvent(Event event, String summary, String type){
            // Event name and description match the user's workout name and type of workout.
            event.setSummary(APPLICATION_NAME + ": " + summary);
            event.setDescription(type);
            // Set event color depending on the type of workout. 
            if (type.equals(liftingType)){
              event.setColorId(liftingColorId);
            }
            else if(type.equals(liftingType)){
              event.setColorId(runningColorId);
            }
            else{
              event.setColorId(unknownWorkoutColorId); 
            }
            return event; 
  }
  
}