package temple.edu.bookcase;

import android.os.Parcel;
import android.os.Parcelable;

import java.net.URL;

public class Book implements Parcelable {
    int id;
    String title;
    String author;
    int published;
    int duration;
    URL coverURL;
    int progress=0;

    public Book(){}

    protected Book(Parcel in) {
        id = in.readInt();
        title = in.readString();
        author = in.readString();
        published = in.readInt();
        duration = in.readInt();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    public String toString(){
        return title+" by "+author+" with "+duration+" Pages, Published in "+published;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(author);
        parcel.writeInt(published);
        parcel.writeInt(duration);
    }
}
