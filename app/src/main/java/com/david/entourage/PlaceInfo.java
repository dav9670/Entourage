package com.david.entourage;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.common.data.DataBufferObserver;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Observable;

/**
 * Created by David on 3/26/2018.
 */

public class PlaceInfo extends Observable implements Parcelable{
    private CharSequence adress;
    private CharSequence attributions;
    private String id;
    private LatLng latLng;
    private Locale locale;
    private CharSequence name;
    private CharSequence phoneNumber;
    private int priceLevel;
    private float rating;
    private LatLngBounds latLngBounds;
    private Uri uri;

    private ArrayList<Bitmap> photos;

    public PlaceInfo(Place place) {
        this.adress = place.getAddress();
        this.attributions = place.getAttributions();
        this.id = place.getId();
        this.latLng = place.getLatLng();
        this.locale = place.getLocale();
        this.name = place.getName();
        this.phoneNumber = place.getPhoneNumber();
        this.priceLevel = place.getPriceLevel();
        this.rating = place.getRating();
        this.latLngBounds = place.getViewport();
        this.uri = place.getWebsiteUri();

        this.photos = new ArrayList<>();

        setChanged();
        notifyObservers();
    }

    protected PlaceInfo(Parcel in) {
        id = in.readString();
        latLng = in.readParcelable(LatLng.class.getClassLoader());
        priceLevel = in.readInt();
        rating = in.readFloat();
        latLngBounds = in.readParcelable(LatLngBounds.class.getClassLoader());
        uri = in.readParcelable(Uri.class.getClassLoader());
        photos = in.createTypedArrayList(Bitmap.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeParcelable(latLng, i);
        parcel.writeInt(priceLevel);
        parcel.writeFloat(rating);
        parcel.writeParcelable(latLngBounds, i);
        parcel.writeParcelable(uri, i);
        parcel.writeTypedList(photos);
    }

    public static final Creator<PlaceInfo> CREATOR = new Creator<PlaceInfo>() {
        @Override
        public PlaceInfo createFromParcel(Parcel in) {
            return new PlaceInfo(in);
        }

        @Override
        public PlaceInfo[] newArray(int size) {
            return new PlaceInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public CharSequence getAdress() {
        return adress;
    }

    public CharSequence getAttributions() {
        return attributions;
    }

    public String getId() {
        return id;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public Locale getLocale() {
        return locale;
    }

    public CharSequence getName() {
        return name;
    }

    public CharSequence getPhoneNumber() {
        return phoneNumber;
    }

    public int getPriceLevel() {
        return priceLevel;
    }

    public float getRating() {
        return rating;
    }

    public LatLngBounds getLatLngBounds() {
        return latLngBounds;
    }

    public Uri getUri() {
        return uri;
    }

    public ArrayList<Bitmap> getPhotos() {
        return photos;
    }

    public void addPhoto(Bitmap photo){
        photos.add(photo);
    }
}
