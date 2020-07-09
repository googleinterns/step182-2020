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

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.sps.data.User;
import com.google.sps.servlets.DataServlet;
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

    String name = "kenna";
    String text = "this some text";
    User user = new User("example@google.com", false);

    when(request.getParameter("name-box")).thenReturn(name);
    when(request.getParameter("text-box")).thenReturn(text);
    when(request.getSession()).thenReturn(session);
    when(session.getAttribute("user")).thenReturn(user);
    
    DataServlet ds = new DataServlet();
    ds.init();
    ds.doPost(request, response);

    verify(request, atLeast(1)).getParameter("name-box");
    verify(request, atLeast(1)).getParameter("text-box");
  }
}