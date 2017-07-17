package com.example.armen.pl.db.provider;

import android.net.Uri;

/**
 * Created by simonyan51 on 7/5/17.
 */

public class UriBuilder {

    public static Uri buildProductUri(long id) {
        return Uri.parse("content://" + PlProvider.AUTHORITY + "/"
                + PlProvider.Path.PRODUCT_LOCATION + "/" + id);
    }

    public static Uri buildProductUri() {
        return Uri.parse("content://" + PlProvider.AUTHORITY + "/"
                + PlProvider.Path.PRODUCT_LOCATION);
    }

}
