package it.unimib.adastra.model.NASA;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class NASAResponse implements Parcelable {

    @PrimaryKey
    @NonNull
    @SerializedName("date")
    private String apodDate;

    @SerializedName("title")
    private String apodTitle;

    @SerializedName("explanation")
    private String apodExplanation;

    @SerializedName("url")
    private String apodUrl;

    @SerializedName("copyright")
    private String apodCopyright;

    public NASAResponse() {}

    protected NASAResponse(Parcel in) {
        apodDate = in.readString();
        apodTitle = in.readString();
        apodExplanation = in.readString();
        apodUrl = in.readString();
        apodCopyright = in.readString();
    }

    public String getApodDate() {
        return apodDate;
    }

    public void setApodDate(String apodDate) {
        this.apodDate = apodDate;
    }

    public String getApodTitle() {
        return apodTitle;
    }

    public void setApodTitle(String apodTitle) {
        this.apodTitle = apodTitle;
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

    public String getApodCopyright() {
        return apodCopyright;
    }

    public void setApodCopyright(String apodCopyright) {
        this.apodCopyright = apodCopyright;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(apodDate);
        dest.writeString(apodTitle);
        dest.writeString(apodExplanation);
        dest.writeString(apodUrl);
        dest.writeString(apodCopyright);
    }

    public void readFromParcel(Parcel source) {
        apodDate = source.readString();
        apodTitle = source.readString();
        apodExplanation = source.readString();
        apodUrl = source.readString();
        apodCopyright = source.readString();
    }

    @Override
    public String toString() {
        return "NASAResponse{" +
                ", apodDate='" + apodDate + '\'' +
                ", apodTitle='" + apodTitle + '\'' +
                ", apodExplanation='" + apodExplanation + '\'' +
                ", apodUrl='" + apodUrl + '\'' +
                ", apoCopyright='" + apodCopyright + '\'' +
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
