
package com.google.sps.servlets;
import java.io.IOException;
import javax.servlet.ServletException;

import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeCallbackServlet;
import javax.servlet.annotation.WebServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.auth.oauth2.Credential;

import com.google.api.client.extensions.appengine.auth.oauth2.AbstractAppEngineAuthorizationCodeCallbackServlet;

@WebServlet("/oauth2callback")
public class OAuth2CallbackServlet extends AbstractAppEngineAuthorizationCodeCallbackServlet {

  // on success, redirct back to /schedule-exercises
//   @Override
  protected void onSuccess(HttpServletRequest req, HttpServletResponse resp, Credential credential)
      throws ServletException, IOException {
    // resp.sendRedirect("/calendar-handler");
    // Calendar calendar = OAuthHelper.loadCalendarClient();
    System.out.println("success");


  }

  // on failure, print a simple error message 
//   @Override
  protected void onError(HttpServletRequest req, HttpServletResponse resp, AuthorizationCodeResponseUrl errorResponse) throws ServletException, IOException {
      resp.setContentType("text/html;");
      resp.getWriter().println("OAuth2 Failed");
            System.out.println("fail");

      }
  // need to implement redirect URI and authorization codeflow
    @Override
  protected String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {
                  System.out.println("redirect");
                  return OAuthHelper.getRedirectUri(req);
    //   return "http://localhost:8080/calendar-handler";
  }

  @Override
  protected AuthorizationCodeFlow initializeFlow() throws IOException {
    return OAuthHelper.newFlow();
  }
}
