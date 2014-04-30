package com.tadeaskriz.devconf2014;

import android.os.Parcel;
import android.os.Parcelable;
import org.jboss.aerogear.android.RecordId;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class Task implements Parcelable {

    @RecordId
    private Long id;
    private String text;
    private boolean done;

    public Task() { }

    private Task(Parcel parcel) {
        id = parcel.readLong();
        text = parcel.readString();
        done = parcel.readInt() > 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(text);
        dest.writeInt(done ? 1 : 0);
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {

        @Override
        public Task createFromParcel(Parcel source) {
            return new Task(source);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[0];
        }
    };
}
