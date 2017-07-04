package com.example.armen.pl.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.armen.pl.R;
import com.example.armen.pl.db.entity.Product;
import com.example.armen.pl.io.bus.BusProvider;
import com.example.armen.pl.io.rest.HttpRequestManager;
import com.example.armen.pl.io.sevice.PLIntentService;
import com.example.armen.pl.util.Constant;
import com.google.common.eventbus.Subscribe;

import java.util.ArrayList;

public class ProductListFragment extends BaseFragment implements View.OnClickListener {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = ProductListFragment.class.getSimpleName();

    // ===========================================================
    // Fields
    // ===========================================================

    private Bundle mArgumentData;

    // ===========================================================
    // Constructors
    // ===========================================================

    public static ProductListFragment newInstance() {
        return new ProductListFragment();
    }

    public static ProductListFragment newInstance(Bundle args) {
        ProductListFragment fragment = new ProductListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);
        BusProvider.register(this);
        findViews(view);
        setListeners();
        getData();
        customizeActionBar();

        PLIntentService.start(
                getActivity(),
                Constant.API.PRODUCT_LIST,
                HttpRequestManager.RequestType.PRODUCT_LIST
        );

        PLIntentService.start(
                getActivity(),
                Constant.API.PRODUCT_ITEM,
                HttpRequestManager.RequestType.PRODUCT_ITEM
        );

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BusProvider.unregister(this);
    }

    // ===========================================================
    // Click Listeners
    // ===========================================================

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    // ===========================================================
    // Other Listeners, methods for/from Interfaces
    // ===========================================================

    @Subscribe
    public void onEventReceived(ArrayList<Product> productArrayList) {
        Log.d(LOG_TAG, "Size" + productArrayList.size());
    }

    // ===========================================================
    // Methods
    // ===========================================================

    private void setListeners() {

    }

    private void findViews(View view) {

    }

    public void getData() {
        if (getArguments() != null) {
            mArgumentData = getArguments().getBundle(Constant.Argument.ARGUMENT_DATA);
        }
    }

    private void customizeActionBar() {

    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}