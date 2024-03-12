package it.unimib.adastra.model.ISS;

import com.google.firebase.firestore.Exclude;

public class User {
    private String id;
    private String username;
    private String email;
    private boolean imperialSystem;
    private boolean timeFormat;
    private boolean ISSNotification;
    private boolean eventsNotifications;






    public User(String id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.imperialSystem = false;
        this.timeFormat = false;
        this.ISSNotification = true;
        this.eventsNotifications = true;
    }

    public User(String id,String username, String email, boolean imperialSystem, boolean timeFormat, boolean ISSNotification, boolean eventsNotifications) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.imperialSystem = imperialSystem;
        this.timeFormat = timeFormat;
        this.ISSNotification = ISSNotification;
        this.eventsNotifications = eventsNotifications;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isImperialSystem() {
        return imperialSystem;
    }

    public void setImperialSystem(boolean imperialSystem) {
        this.imperialSystem = imperialSystem;
    }

    public boolean isTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(boolean timeFormat) {
        this.timeFormat = timeFormat;
    }

    public boolean isISSNotification() {
        return ISSNotification;
    }

    public void setISSNotification(boolean ISSNotification) {
        this.ISSNotification = ISSNotification;
    }

    public boolean isEventsNotifications() {
        return eventsNotifications;
    }

    public void setEventsNotifications(boolean eventsNotifications) {
        this.eventsNotifications = eventsNotifications;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", imperialSystem=" + imperialSystem +
                ", timeFormat=" + timeFormat +
                ", ISSNotification=" + ISSNotification +
                ", eventsNotifications=" + eventsNotifications +
                '}';
    }
}