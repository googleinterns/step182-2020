package com.google.sps.servlets;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.appengine.auth.oauth2.AbstractAppEngineAuthorizationCodeCallbackServlet;
import com.google.appengine.api.users.UserServiceFactory;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.annotation.WebServlet;

import com.google.sps.util.*;


// OAuth2CallbackServlet handles the callback for users granting access to their calendars. 
@WebServlet("/oauth2callback")
public class OAuth2CallbackServlet extends AbstractAppEngineAuthorizationCodeCallbackServlet {
  String nickname = UserServiceFactory.getUserService().getCurrentUser().getNickname();

  // On success, meaning that the user has given access, it just redirects back to the original abstract servlet
  @Override
  protected void onSuccess(HttpServletRequest req, HttpServletResponse resp, Credential credential)
      throws ServletException, IOException {
    resp.sendRedirect("/abstract");
    resp.getWriter().print( nickname + " is logged in and has given access to their calendar.");
    System.out.println("CALLBAcK "  + nickname + " is logged in.");
  }
  
  //On error, meaning that the user chose not to grant access, it prints this simple error message.
  @Override
  protected void onError(
      HttpServletRequest req, HttpServletResponse resp, AuthorizationCodeResponseUrl errorResponse)
      throws ServletException, IOException {
    resp.getWriter().print( nickname + " has not given access to their calendar. Why not? :(");
    resp.setStatus(200);
    resp.addHeader("Content-Type", "text/html");
  }

  @Override
  protected String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {
    return Utils.getRedirectUri(req);
  }

  @Override
  protected AuthorizationCodeFlow initializeFlow() throws IOException {
    return Utils.newFlow();
  }
}