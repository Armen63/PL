package com.example.armen.pl.db.entity;


import com.example.armen.pl.util.Constant;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


public class ProductResponse {

    @SerializedName(Constant.POJO.PRODUCTS)
    private ArrayList<Product> products;

    public ProductResponse() {
    }

    public ProductResponse(ArrayList<Product> products) {
        this.products = products;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }
}
