package it.unimib.adastra.model.user;

import static it.unimib.adastra.util.Constants.IMPERIAL_SYSTEM;
import static it.unimib.adastra.util.Constants.TIME_FORMAT;

public class User {
    private String id;
    private String username;
    private boolean imperialSystem;
    private boolean timeFormat;
    private boolean verified;

    public User(String id, String username) {
        this.id = id;
        this.username = username;
        this.imperialSystem = false;
        this.timeFormat = false;
        this.verified = false;
    }

    public User(String id,String username, boolean imperialSystem, boolean timeFormat, boolean verified) {
        this.id = id;
        this.username = username;
        this.imperialSystem = imperialSystem;
        this.timeFormat = timeFormat;
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

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
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
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "id = '" + id + '\'' +
                ", username = '" + username + '\'' +
                ", imperialSystem = " + imperialSystem +
                ", timeFormat = " + timeFormat +
                ", verified = " + verified +
                '}';
    }
}