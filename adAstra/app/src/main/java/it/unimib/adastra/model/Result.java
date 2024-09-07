package it.unimib.adastra.model;

import java.util.List;

import it.unimib.adastra.model.ISS.ISSPositionResponse;
import it.unimib.adastra.model.NASA.NASAResponse;
import it.unimib.adastra.model.user.User;
import it.unimib.adastra.model.wiki.WikiObj;

public abstract class Result {
    private Result() {}

    public boolean isSuccess() {
        if (this instanceof ISSPositionResponseSuccess ||
                this instanceof UserResponseSuccess ||
                this instanceof NASAResponseSuccess ||
                this instanceof WikiResponseSuccess) {
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

    public static final class WikiResponseSuccess extends Result {
        private final List<WikiObj> wikiObjs;

        public WikiResponseSuccess(List<WikiObj> wikiObjs) {
            this.wikiObjs = wikiObjs;
        }

        public List<WikiObj> getWikiObjs() {
            return wikiObjs;
        }
    }

    /**
     * Class that represents a successful action during the interaction
     * with a Web Service or a local database.
     */
    public static final class NASAResponseSuccess extends Result {
        private final NASAResponse nasaResponse;

        public NASAResponseSuccess(NASAResponse nasaResponse) {
            this.nasaResponse = nasaResponse;
        }
        public NASAResponse getData() {
            return nasaResponse;
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