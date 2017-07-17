package com.example.armen.pl.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.armen.pl.R;
import com.example.armen.pl.db.cursor.CursorReader;
import com.example.armen.pl.db.entity.Product;
import com.example.armen.pl.db.handler.PlAsyncQueryHandler;
import com.example.armen.pl.io.bus.BusProvider;
import com.example.armen.pl.ui.activity.ProductActivity;
import com.example.armen.pl.ui.adapter.ProductAdapter;
import com.example.armen.pl.util.Constant;
import com.google.common.eventbus.Subscribe;

import java.util.ArrayList;

public class FavoriteFragment extends BaseFragment implements View.OnClickListener,
        PlAsyncQueryHandler.AsyncQueryListener, ProductAdapter.OnItemClickListener {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = FavoriteFragment.class.getSimpleName();

    // ===========================================================
    // Fields
    // ===========================================================

    private Bundle mArgumentData;
    private PlAsyncQueryHandler mTlAsyncQueryHandler;
    private RecyclerView mRv;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProductAdapter mRecyclerViewAdapter;
    private LinearLayoutManager mLlm;
    private ArrayList<Product> mProductArrayList;

    // ===========================================================
    // Constructors
    // ===========================================================

    public static FavoriteFragment newInstance() {
        return new FavoriteFragment();
    }

    public static FavoriteFragment newInstance(Bundle args) {
        FavoriteFragment fragment = new FavoriteFragment();
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
        View view = inflater.inflate(R.layout.fragment_favorite_list, container, false);
        BusProvider.register(this);
        findViews(view);
        init();
        setListeners();
        getData();
        customizeActionBar();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mTlAsyncQueryHandler.getAllFavoriteProducts();
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

    @Override
    public void onItemClick(Product product, int position) {
        Intent intent = new Intent(getContext(), ProductActivity.class);
        intent.putExtra(Constant.Extra.EXTRA_PRODUCT_ID, product.getId());
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(final Product product, int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete")
                .setMessage("A y sure?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        product.setFavorite(false);
                        mTlAsyncQueryHandler.updateProduct(product);
                        mProductArrayList.remove(product);
                        mRecyclerViewAdapter.notifyDataSetChanged();

                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialog != null) {
                            dialog.cancel();
                        }
                    }
                })
                .show();
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
            case PlAsyncQueryHandler.QueryToken.GET_FAVORITE_PRODUCTS:
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
        switch (token) {
            case PlAsyncQueryHandler.QueryToken.DELETE_PRODUCT:
                int position = (int) cookie;
                mProductArrayList.remove(position);
                mRecyclerViewAdapter.notifyItemRemoved(position);
                break;
        }

    }

    // ===========================================================
    // Methods
    // ===========================================================
    public void unFavorite(final Product product, final int position) {
    }

    private void setListeners() {
//        mSwipeRefreshLayout.setColorSchemeColors(Color.GREEN, Color.RED, Color.BLUE);
//        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                if (NetworkUtil.getInstance().isConnected(getContext())) {
//                    mSwipeRefreshLayout.setRefreshing(false);
//                    mRecyclerViewAdapter.notifyDataSetChanged();
//                } else {
//                    (new Handler()).postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            mSwipeRefreshLayout.setRefreshing(false);
//                            mRecyclerViewAdapter.notifyDataSetChanged();
//                        }
//                    }, 1000);
//                }
//            }
//        });
    }

    private void findViews(View view) {
        mRv = (RecyclerView) view.findViewById(R.id.rv_fv_list);
//        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.sr_favorite_list);
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

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}