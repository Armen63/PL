package com.example.armen.pl.db.cursor;

import android.database.Cursor;
import android.support.annotation.Nullable;

import com.example.armen.pl.db.PlDataBase;
import com.example.armen.pl.db.entity.Product;
import com.example.armen.pl.util.AppUtil;

import java.util.ArrayList;

public class CursorReader {

    // ===========================================================
    // Constants
    // ===========================================================

    private final static String LOG_TAG = CursorReader.class.getSimpleName();

    // ===========================================================
    // Fields
    // ===========================================================

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================

    /**
     * PRODUCT
     *************************************************************/

    @Nullable
    public static Product parseProduct(Cursor cursor) {
        Product product = null;
        if (cursor != null && !cursor.isClosed() && cursor.moveToFirst()) {
            product = composeProduct(cursor);
        }

        if (cursor != null) {
            cursor.close();
        }
        return product;
    }

    public static ArrayList<Product> parseProducts(Cursor cursor) {
        ArrayList<Product> userArrayList = new ArrayList<>();
        if (cursor != null && !cursor.isClosed() && cursor.moveToFirst()) {
            do {
                Product product = composeProduct(cursor);
                userArrayList.add(product);

            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        return userArrayList;
    }


    private static Product composeProduct(Cursor cursor) {
        Product product = new Product();
        product.setId(cursor.getLong(cursor.getColumnIndex(PlDataBase.PRODUCT_ID)));
        product.setName(cursor.getString(cursor.getColumnIndex(PlDataBase.PRODUCT_NAME)));
        product.setImage(cursor.getString(cursor.getColumnIndex(PlDataBase.PRODUCT_IMAGE)));
        product.setUserProduct(AppUtil.intToBoolean(cursor.getInt(cursor.getColumnIndex(PlDataBase.PRODUCT_USER))));
        product.setFavorite(AppUtil.intToBoolean(cursor.getInt(cursor.getColumnIndex(PlDataBase.PRODUCT_FAVORITE))));
        product.setDescription(cursor.getString(cursor.getColumnIndex(PlDataBase.PRODUCT_DESCRIPTION)));
        product.setPrice(cursor.getLong(cursor.getColumnIndex(PlDataBase.PRODUCT_PRICE)));
        return product;
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}