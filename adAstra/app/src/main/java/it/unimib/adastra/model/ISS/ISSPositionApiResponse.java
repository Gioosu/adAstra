package it.unimib.adastra.model.ISS;

import android.os.Parcel;

import androidx.room.Entity;
import androidx.room.Ignore;

// Class to represent the source of ISS information
@Entity
public class ISSPositionApiResponse extends ISSPositionResponse {
    private String message;

    @Ignore
    public ISSPositionApiResponse(){
        super();
    }

    public ISSPositionApiResponse(String message, Coordinates coordinates, long timestamp) {
        super(coordinates, timestamp);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ISSApiResponse{" +
                "message='" + message + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.message);
    }

    public void readFromParcel(Parcel source) {
        super.readFromParcel(source);
        this.message = source.readString();
    }

    protected ISSPositionApiResponse(Parcel in) {
        super(in);
        this.message = in.readString();
    }

    public static final Creator<ISSPositionApiResponse> CREATOR = new Creator<ISSPositionApiResponse>() {
        @Override
        public ISSPositionApiResponse createFromParcel(Parcel source) {
            return new ISSPositionApiResponse(source);
        }

        @Override
        public ISSPositionApiResponse[] newArray(int size) {
            return new ISSPositionApiResponse[size];
        }
    };

}