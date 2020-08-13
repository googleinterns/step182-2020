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

@WebServlet("/oauth2callback")
public class OAuth2CallbackServlet extends AbstractAppEngineAuthorizationCodeCallbackServlet {
  String nickname = UserServiceFactory.getUserService().getCurrentUser().getNickname();

  @Override
  protected void onSuccess(HttpServletRequest req, HttpServletResponse resp, Credential credential)
      throws ServletException, IOException {
    resp.sendRedirect("/abstract");
    resp.getWriter().print( nickname + " is logged in and has given access to their calendar.");
    System.out.println( nickname + " is logged in.");

  }

  @Override
  protected void onError(
      HttpServletRequest req, HttpServletResponse resp, AuthorizationCodeResponseUrl errorResponse)
      throws ServletException, IOException {
    System.out.println(nickname + " is not logged in");
    resp.getWriter().print( nickname + " has not given access to their calendar.");
    resp.setStatus(200);
    resp.addHeader("Content-Type", "text/html");
  }

  @Override
  protected String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {
    System.out.println(nickname + " redirected on callback servlet");
    return Utils.getRedirectUri(req);
  }

  @Override
  protected AuthorizationCodeFlow initializeFlow() throws IOException {
    System.out.println(nickname + " initialized a new flow on callback servlet");
    return Utils.newFlow();
  }
}