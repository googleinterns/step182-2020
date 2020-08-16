// // Copyright 2019 Google LLC
// //
// // Licensed under the Apache License, Version 2.0 (the "License");
// // you may not use this file except in compliance with the License.
// // You may obtain a copy of the License at
// //
// //     https://www.apache.org/licenses/LICENSE-2.0
// //
// // Unless required by applicable law or agreed to in writing, software
// // distributed under the License is distributed on an "AS IS" BASIS,
// // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// // See the License for the specific language governing permissions and
// // limitations under the License.

// package com.google.sps.servlets;
// import com.google.api.client.util.DateTime;

// import javax.servlet.annotation.WebServlet;
// import javax.servlet.http.HttpServlet;
// import javax.servlet.http.HttpServletRequest;
// import javax.servlet.http.HttpServletResponse;

// import java.io.IOException;

// import com.google.gson.Gson;
// import java.util.Date;

// import java.time.format.DateTimeFormatter;  
// import java.time.LocalDateTime;    


// // servlet that sends the date to calendar.html. 
// @WebServlet("/calendar-events")
// public class CalendarEventsServlet extends HttpServlet {
//   Gson gson = new Gson();
  
//   DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/YYYY");  
//   String nextDay = now.getYear() + "-" + now.getMonthValue() + "-" +(now.getDayOfMonth()+1);
//   String nextDayTime = todayString + nextDayTimeString;
  
//   LocalDateTime now = LocalDateTime.now();  
//   DateTimeFormatter myDtf = DateTimeFormatter.ofPattern("YYYY-MM-dd");  
//   String nextDayOfMonth = myDtf.format(now.plusDays(1));
//   String nextDayStartTime = "T11:00:00+00:00";
//   String nextRCF3339 = nextDayOfMonth + nextDayStartTime;
//   DateTime asDateTime = new DateTime(nextRCF3339);



//   public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
//     response.setContentType("text/html;");
//     response.getWriter().println(gson.toJson(dtf.format(now)));
//     String next = again + nextDayTimeString;
//     System.out.println(next);
//     DateTime asDateTime = new DateTime(next);
//   }
// }