package com.google.sps.servlets;
 
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import com.google.api.client.extensions.appengine.auth.oauth2.AbstractAppEngineAuthorizationCodeServlet;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.sps.util.*;
import com.google.api.services.calendar.Calendar;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import java.util.*;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.calendar.model.CalendarList;
import com.google.gson.Gson;
import com.google.api.services.calendar.model.CalendarListEntry;
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;   

// AbstractAuthServlet initializes the OAuth process. 
@WebServlet("/abstract")
public class AbstractAuthServlet extends AbstractAppEngineAuthorizationCodeServlet {
  String nickname = UserServiceFactory.getUserService().getCurrentUser().getNickname();
  String APPLICATION_NAME = "Marathon App";
  Calendar calendar;
  Scheduler scheduler;
  long exerciseDuration = 30;  
  Gson gson = new Gson();
  DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/YYYY");  
  LocalDateTime now = LocalDateTime.now();  

  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    response.setContentType("application/json;");
    
    // Build calendar. 
    String userId = getUserId(request);
    Credential credential = Utils.newFlow().loadCredential(userId);
    calendar = new Calendar.Builder(
    new UrlFetchTransport(),
    new JacksonFactory(),
    credential).setApplicationName(APPLICATION_NAME).build();

    com.google.api.services.calendar.Calendar.CalendarList.List listRequest = calendar.calendarList().list();
    listRequest.setFields("items(id)").setMaxResults(1);
    CalendarList feed = listRequest.execute();
   
    ArrayList<String> result = new ArrayList<String>();
      if (feed.getItems() != null) {
        for (CalendarListEntry entry : feed.getItems()) {
          result.add(entry.getId());
        }}

    String jsonId = gson.toJson(result.get(0));
    response.getWriter().println((jsonId));    
 
    scheduler = new Scheduler(exerciseDuration);
    
    //TODO (@piercedw) : Get from datastore using getData().
    int daysAvailable = 14;

    // TODO (@piercedw) : Get from datastore using getData(). 
    List<String> exercises = new ArrayList<String>();
    exercises.add("Scheduled Exercise 1");
    exercises.add("Scheduled Exercise 2");
    exercises.add("Scheduled Exercise 3");
    exercises.add("Scheduled Exercise 4");
    exercises.add("Scheduled Exercise 5");
    exercises.add("Scheduled Exercise 6");
    exercises.add("Scheduled Exercise 7");
    
    int timesPerWeek = daysAvailable/ exercises.size(); 

    // Hardcoded as 8:00 AM today. 
    // TODO (@piercedw) : Make this just the start of the next day. 
    DateTime minSpan = new DateTime("2020-08-14T12:00:00+00:00");

    // Hardcoded as 8:00 PM today. 
    //TODO (@piercedw) : Same as above.
    DateTime maxSpan = new DateTime("2020-08-15T00:00:00+00:00");
    
    int y = 0;
    for (int x = 0; x<daysAvailable; x+=timesPerWeek){
        List<Event> currentlyScheduledEvents = this.getEventsInTimespan(minSpan, maxSpan);
        Event exerciseEvent = scheduler.getFreeTime(minSpan, maxSpan, currentlyScheduledEvents);
        exerciseEvent.setSummary(exercises.get(y));
        exerciseEvent.setColorId("4");
        this.insertEvent(exerciseEvent);
    // Increment minSpan and maxSpan by one day. 
        y++;
        minSpan = new DateTime(minSpan.getValue() + (Time.millisecondsPerDay * timesPerWeek));
        maxSpan = new DateTime(maxSpan.getValue() + (Time.millisecondsPerDay * timesPerWeek));
    }
    
  }
 
  @Override
  protected String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {
    return Utils.getRedirectUri(req);
  }
 
  @Override
  protected AuthorizationCodeFlow initializeFlow() throws IOException {
    return Utils.newFlow();
  }
  
  public List<Event> getEventsInTimespan(DateTime minSpan, DateTime maxSpan) throws IOException{
    Events events = calendar.events().list("primary")
                .setTimeMin(minSpan)
                .setTimeMax(maxSpan)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
    List<Event> items = events.getItems();
    return items;
  }

  // Method for putting an event on the user's calendar. 
  public void insertEvent(Event event) throws IOException{
    Event myNewEvent = calendar.events().insert("primary", event).execute();
  }
}
 
