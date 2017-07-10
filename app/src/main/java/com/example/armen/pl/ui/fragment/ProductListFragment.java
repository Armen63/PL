package com.example.armen.pl.ui.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.armen.pl.R;
import com.example.armen.pl.db.cursor.CursorReader;
import com.example.armen.pl.db.entity.Product;
import com.example.armen.pl.db.handler.PlAsyncQueryHandler;
import com.example.armen.pl.io.bus.BusProvider;
import com.example.armen.pl.io.rest.HttpRequestManager;
import com.example.armen.pl.io.service.PLIntentService;
import com.example.armen.pl.ui.activity.AddProductActivity;
import com.example.armen.pl.ui.adapter.ProductAdapter;
import com.example.armen.pl.util.Constant;
import com.example.armen.pl.util.NetworkUtil;
import com.google.common.eventbus.Subscribe;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class ProductListFragment extends BaseFragment implements View.OnClickListener,
        PlAsyncQueryHandler.AsyncQueryListener, ProductAdapter.OnItemClickListener {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = ProductListFragment.class.getSimpleName();
    private static final int REQUEST_CODE = 1;


    // ===========================================================
    // Fields
    // ===========================================================

    private Bundle mArgumentData;
    private PlAsyncQueryHandler mTlAsyncQueryHandler;
    private RecyclerView mRv;
    private ProductAdapter mRecyclerViewAdapter;
    private LinearLayoutManager mLlm;
    private ArrayList<Product> mProductArrayList;
    private TextView mTvAdd;
    private Product mAddProduct;


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
        init();
        setListeners();
        Log.d("mtav pr", "mtav pr");
//        getData();
        customizeActionBar();


        if (NetworkUtil.getInstance().isConnected(getActivity())) {
            PLIntentService.start(
                    getActivity(),
                    Constant.API.PRODUCT_LIST,
                    HttpRequestManager.RequestType.PRODUCT_LIST
            );
        } else {
            mTlAsyncQueryHandler.getProducts();
        }


        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BusProvider.unregister(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_add, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_add:
                Intent intent = new Intent(getContext(), AddProductActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if(requestCode == REQUEST_CODE && resultCode  == RESULT_OK) {
                Product product = data.getExtras().getParcelable("ADD_PRODUCT");
                mProductArrayList.add(product);
                mRecyclerViewAdapter.notifyDataSetChanged();

            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    @Override
    public void onItemClick(Product product) {
        Log.d(LOG_TAG, product.getName());
    }

    @Override
    public void onItemLongClick(Product product) {
        Log.d(LOG_TAG, product.getName());
    }

    // ===========================================================
    // Other Listeners, methods for/from Interfaces
    // ===========================================================

    @Subscribe
    public void onEventReceived(ArrayList<Product> products) {
        mProductArrayList.clear();
        mProductArrayList.addAll(products);
        mRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onQueryComplete(int token, Object cookie, Cursor cursor) {
        switch (token) {
            case PlAsyncQueryHandler.QueryToken.GET_PRODUCTS:
                ArrayList<Product> products = CursorReader.parseProducts(cursor);
                mProductArrayList.clear();
                mProductArrayList.addAll(products);
                mRecyclerViewAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onInsertComplete(int token, Object cookie, Uri uri) {

    }

    @Override
    public void onUpdateComplete(int token, Object cookie, int result) {

    }

    @Override
    public void onDeleteComplete(int token, Object cookie, int result) {

    }

    // ===========================================================
    // Methods
    // ===========================================================

    private void setListeners() {
    }

    private void findViews(View view) {
        mRv = (RecyclerView) view.findViewById(R.id.rv_product_list);
        mTvAdd = (TextView) view.findViewById(R.id.nav_add);
    }

    private void init() {
        mTlAsyncQueryHandler = new PlAsyncQueryHandler(getActivity().getApplicationContext(), this);

        mRv.setHasFixedSize(true);
        mLlm = new LinearLayoutManager(getActivity());
        mRv.setLayoutManager(mLlm);
        mRv.setItemAnimator(new DefaultItemAnimator());
        mRv.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mProductArrayList = new ArrayList<>();
        mRecyclerViewAdapter = new ProductAdapter(mProductArrayList, this);
        mRv.setAdapter(mRecyclerViewAdapter);
    }

    public void getData() {
        if (getArguments() != null) {
            mArgumentData = getArguments().getBundle(Constant.Argument.ARGUMENT_DATA);
        }
    }

    private void customizeActionBar() {

    }
}