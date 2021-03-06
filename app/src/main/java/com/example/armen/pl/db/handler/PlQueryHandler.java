package com.example.armen.pl.db.handler;

import android.content.Context;
import android.database.Cursor;

import com.example.armen.pl.db.PlDataBase;
import com.example.armen.pl.db.cursor.CursorReader;
import com.example.armen.pl.db.entity.Product;
import com.example.armen.pl.db.provider.UriBuilder;
import com.example.armen.pl.util.AppUtil;

import java.util.ArrayList;


public class PlQueryHandler {

    // ===========================================================
    // Constants
    // ===========================================================

    private final static String LOG_TAG = PlQueryHandler.class.getSimpleName();

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

    public synchronized static void addProduct(Context context, Product product) {
        context.getContentResolver().insert(
                UriBuilder.buildProductUri(),
                PlDataBase.composeValues(product, PlDataBase.ContentValuesType.PRODUCTS)
        );
    }

    public synchronized static void addProducts(Context context, ArrayList<Product> products) {
        context.getContentResolver().bulkInsert(
                UriBuilder.buildProductUri(),
                PlDataBase.composeValuesArray(products, PlDataBase.ContentValuesType.PRODUCTS)
        );
    }

    public synchronized static void updateProduct(Context context, Product product) {
        context.getContentResolver().update(
                UriBuilder.buildProductUri(),
                PlDataBase.composeValues(product, PlDataBase.ContentValuesType.PRODUCTS),
                PlDataBase.PRODUCT_ID + "=?",
                new String[]{String.valueOf(product.getId())}
        );
    }

    public synchronized static void updateProductDescription(Context context, Product product) {
        context.getContentResolver().update(
                UriBuilder.buildProductUri(),
                PlDataBase.composeValues(product, PlDataBase.ContentValuesType.DESCRIPTION),
                PlDataBase.PRODUCT_ID + "=?",
                new String[]{String.valueOf(product.getId())}
        );
    }

    public synchronized static void updateProducts(Context context, ArrayList<Product> products) {
        for (Product product : products) {
            context.getContentResolver().update(
                    UriBuilder.buildProductUri(),
                    PlDataBase.composeValues(product, PlDataBase.ContentValuesType.PRODUCTS),
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

    public synchronized static ArrayList<Product> getAllFavoriteProducts(Context context) {
        Cursor cursor = context.getContentResolver().query(
                UriBuilder.buildProductUri(),
                PlDataBase.Projection.PRODUCT,
                PlDataBase.PRODUCT_FAVORITE + "=?",
                new String[]{String.valueOf(AppUtil.booleanToInt(true))},
                null
        );
        return CursorReader.parseProducts(cursor);
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}