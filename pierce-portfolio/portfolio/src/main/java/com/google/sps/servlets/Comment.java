package com.google.sps.servlets;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Comment {
      String name;
      String text;
      String timestamp;
      public Comment(String tName, String tText){
          this.name=tName;
          this.text=tText;
          this.timestamp= new Date().toString();
      }
      public Comment(String tName, String tText, String tTimestamp){
          this.name=tName;
          this.text=tText;
          this.timestamp= tTimestamp;
      }
      public String toString(){
          return this.name + " said " + "\n" + this.text + "\n" + " (" + this.timestamp + ")";
      }
      public String getName(){
          return this.name;
      }
      
      public String getText(){
          return this.text;
      }
    
      public String getTimestamp(){
          return this.timestamp;
      }

      public void setName(String newName){
          this.name = newName;
      }
      public void setText(String newText){
          this.text = newText;
      }
      public void setTimestamp(String newTimestamp){
          this.timestamp = newTimestamp;
      }
    }