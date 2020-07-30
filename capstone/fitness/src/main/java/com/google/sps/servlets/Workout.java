 
public class Workout {
  String eventID;
  String description;
 
  public Workout(String eventID, String description){
    this.eventID=eventID;
    this.description=description;
  }
 
  public String toString(){
      return this.eventID + ": " + this.description;
  }
 
  public String getEventID(){
      return this.eventID;
  }
 
  public String getDescription(){
      return this.description;
  }
 
  public void setEventID(String eventID){
      this.eventID = eventID;
  }
 
  public void setDescription(String description){
      this.description=description; 
  }
}
