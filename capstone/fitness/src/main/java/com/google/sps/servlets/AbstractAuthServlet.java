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
 
// AbstractAuthServlet initializes the OAuth process. 
@WebServlet("/abstract")
public class AbstractAuthServlet extends AbstractAppEngineAuthorizationCodeServlet {
  String nickname = UserServiceFactory.getUserService().getCurrentUser().getNickname();
  String APPLICATION_NAME = "Marathon App";

  Calendar calendar;
  Scheduler scheduler;

  long exerciseDuration = 30;
  


  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    response.setContentType("text/html;");
    response.getWriter().println(nickname + " has authorized this app.");
    
    scheduler = new Scheduler(exerciseDuration);

    String userId = getUserId(request);
    Credential credential = Utils.newFlow().loadCredential(userId);
    
    calendar = new Calendar.Builder(
            new UrlFetchTransport(),
            new JacksonFactory(),
            credential).setApplicationName(APPLICATION_NAME).build();
    
        // List the next 10 events from the primary calendar.
        DateTime now = new DateTime(System.currentTimeMillis());
        DateTime then = new DateTime("2020-08-14T22:00:00+00:00");
        Events events = calendar.events().list("primary")
                .setMaxResults(10)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        
        
        List<Event> items = events.getItems();
        Event event = scheduler.getFreeTime(now, then, items);
        event.setSummary("Test from webapp");
        System.out.println(event.getStart().getDateTime());
        
        // do this in a loop
        this.insertEvent(event);
        
        System.out.println("done");

        // if (items.isEmpty()) {
        //     System.out.println("No upcoming events found.");
        // } else {
        //     System.out.println("Upcoming events");
        //     for (Event event : items) {
        //         DateTime start = event.getStart().getDateTime();
        //         if (start == null) {
        //             start = event.getStart().getDate();
        //         }
        //         System.out.printf("%s (%s)\n", event.getSummary(), start);
        //     }
        // }
  }
 
  @Override
  protected String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {
    System.out.println("Nickname redirected " + nickname);
    System.out.println("req url " + req);
    return Utils.getRedirectUri(req);
  }
 
  @Override
  protected AuthorizationCodeFlow initializeFlow() throws IOException {
    System.out.println("Nickname initialized " + nickname);
    return Utils.newFlow();
  }

  public void insertEvent(Event event){
    Event myNewEvent = calendar.events().insert("primary", event).execute();
  }
}
 
