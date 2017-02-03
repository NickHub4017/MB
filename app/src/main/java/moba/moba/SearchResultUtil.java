package moba.moba;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by nrv on 2/2/17.
 */

public class SearchResultUtil implements Parcelable{
    String titile;
    String link;
    String description;

    protected SearchResultUtil(Parcel in) {
        titile = in.readString();
        link = in.readString();
        description = in.readString();
    }

    public static final Creator<SearchResultUtil> CREATOR = new Creator<SearchResultUtil>() {
        @Override
        public SearchResultUtil createFromParcel(Parcel in) {
            return new SearchResultUtil(in);
        }

        @Override
        public SearchResultUtil[] newArray(int size) {
            return new SearchResultUtil[size];
        }
    };

    public String getTitile() {
        return titile;
    }

    public void setTitile(String titile) {
        this.titile = titile;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SearchResultUtil(String titile, String link, String description) {
        this.titile = titile;
        this.link = link;
        this.description = description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(titile);
        parcel.writeString(link);
        parcel.writeString(description);
    }
}
