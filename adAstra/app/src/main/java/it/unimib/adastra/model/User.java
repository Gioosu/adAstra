package it.unimib.adastra.model;

import static it.unimib.adastra.util.Constants.EVENTS_NOTIFICATIONS;
import static it.unimib.adastra.util.Constants.IMPERIAL_SYSTEM;
import static it.unimib.adastra.util.Constants.ISS_NOTIFICATIONS;
import static it.unimib.adastra.util.Constants.TIME_FORMAT;

public class User {
    private String id;
    private String username;
    private boolean imperialSystem;
    private boolean timeFormat;
    private boolean issNotifications;
    private boolean eventsNotifications;
    private boolean verified;

    public User(String id, String username) {
        this.id = id;
        this.username = username;
        this.imperialSystem = false;
        this.timeFormat = false;
        this.issNotifications = true;
        this.eventsNotifications = true;
        this.verified = false;
    }

    public User(String id,String username, boolean imperialSystem, boolean timeFormat, boolean issNotifications, boolean eventsNotifications, boolean verified) {
        this.id = id;
        this.username = username;
        this.imperialSystem = imperialSystem;
        this.timeFormat = timeFormat;
        this.issNotifications = issNotifications;
        this.eventsNotifications = eventsNotifications;
        this.verified = verified;
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

    public boolean isIssNotifications() {
        return issNotifications;
    }

    public void setIssNotifications(boolean issNotifications) {
        this.issNotifications = issNotifications;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public boolean isEventsNotifications() {
        return eventsNotifications;
    }

    public void setEventsNotifications(boolean eventsNotifications) {
        this.eventsNotifications = eventsNotifications;
    }

    public void setSwitch (String key, Boolean value) {
        if (value == null)
            return;

        switch (key) {
            case IMPERIAL_SYSTEM:
                setImperialSystem(value);
                break;
            case TIME_FORMAT:
                setTimeFormat(value);
                break;
            case ISS_NOTIFICATIONS:
                setIssNotifications(value);
                break;
            case EVENTS_NOTIFICATIONS:
                setEventsNotifications(value);
                break;
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", imperialSystem=" + imperialSystem +
                ", timeFormat=" + timeFormat +
                ", issNotifications=" + issNotifications +
                ", eventsNotifications=" + eventsNotifications +
                ", verified=" + verified +
                '}';
    }
}