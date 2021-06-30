package com.andriod.simplenote.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentId;

import java.util.Calendar;

public class Note implements Parcelable {

    private static final int SHORT_CONTENT_LENGTH = 30;
    private static final String TAG = "@@@Note@";
    @DocumentId
    private String id;
    private String header;
    private long date;
    private boolean favorite;

    public enum NoteType {
        Text(0), HTTP(1), Video(2);
        private final int value;

        NoteType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    private NoteType type;

    private String content;

    public Note() {
        this(NoteType.Text, null, getCurrentDate());
    }

    public Note(NoteType type) {
        this(type, null, getCurrentDate());
    }

    public Note(NoteType type, String header, long date) {
        this.type = type;
        this.header = header;
        this.date = date;
    }

    protected Note(Parcel in) {
        id = in.readString();
        header = in.readString();
        date = in.readLong();
        favorite = in.readByte() == 1;
        content = in.readString();
        type = NoteType.valueOf(in.readString());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(header);
        dest.writeLong(date);
        dest.writeByte((byte) (favorite ? 1 : 0));
        dest.writeString(content);
        dest.writeString(type.name());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    private static long getCurrentDate() {
        return Calendar.getInstance().getTimeInMillis();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public NoteType getType() {
        return type;
    }

    public void setType(NoteType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getShortContent() {
        if (content == null || content.isEmpty() || content.length() <= SHORT_CONTENT_LENGTH)
            return content;
        return String.format("%s...", content.substring(0, SHORT_CONTENT_LENGTH));
    }

    public boolean checkSearch(String searchQuery) {
        if (searchQuery == null || searchQuery.isEmpty()) return false;

        if (header != null && header.contains(searchQuery)) return true;
        return content != null && content.contains(searchQuery);
    }
}
