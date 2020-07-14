// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.sps.data.Comment;
import com.google.sps.data.Metadata.Sort;
import com.google.sps.data.User;
import com.google.sps.database.CommentDatabase;
import com.google.sps.database.DatabaseInterface;
import com.google.sps.login.*;
import com.google.sps.servlets.*;
import java.io.*;
import javax.servlet.http.*;
import org.junit.*;
import org.mockito.Mockito;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ServletFunctionsTest extends Mockito {
  
  private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  @Before
  public void setUp() {
    helper.setUp();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void testDataServletPost() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);       
    HttpServletResponse response = mock(HttpServletResponse.class);    
    HttpSession session = mock(HttpSession.class);

    DatabaseInterface database = new CommentDatabase();
    String text = "this some text";
    User user = new User("example@google.com", false);

    when(request.getParameter("text-box")).thenReturn(text);
    when(request.getSession()).thenReturn(session);
    when(session.getAttribute("user")).thenReturn(user);

    DataServlet ds = new DataServlet();
    assertTrue(database.size() == 0);

    ds.doPost(request, response);

    verify(request, atLeast(1)).getParameter("text-box");
    assertTrue(database.size() == 1);
    assertTrue(((String)(database.getContents(Sort.OLDEST, 10, 0).get(0).getProperty("email"))).equals(user.getEmail()));
  }

  @Test
  public void testDataServletAnonymousPost() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);       
    HttpServletResponse response = mock(HttpServletResponse.class);    
    HttpSession session = mock(HttpSession.class);

    DatabaseInterface database = new CommentDatabase();
    String text = "this some text";
    User user = new User("example@google.com", false);
    
    when(request.getParameter("anonymous")).thenReturn("");    /* Value shouldn't matter */
    when(request.getParameter("text-box")).thenReturn(text);
    when(request.getSession()).thenReturn(session);
    when(session.getAttribute("user")).thenReturn(user);

    DataServlet ds = new DataServlet();
    assertTrue(database.size() == 0);

    ds.doPost(request, response);

    verify(request, atLeast(1)).getParameter("anonymous");
    verify(request, atLeast(1)).getParameter("text-box");
    assertTrue(database.size() == 1);
    assertTrue(((String)(database.getContents(Sort.OLDEST, 10, 0).get(0).getProperty("nickname"))).equals("Anonymous"));
  }

  @Test
  public void testDataServletNoUserPost() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);       
    HttpServletResponse response = mock(HttpServletResponse.class);    
    HttpSession session = mock(HttpSession.class);

    DatabaseInterface database = new CommentDatabase();
    String text = "this some text";
    
    when(request.getParameter("text-box")).thenReturn(text);
    when(request.getSession()).thenReturn(session);
    when(session.getAttribute("user")).thenReturn(null);
    
    DataServlet ds = new DataServlet();
    assertTrue(database.size() == 0);

    ds.doPost(request, response);

    assertTrue(database.size() == 0);
  }

  @Test
  public void testDataServletGet() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);       
    HttpServletResponse response = mock(HttpServletResponse.class);    
    HttpSession session = mock(HttpSession.class);

    DatabaseInterface database = new CommentDatabase();
    
    String nickname = "kenna";
    String text = "this some text"; 
    long timestamp = 0;
    String email = "example@google.com";

    Comment comment = new Comment(nickname, text, timestamp, email);
    database.storeEntity(comment);

    when(request.getSession()).thenReturn(session);
    when(session.getAttribute("metadata")).thenReturn(null);

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    DataServlet ds = new DataServlet();
    ds.doGet(request, response);
        
    writer.flush(); 
    assertTrue(stringWriter.toString().contains(nickname));
    assertTrue(stringWriter.toString().contains(text));
    assertTrue(stringWriter.toString().contains("" + timestamp));
    assertTrue(stringWriter.toString().contains(email));
  }

  @Test
  public void testDataServletEmptyGet() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);       
    HttpServletResponse response = mock(HttpServletResponse.class);    
    HttpSession session = mock(HttpSession.class);

    when(request.getSession()).thenReturn(session);
    when(session.getAttribute("metadata")).thenReturn(null);

    String nicknameParameter = "nickname";
    String textParameter = "text";
    String defaultTimestamp = "0";
    String emailParameter = "email";  

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    DataServlet ds = new DataServlet();
    ds.doGet(request, response);
        
    writer.flush(); 
    assertTrue(stringWriter.toString().contains(nicknameParameter));
    assertTrue(stringWriter.toString().contains(textParameter));
    assertTrue(stringWriter.toString().contains(defaultTimestamp));
    assertTrue(stringWriter.toString().contains(emailParameter));
  }

  @Test
  public void testDeleteCommentsServletPost() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);       
    HttpServletResponse response = mock(HttpServletResponse.class);    

    DatabaseInterface database = new CommentDatabase();
    
    String nickname = "kenna";
    String text = "this some text"; 
    long timestamp = 0;
    String email = "example@google.com";

    Comment comment = new Comment(nickname, text, timestamp, email);
    
    assertTrue(database.size() == 0);
    long id = database.storeEntity(comment);
    assertTrue(database.size() == 1);
    
    when(request.getParameter("delete-comment")).thenReturn("" + id);
    
    DeleteCommentsServlet dcs = new DeleteCommentsServlet();
    dcs.doPost(request, response);
    
    assertTrue(database.size() == 0);
  }
  
}