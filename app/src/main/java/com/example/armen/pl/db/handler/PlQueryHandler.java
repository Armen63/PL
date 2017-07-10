package com.example.armen.pl.db.handler;

import android.content.Context;
import android.database.Cursor;

import com.example.armen.pl.db.PlDataBase;
import com.example.armen.pl.db.cursor.CursorReader;
import com.example.armen.pl.db.entity.Product;
import com.example.armen.pl.db.provider.UriBuilder;

import java.util.ArrayList;

public class PlQueryHandler {

    // ===========================================================
    // Constants
    // ===========================================================

    private final static String LOG_TAG = PlQueryHandler.class.getSimpleName();

    /**
     * PRODUCT METHODS
     *************************************************************/

    public synchronized static void addProduct(Context context, Product product) {
        context.getContentResolver().insert(
                UriBuilder.buildProductUri(),
                PlDataBase.composeValues(product, PlDataBase.PRODUCT_TABLE)
        );
    }

    public synchronized static void addProducts(Context context, ArrayList<Product> products) {
        context.getContentResolver().bulkInsert(
                UriBuilder.buildProductUri(),
                PlDataBase.composeValuesArray(products, PlDataBase.PRODUCT_TABLE)
        );
    }

    public synchronized static void updateProduct(Context context, Product product) {
        context.getContentResolver().update(
                UriBuilder.buildProductUri(),
                PlDataBase.composeValues(product, PlDataBase.PRODUCT_TABLE),
                PlDataBase.PRODUCT_ID + "=?",
                new String[]{String.valueOf(product.getId())}
        );
    }

    public synchronized static void updateProducts(Context context, ArrayList<Product> products) {
        for (Product product : products) {
            context.getContentResolver().update(
                    UriBuilder.buildProductUri(),
                    PlDataBase.composeValues(product, PlDataBase.PRODUCT_TABLE),
                    PlDataBase.PRODUCT_ID + "=?",
                    new String[]{String.valueOf(product.getId())}
            );
        }
    }

    public synchronized static Product getProduct(Context context, long id) {
        Cursor cursor = context.getContentResolver().query(
                UriBuilder.buildProductUri(id),
                PlDataBase.Projection.PRODUCT,
                null,
                null,
                null
        );
        return CursorReader.parseProduct(cursor);
    }

    public synchronized static ArrayList<Product> getProducts(Context context) {
        Cursor cursor = context.getContentResolver().query(
                UriBuilder.buildProductUri(),
                PlDataBase.Projection.PRODUCT,
                null,
                null,
                null
        );
        return CursorReader.parseProducts(cursor);
    }

    public synchronized static void deleteProduct(Context context, Product product) {
        context.getContentResolver().delete(
                UriBuilder.buildProductUri(),
                PlDataBase.PRODUCT_ID + "=?",
                new String[]{String.valueOf(product.getId())}
        );
    }

    public synchronized static void deleteProducts(Context context) {
        context.getContentResolver().delete(
                UriBuilder.buildProductUri(),
                null,
                null
        );
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}