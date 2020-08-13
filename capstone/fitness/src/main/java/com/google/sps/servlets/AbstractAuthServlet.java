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

// AbstractAuthServlet initializes the OAuth process. 
@WebServlet("/abstract")
public class AbstractAuthServlet extends AbstractAppEngineAuthorizationCodeServlet {
  String nickname = UserServiceFactory.getUserService().getCurrentUser().getNickname();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    System.out.println("Nickname " + nickname);
    response.setContentType("text/html;");
    response.getWriter().println(nickname + " has authorized this app.");
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
}
