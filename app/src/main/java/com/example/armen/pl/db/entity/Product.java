package com.example.armen.pl.db.entity;


import android.os.Parcel;
import android.os.Parcelable;

import com.example.armen.pl.util.Constant;
import com.google.gson.annotations.SerializedName;

public class Product implements Parcelable {

    @SerializedName(Constant.POJO.PRODUCT_ID)
    private long id;

    @SerializedName(Constant.POJO.NAME)
    private String name;

    @SerializedName(Constant.POJO.PRICE)
    private long price;

    @SerializedName(Constant.POJO.IMAGE)
    private String image;

    @SerializedName(Constant.POJO.DESCRIPTION)
    private String description;

    private boolean isFavorite;

    private boolean isUserProduct;

    public Product() {
    }

    public Product(long id, String name, long price, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
    }

    public Product(long id, String name, long price, String image, String description, boolean isFavorite, boolean isUserProduct) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.image = image;
        this.description = description;
        this.isFavorite = isFavorite;
        this.isUserProduct = isUserProduct;
    }

    public boolean isUserProduct() {
        return isUserProduct;
    }

    public void setUserProduct(boolean userProduct) {
        isUserProduct = userProduct;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeLong(this.price);
        dest.writeString(this.image);
        dest.writeString(this.description);
        dest.writeByte(this.isFavorite ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isUserProduct ? (byte) 1 : (byte) 0);
    }

    protected Product(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.price = in.readLong();
        this.image = in.readString();
        this.description = in.readString();
        this.isFavorite = in.readByte() != 0;
        this.isUserProduct = in.readByte() != 0;
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel source) {
            return new Product(source);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}
