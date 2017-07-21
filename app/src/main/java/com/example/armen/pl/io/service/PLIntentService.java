package com.example.armen.pl.io.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.armen.pl.db.entity.Product;
import com.example.armen.pl.db.entity.ProductResponse;
import com.example.armen.pl.db.handler.PlQueryHandler;
import com.example.armen.pl.io.bus.BusProvider;
import com.example.armen.pl.io.bus.event.ApiEvent;
import com.example.armen.pl.io.rest.HttpRequestManager;
import com.example.armen.pl.io.rest.HttpResponseUtil;
import com.example.armen.pl.util.Constant;
import com.google.gson.Gson;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class PLIntentService extends IntentService {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = PLIntentService.class.getSimpleName();

    private class Extra {
        static final String URL = "PRODUCT_LIST";
        static final String POST_ENTITY = "POST_ENTITY";
        static final String REQUEST_TYPE = "REQUEST_TYPE";
    }

    // ===========================================================
    // Fields
    // ===========================================================

    // ===========================================================
    // Constructors
    // ===========================================================

    public PLIntentService() {
        super(PLIntentService.class.getName());
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    // ===========================================================
    // Start/stop commands
    // ===========================================================

    /**
     * @param url         - calling api url
     * @param requestType - string constant that helps us to distinguish what request it is
     * @param postEntity  - POST request entity (json string that must be sent on server)
     */

    public static void start(Context context, String url, String postEntity, int requestType) {
        Intent intent = new Intent(context, PLIntentService.class);
        intent.putExtra(Extra.URL, url);
        intent.putExtra(Extra.REQUEST_TYPE, requestType);
        intent.putExtra(Extra.POST_ENTITY, postEntity);
        context.startService(intent);
    }

    public static void start(Context context, String url, int requestType) {
        Intent intent = new Intent(context, PLIntentService.class);
        intent.putExtra(Extra.URL, url);
        intent.putExtra(Extra.REQUEST_TYPE, requestType);
        context.startService(intent);
    }

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    @Override
    protected void onHandleIntent(Intent intent) {
        String url = intent.getExtras().getString(Extra.URL);
        String data = intent.getExtras().getString(Extra.POST_ENTITY);
        int requestType = intent.getExtras().getInt(Extra.REQUEST_TYPE);

        Log.i(LOG_TAG, requestType + Constant.Symbol.SPACE + url);

        HttpURLConnection connection;

        switch (requestType) {
            case HttpRequestManager.RequestType.PRODUCT_LIST:

                connection = HttpRequestManager.executeRequest(
                        url,
                        HttpRequestManager.RequestMethod.GET,
                        null
                );

                String jsonList = HttpResponseUtil.parseResponse(connection);

                ProductResponse productResponse = new Gson().fromJson(jsonList, ProductResponse.class);

                if (productResponse != null) {

                    ArrayList<Product> products = productResponse.getProducts();
                    for (int i = 0; i < products.size(); ++i) {
                        products.get(i).setUserProduct(false);
                    }

                    PlQueryHandler.addProducts(this, products);

                    BusProvider.getInstance().post(new ApiEvent<>(ApiEvent.EventType.PRODUCT_LIST_LOADED, true, products));

                } else {
                    BusProvider.getInstance().post(new ApiEvent<>(ApiEvent.EventType.PRODUCT_LIST_LOADED, false));

                }

                break;

            case HttpRequestManager.RequestType.PRODUCT_ITEM:

                connection = HttpRequestManager.executeRequest(
                        url,
                        HttpRequestManager.RequestMethod.GET,
                        null
                );

                String jsonItem = HttpResponseUtil.parseResponse(connection);

                Product product = new Gson().fromJson(jsonItem, Product.class);
                // TODO validation
                if(product != null) {
                    PlQueryHandler.updateProductDescription(this, product);

                    BusProvider.getInstance().post(product);
                    BusProvider.getInstance().post(new ApiEvent<>(ApiEvent.EventType.PRODUCT_ITEM_LOADED, false));

                }
                break;
        }

    }

// ===========================================================
// Methods
// ===========================================================

// ===========================================================
// Util
// ===========================================================

}