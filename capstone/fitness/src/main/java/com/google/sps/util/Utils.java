package com.google.sps.util;

import javax.servlet.http.HttpServletRequest;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import java.io.IOException;
import javax.servlet.ServletException;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import java.util.*;
import com.google.api.client.extensions.appengine.datastore.AppEngineDataStoreFactory;
import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.util.Preconditions;
import java.io.InputStreamReader;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.api.services.calendar.Calendar;

// Utils.java contains helper functions for both Abstract Authorization Servlets
public class Utils {
  static String APPLICATION_NAME = "GetIn' Progress";
  /**   * Global instance of the {@link DataStoreFactory}.    */

  private static final AppEngineDataStoreFactory DATA_STORE_FACTORY =
      AppEngineDataStoreFactory.getDefaultInstance();
  
  /** Global instance of the HTTP transport. */
  static final HttpTransport HTTP_TRANSPORT = new UrlFetchTransport();

  /** Global instance of the JSON factory. */
  static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
  private static GoogleClientSecrets clientSecrets = null;

  static GoogleClientSecrets getClientCredential() throws IOException {
    if (clientSecrets == null) {
      clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
          new InputStreamReader(Utils.class.getResourceAsStream("/client_secret.json")));
      Preconditions.checkArgument(!clientSecrets.getDetails().getClientId().startsWith("Enter ")
          && !clientSecrets.getDetails().getClientSecret().startsWith("Enter "),
          "Download client_secrets.json file from https://code.google.com/apis/console/"
          + "?api=calendar into calendar-appengine-sample/src/main/resources/client_secrets.json");
    }
    return clientSecrets;
  }
  public static String getRedirectUri(HttpServletRequest req) {
    return "https://capstone-step182-2020.uc.r.appspot.com/oauth2callback";
  }

  public static GoogleAuthorizationCodeFlow newFlow() throws IOException {
    return new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
        getClientCredential(), Collections.singleton(CalendarScopes.CALENDAR)).setDataStoreFactory(
        DATA_STORE_FACTORY).setAccessType("offline").build();
  }
 

}
