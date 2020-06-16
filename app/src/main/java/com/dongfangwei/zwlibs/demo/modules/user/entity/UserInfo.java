package com.dongfangwei.zwlibs.demo.modules.user.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class UserInfo implements Parcelable {
    private int id;
    private String username;
    private String photo;

    public UserInfo() {
    }

    protected UserInfo(Parcel in) {
        id = in.readInt();
        username = in.readString();
        photo = in.readString();
    }

    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel in) {
            return new UserInfo(in);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(username);
        dest.writeString(photo);
    }
}
