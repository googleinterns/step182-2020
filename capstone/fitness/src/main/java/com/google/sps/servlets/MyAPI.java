package com.google.sps.servlets;
import java.util.Date;
import java.util.Collection;

interface MyAPI {

  String CreateEvent(String title, String description, Date date);

  Event GetEvent(String id);

  // com.google.api.client.util.DateTime
  // how am I going to see whether there is an event at a certain time?
  Collection<Event> GetEvents(Date min, Date max);
}