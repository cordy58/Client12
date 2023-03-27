package Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Models.Event;
import Models.Person;

public class DataCache {
    private static DataCache instance = new DataCache();
    //the string in people should be a personID
    Map<String, Person> people;
    //the string in events should be an eventID
    Map<String, Event> events;

    String authToken;
    String username;

    public static DataCache getInstance() {
        return instance;
    }

    private DataCache() {
        people = new HashMap<>();
        events = new HashMap<>();
    }

    public void addPeople(List<Person> people) {
        for (Person person: people) {
            this.people.put(person.getPersonID(), person);
        }
    }

    public Person getPerson(String personID) {
        return people.get(personID);
    }

    public void addEvents(List<Event> events) {
        for (Event event: events) {
            this.events.put(event.getEventID(), event);
        }
    }

    public Map<String, Event> getEvents() {
        return events;
    }

    public Event getEvent(String eventID) {
        return events.get(eventID);
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }

}
