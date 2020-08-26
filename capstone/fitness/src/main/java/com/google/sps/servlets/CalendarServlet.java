package com.google.sps.servlets;
 
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
import com.google.sps.util.*;
import com.google.sps.progress.*;
import com.google.sps.fit.*;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
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
  private static String APPLICATION_NAME = "Marathon App";
  private static String colorId = "4";
  private static long exerciseDuration = 30;  
  private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/YYYY");  
  Gson gson = new Gson();
  Calendar calendar; 
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    response.setContentType("application/json;");
    
    // Build calendar. 
    String userId = getUserId(request);
    Credential credential = Utils.newFlow().loadCredential(userId);
    calendar= new Calendar.Builder(new UrlFetchTransport(), new JacksonFactory(), credential).setApplicationName(APPLICATION_NAME).build();

    Scheduler scheduler = new Scheduler(exerciseDuration);
    
    String wks = (DataHandler.getUserData("weeksToTrain",DataHandler.getUser()));
    int weeksToTrain = Integer.parseInt(wks);
    
    int daysAvailable = weeksToTrain * Time.weeksToDays;
    
    List<String> exercises = this.getExercises();
    int timesPerWeek = daysAvailable/ exercises.size(); 

    // // Sets minSpan to 7:00 AM the next day, and maxSpan to 7PM the next day.
    LocalDateTime now = LocalDateTime.now();  
    DateTimeFormatter myDtf = DateTimeFormatter.ofPattern("YYYY-MM-dd");  
    String nextDayOfMonth = myDtf.format(now.plusDays(1));
    String nextDayStartTime = "T11:00:00+00:00";
    String nextDayEndTime = "T23:00:00+00:00";
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
        Event exerciseEvent = scheduler.getFreeTime(minSpan, maxSpan, currentlyScheduledEvents);
        exerciseEvent.setSummary(APPLICATION_NAME + ": " + exercises.get(y));
        exerciseEvent.setColorId(colorId);
        // TODO (@piercedw) : Store each event's eventID in datastore for display later.
        this.insertEvent(exerciseEvent);
        // Increment minSpan and maxSpan by one day. 
        minSpan = new DateTime(minSpan.getValue() + (Time.millisecondsPerDay * timesPerWeek));
        maxSpan = new DateTime(maxSpan.getValue() + (Time.millisecondsPerDay * timesPerWeek));
        y++;
    }
    }
    String id = this.getCalendarId();
    response.sendRedirect("/calendar.html?calendarId=" + id); 
  }
 
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

  private String getCalendarId() throws IOException{
    // Might be an extra step. 
    com.google.api.services.calendar.Calendar.CalendarList.List listRequest = calendar.calendarList().list();
    listRequest.setFields("items(id)").setMaxResults(1);
    CalendarList feed = listRequest.execute();
   
    ArrayList<String> result = new ArrayList<String>();

    if (feed.getItems() != null) {
      for (CalendarListEntry entry : feed.getItems()) {
        result.add(entry.getId());}
        }
        
    // TODO (@piercedw) : Store this result in datastore.
    return result.get(0); 
  }
 private List <String> getExercises(){
    String goalSteps = DataHandler.getGoalSteps();
    ArrayList<JsonExercise> goalStepsArray = gson.fromJson(goalSteps, new TypeToken<List<JsonExercise>>(){}.getType());
    List<String> exercises = new ArrayList<String>();
    for(JsonExercise goal: goalStepsArray){
      exercises.add(goal.getName());
    }

    return exercises; 
    
  }
}