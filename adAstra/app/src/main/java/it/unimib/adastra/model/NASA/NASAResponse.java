package it.unimib.adastra.model.NASA;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class NASAResponse implements Parcelable {

    @PrimaryKey
    private int id;

    private String apodTitle;
    private String apodDate;
    private String apodExplanation;
    private String apodUrl;

    public NASAResponse() {}
    //TODO: Aggiungere altri costruttori?

    protected NASAResponse(Parcel in) {
        id = in.readInt();
        apodExplanation = in.readString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getApodTitle() {
        return apodTitle;
    }

    public void setApodTitle(String apodTitle) {
        this.apodTitle = apodTitle;
    }

    public String getApodDate() {
        return apodDate;
    }

    public void setApodDate(String apodDate) {
        this.apodDate = apodDate;
    }

    public String getApodExplanation() {
        return apodExplanation;
    }

    public void setApodExplanation(String apodExplanation) {
        this.apodExplanation = apodExplanation;
    }

    public String getApodUrl() {
        return apodUrl;
    }

    public void setApodUrl(String apodUrl) {
        this.apodUrl = apodUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(apodTitle);
        dest.writeString(apodDate);
        dest.writeString(apodExplanation);
        dest.writeString(apodUrl);
    }

    public void readFromParcel(Parcel source) {
        id = source.readInt();
        apodTitle = source.readString();
        apodDate = source.readString();
        apodExplanation = source.readString();
        apodUrl = source.readString();
    }

    @Override
    public String toString() {
        return "NASAResponse{" +
                "id=" + id +
                ", apodTitle='" + apodTitle + '\'' +
                ", apodDate='" + apodDate + '\'' +
                ", apodExplanation='" + apodExplanation + '\'' +
                ", apodUrl='" + apodUrl + '\'' +
                '}';
    }

    public static final Creator<NASAResponse> CREATOR = new Creator<NASAResponse>() {
        @Override
        public NASAResponse createFromParcel(Parcel in) {
            return new NASAResponse(in);
        }

        @Override
        public NASAResponse[] newArray(int size) {
            return new NASAResponse[size];
        }
    };
}
