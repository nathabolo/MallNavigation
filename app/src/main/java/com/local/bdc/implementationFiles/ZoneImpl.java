package com.local.bdc.implementationFiles;

import android.os.Parcel;

public class ZoneImpl extends java.lang.Object implements android.os.Parcelable {
    protected ZoneImpl(Parcel in) {
    }

    public static final Creator<ZoneImpl> CREATOR = new Creator<ZoneImpl>() {
        @Override
        public ZoneImpl createFromParcel(Parcel in) {
            return new ZoneImpl(in);
        }

        @Override
        public ZoneImpl[] newArray(int size) {
            return new ZoneImpl[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    }
}
