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

import static org.junit.Assert.*;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;
import java.io.*;
import javax.servlet.http.*;
//import org.apache.commons.io.FileUtils;
import org.junit.Test;
import com.google.sps.servlets.DataServlet;

public class ServletFunctionsTest extends Mockito {
  @Test
  public void testDataServletPost() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);       
    HttpServletResponse response = mock(HttpServletResponse.class);    

    String name = "kenna";
    String text = "this some text";

    when(request.getParameter("name-box")).thenReturn(name);
    when(request.getParameter("text-box")).thenReturn(text);

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    new DataServlet().doPost(request, response);

    verify(request, atLeast(1)).getParameter("name-box");
    verify(request, atLeast(1)).getParameter("text-box");
    writer.flush();
    assertTrue(stringWriter.toString().contains(name));
    assertTrue(stringWriter.toString().contains(text));
  }
}