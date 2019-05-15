package com.example.course_work_2019.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class OfferParcelable implements Parcelable {
    private String user_id;
    private String offer_id;
    private String name;
    private String description;
    private String category;
    private String city;
    private String pers_name;
    private String phone;
    private String soc_netw;
    private Long numPhotos = 0L;
    private String main_photo;
    private String[] photos;
    private String date;
    private boolean find_or_offer;

    @Override
    public int describeContents() {
        return 0;
    }

    public OfferParcelable(Offer offer){
        user_id = offer.getUser_id();
        offer_id = offer.getOffer_id();
        name = offer.getName();
        description = offer.getDescription();
        category = offer.getCategory();
        city = offer.getCity();
        pers_name = offer.getPers_name();
        phone = offer.getPhone();
        soc_netw = offer.getSoc_netw();
        numPhotos = offer.getNumPhotos();
        main_photo = offer.getMain_photo();
        photos = offer.getPhotos();
        date = offer.getDate();
        find_or_offer = offer.isFind_or_offer();
    }

    public Offer makeOffer(){
        Offer offer = new Offer();
        offer.setUser_id(user_id);
        offer.setOffer_id(offer_id);
        offer.setName(name);
        offer.setDescription(description);
        offer.setCategory(category);
        offer.setCity(city);
        offer.setPers_name(pers_name);
        offer.setPhone(phone);
        offer.setSoc_netw(soc_netw);
        offer.setNumPhotos(numPhotos);
        offer.setMain_photo(main_photo);
        offer.setPhotos(photos);
        offer.setDate(date);
        offer.setFind_or_offer(find_or_offer);
        return offer;
    }

    private OfferParcelable(Parcel in) {
        user_id = in.readString();
        offer_id = in.readString();
        name = in.readString();
        description = in.readString();
        category = in.readString();
        city = in.readString();
        pers_name = in.readString();
        phone = in.readString();
        soc_netw = in.readString();
        numPhotos = in.readLong();
        main_photo = in.readString();
        photos = in.createStringArray();
        date = in.readString();
        find_or_offer = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(user_id);
        dest.writeString(offer_id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(category);
        dest.writeString(city);
        dest.writeString(pers_name);
        dest.writeString(phone);
        dest.writeString(soc_netw);
        dest.writeLong(numPhotos);
        dest.writeString(main_photo);
        dest.writeStringArray(photos);
        dest.writeString(date);
        dest.writeByte((byte) (find_or_offer ? 1 : 0));
    }


    public static final Creator<OfferParcelable> CREATOR = new Creator<OfferParcelable>() {
        @Override
        public OfferParcelable createFromParcel(Parcel in) {
            return new OfferParcelable(in);
        }

        @Override
        public OfferParcelable[] newArray(int size) {
            return new OfferParcelable[size];
        }
    };
}
