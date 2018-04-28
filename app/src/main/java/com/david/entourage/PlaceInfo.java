package com.david.entourage;

import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.david.entourage.Application.AppController;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;
import java.util.Observable;

/**
 * Created by David on 3/26/2018.
 */

public class PlaceInfo extends Observable implements Parcelable{
    private String address;
    private String attributions;
    private String id;
    private LatLng latLng;
    private Locale locale;
    private String name;
    private String phoneNumber;
    private int priceLevel;
    private float rating;
    private LatLngBounds latLngBounds;
    private Uri uri;

    private ArrayList<Bitmap> photos;

    public PlaceInfo(Place place) {
        this.address = place.getAddress().toString();
        this.attributions = place.getAttributions() != null ? place.getAttributions().toString() : null;
        this.id = place.getId();
        this.latLng = place.getLatLng();
        this.locale = place.getLocale();
        this.name = place.getName().toString();
        this.phoneNumber = place.getPhoneNumber().toString();
        this.priceLevel = place.getPriceLevel();
        this.rating = place.getRating();
        this.latLngBounds = place.getViewport();
        this.uri = place.getWebsiteUri();

        this.photos = new ArrayList<>();

        setChanged();
        notifyObservers();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(address);
        parcel.writeString(attributions);
        parcel.writeString(id);
        parcel.writeParcelable(latLng, i);
        parcel.writeString(name);
        parcel.writeString(phoneNumber);
        parcel.writeInt(priceLevel);
        parcel.writeFloat(rating);
        parcel.writeParcelable(latLngBounds, i);
        parcel.writeParcelable(uri, i);
        parcel.writeTypedList(photos);
    }


    protected PlaceInfo(Parcel in) {
        address = in.readString();
        attributions = in.readString();
        id = in.readString();
        latLng = in.readParcelable(LatLng.class.getClassLoader());
        name = in.readString();
        phoneNumber = in.readString();
        priceLevel = in.readInt();
        rating = in.readFloat();
        latLngBounds = in.readParcelable(LatLngBounds.class.getClassLoader());
        uri = in.readParcelable(Uri.class.getClassLoader());
        photos = in.createTypedArrayList(Bitmap.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
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

    public String getAddress() {
        return address;
    }

    public String getAttributions() {
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

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
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

    public float getDistance(){
        float results[] = new float[1];
        Location.distanceBetween(AppController.getLastKnownLocation().getLatitude(),AppController.getLastKnownLocation().getLongitude(),latLng.latitude,latLng.longitude,results);
        return results[0];
    }

    public static Comparator<PlaceInfo> PlaceInfoCompDist = new Comparator<PlaceInfo>() {
        @Override
        public int compare(PlaceInfo placeInfo, PlaceInfo t1) {
            return (int)(placeInfo.getDistance() - t1.getDistance());
        }
    };

    public static Comparator<PlaceInfo> PlaceInfoCompName = new Comparator<PlaceInfo>() {
        @Override
        public int compare(PlaceInfo placeInfo, PlaceInfo t1) {
            return placeInfo.getName().toString().compareTo(t1.getName().toString());
        }
    };

    public static Comparator<PlaceInfo> PlaceInfoCompRating = new Comparator<PlaceInfo>() {
        @Override
        public int compare(PlaceInfo placeInfo, PlaceInfo t1) {
            float change = t1.getRating() - placeInfo.getRating();
            return change > 0 ?  1 : change < 0 ? -1 : 0;
        }
    };
}
