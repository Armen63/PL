package com.example.armen.pl.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.armen.pl.R;
import com.example.armen.pl.db.cursor.CursorReader;
import com.example.armen.pl.db.entity.Product;
import com.example.armen.pl.db.handler.PlAsyncQueryHandler;
import com.example.armen.pl.io.bus.BusProvider;
import com.example.armen.pl.io.bus.event.ApiEvent;
import com.example.armen.pl.io.service.PLIntentService;
import com.example.armen.pl.ui.activity.AddProductActivity;
import com.example.armen.pl.ui.activity.ProductActivity;
import com.example.armen.pl.ui.adapter.ProductAdapter;
import com.example.armen.pl.util.Constant;
import com.example.armen.pl.util.NetworkUtil;
import com.google.common.eventbus.Subscribe;

import java.util.ArrayList;

import static android.R.attr.id;
import static android.app.Activity.RESULT_OK;
import static com.example.armen.pl.ui.activity.AddProductActivity.ADD_PRODUCT;


public class ProductListFragment extends BaseFragment implements View.OnClickListener,
        PlAsyncQueryHandler.AsyncQueryListener, ProductAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = ProductListFragment.class.getSimpleName();
    private static final int REQUEST_CODE = 100;


    // ===========================================================
    // Fields
    // ===========================================================

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private PlAsyncQueryHandler mTlAsyncQueryHandler;
    private RecyclerView mRv;
    private ProductAdapter mRecyclerViewAdapter;
    private LinearLayoutManager mLlm;
    private ArrayList<Product> mProductList;

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
        loadData();
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
                break;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
                Product product = data.getParcelableExtra(ADD_PRODUCT);
                mProductList.add(product);
                mTlAsyncQueryHandler.getProduct(id);
                mRecyclerViewAdapter.notifyDataSetChanged();

            }
        }
    }

    @Override
    public void onItemClick(Product product, int position) {
        Intent intent = new Intent(getContext(), ProductActivity.class);
        intent.putExtra(Constant.Extra.PRODUCT_ID, product.getId());
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(Product product, int position) {
        openDeleteProductDialog(product, position);
    }

    public void openDeleteProductDialog(final Product product, final int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete")
                .setMessage("A y sure?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mTlAsyncQueryHandler.deleteProduct(product, position);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                })
                .show();
    }

    // ===========================================================
    // Other Listeners, methods for/from Interfaces
    // ===========================================================

    @Subscribe
    public void onEventReceived(ApiEvent<Object> apiEvent) {
        switch (apiEvent.getEventType()) {
            case ApiEvent.EventType.PRODUCT_LIST_LOADED:
                if (apiEvent.isSuccess()) {
                    mProductList.clear();
                    mProductList.addAll((ArrayList<Product>) apiEvent.getEventData());
                    mRecyclerViewAdapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(getActivity(), "Something went wrong, please try again",
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onQueryComplete(int token, Object cookie, Cursor cursor) {
        switch (token) {
            case PlAsyncQueryHandler.QueryToken.GET_PRODUCTS:
                ArrayList<Product> products = CursorReader.parseProducts(cursor);
                mProductList.clear();
                mProductList.addAll(products);
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
                mProductList.remove(position);
                mRecyclerViewAdapter.notifyItemRemoved(position);
                break;
        }

    }

    // ===========================================================
    // Methods
    // ===========================================================

    private void loadData() {
        if (mProductList.size() == 0) {
            if (NetworkUtil.getInstance().isConnected(getActivity())) {
                PLIntentService.start(
                        getActivity(),
                        Constant.API.PRODUCT_LIST,
                        Constant.RequestType.PRODUCT_LIST
                );
            } else {
                mTlAsyncQueryHandler.getProducts();
            }
        } else {
            mTlAsyncQueryHandler.getProducts();
            mRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

    private void setListeners() {
        mSwipeRefreshLayout.setColorSchemeColors(Color.GREEN, Color.RED, Color.BLUE);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void findViews(View view) {
        mRv = (RecyclerView) view.findViewById(R.id.rv_product_list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.sr_product_list);
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    private void init() {
        mTlAsyncQueryHandler = new PlAsyncQueryHandler(getActivity().getApplicationContext(), this);

        mRv.setHasFixedSize(true);
        mLlm = new LinearLayoutManager(getActivity());
        mRv.setLayoutManager(mLlm);
        mRv.setItemAnimator(new DefaultItemAnimator());
        mRv.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mProductList = new ArrayList<>();
        mRecyclerViewAdapter = new ProductAdapter(mProductList, this);
        mRv.setAdapter(mRecyclerViewAdapter);
    }

    @Override
    public void onRefresh() {
        if (NetworkUtil.getInstance().isConnected(getContext())) {
            PLIntentService.start(
                    getActivity(),
                    Constant.API.PRODUCT_LIST,
                    Constant.RequestType.PRODUCT_LIST
            );
            mSwipeRefreshLayout.setRefreshing(false);

        } else {
            mProductList.clear();
            mRecyclerViewAdapter.notifyDataSetChanged();
            Toast.makeText(getContext(), R.string.msg_network_connection_error, Toast.LENGTH_LONG).show();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void fetchData() {
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}