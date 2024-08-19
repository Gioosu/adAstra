package it.unimib.adastra.model;

import it.unimib.adastra.model.ISS.ISSPositionResponse;
import it.unimib.adastra.model.user.User;

public abstract class Result {
    private Result() {}

    public boolean isSuccess() {
        if (this instanceof ISSPositionResponseSuccess || this instanceof UserResponseSuccess) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Class that represents a successful action during the interaction
     * with a Web Service or a local database.
     */
    public static final class ISSPositionResponseSuccess extends Result {
        private final ISSPositionResponse issPositionResponse;

        public ISSPositionResponseSuccess(ISSPositionResponse issPositionResponse) {
            this.issPositionResponse = issPositionResponse;
        }

        public ISSPositionResponse getData() {
            return issPositionResponse;
        }
    }

    /**
     * Class that represents a successful action during the interaction
     * with a Web Service or a local database.
     */
    public static final class UserResponseSuccess extends Result {
        private final User user;
        public UserResponseSuccess(User user) {
            this.user = user;
        }
        public User getUser() {
            return user;
        }
    }

    /**
     * Class that represents an error occurred during the interaction
     * with a Web Service or a local database.
     */
    public static final class Error extends Result {
        private final String message;
        public Error(String message) {
            this.message = message;
        }
        public String getMessage() {
            return message;
        }
    }
}