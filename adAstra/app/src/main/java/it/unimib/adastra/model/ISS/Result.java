package it.unimib.adastra.model.ISS;

public abstract class Result {
    private Result() {}

    public boolean isSuccess() {
        if (this instanceof ISSPositionResponseSuccess || this instanceof UserResponseSuccess) {
            return true;
        } else {
            return false;
        }
    }

    public static final class ISSPositionResponseSuccess extends Result {
        private final ISSPositionResponse issPositionResponse;
        public ISSPositionResponseSuccess(ISSPositionResponse issPositionResponse) {
            this.issPositionResponse = issPositionResponse;
        }
        public ISSPositionResponse getData() {
            return issPositionResponse;
        }
    }

    public static final class UserResponseSuccess extends Result {
        private final User user;
        public UserResponseSuccess(User user) {
            this.user = user;
        }

        public User getUser() {
            return user;
        }

    }

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