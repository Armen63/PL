package com.example.armen.pl.ui.activity;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.armen.pl.R;
import com.example.armen.pl.db.cursor.CursorReader;
import com.example.armen.pl.db.entity.Product;
import com.example.armen.pl.db.handler.PlAsyncQueryHandler;
import com.example.armen.pl.io.bus.BusProvider;
import com.example.armen.pl.io.bus.event.ApiEvent;
import com.example.armen.pl.io.service.PLIntentService;
import com.example.armen.pl.util.Constant;
import com.example.armen.pl.util.NetworkUtil;
import com.google.common.eventbus.Subscribe;

public class ProductActivity extends BaseActivity implements
        PlAsyncQueryHandler.AsyncQueryListener {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = ProductActivity.class.getSimpleName();

    // ===========================================================
    // Fields
    // ===========================================================

    private EditText mEtProductTitle;
    private EditText mEtProductPrice;
    private EditText mEtProductDesc;
    private TextView mTvProductTitle;
    private TextView mTvProductPrice;
    private TextView mTvProductDesc;
    private ImageView mIvProductImage;
    private LinearLayout mLlProductView;
    private LinearLayout mLlProductEdit;
    private MenuItem mMenuEdit;
    private MenuItem mMenuDone;
    private MenuItem mMenuUnFavorite;
    private MenuItem mMenuFavorite;
    private Bundle mBundle;
    private Product mProduct = new Product();
    private PlAsyncQueryHandler mPlAsyncQueryHandler;

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            BusProvider.register(this);
            findViews();
            init(savedInstanceState);
            getData();

        } else
            mProduct = savedInstanceState.getParcelable("product");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPlAsyncQueryHandler.getProducts();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.unregister(this);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_product;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_product_item, menu);
        mMenuEdit = menu.findItem(R.id.menu_product_edit);
        mMenuDone = menu.findItem(R.id.menu_product_done);
        mMenuUnFavorite = menu.findItem(R.id.menu_product_unfav);
        mMenuFavorite = menu.findItem(R.id.menu_product_fav);

        if (mProduct.isFavorite()) {
            mMenuFavorite.setVisible(true);
            mMenuUnFavorite.setVisible(false);
        } else {
            mMenuFavorite.setVisible(false);
            mMenuUnFavorite.setVisible(true);
        }

        if (mProduct.isUserProduct()) {
            if (mBundle != null && mBundle.getBoolean("menuDone")) {
                mMenuEdit.setVisible(false);
                mMenuDone.setVisible(true);
                mMenuFavorite.setVisible(false);
                mMenuUnFavorite.setVisible(false);

            } else {
                mMenuEdit.setVisible(true);

            }
        }

        return true;
    }

    // ===========================================================
    // Click Listeners
    // ===========================================================

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;

            case R.id.menu_product_edit:
                mLlProductView.setVisibility(View.GONE);
                mMenuDone.setVisible(true);
                mMenuEdit.setVisible(false);
                mMenuFavorite.setVisible(false);
                mMenuUnFavorite.setVisible(false);
                openEditLayout(mProduct);
                return true;

            case R.id.menu_product_fav:
                mMenuFavorite.setVisible(false);
                mMenuUnFavorite.setVisible(true);
                mProduct.setFavorite(false);
                mPlAsyncQueryHandler.updateProduct(mProduct);
                return true;

            case R.id.menu_product_unfav:
                mMenuFavorite.setVisible(true);
                mMenuUnFavorite.setVisible(false);
                mProduct.setFavorite(true);
                mPlAsyncQueryHandler.updateProduct(mProduct);
                return true;

            case R.id.menu_product_done:
                mMenuUnFavorite.setVisible(true);
                updateProduct(
                        mEtProductTitle.getText().toString(),
                        Integer.parseInt(mEtProductPrice.getText().toString()),
                        mEtProductDesc.getText().toString()
                );
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putParcelable("product", mProduct);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    // ===========================================================
    // Other Listeners, methods for/from Interfaces
    // ===========================================================

    @Subscribe
    public void onEventReceived(ApiEvent<Object> apiEvent) {
        switch (apiEvent.getEventType()) {
            case ApiEvent.EventType.PRODUCT_ITEM_LOADED:
                mProduct = (Product) apiEvent.getEventData();
                openProduct(mBundle);
                openViewLayout(mProduct);
        }
    }


    @Override
    public void onQueryComplete(int token, Object cookie, Cursor cursor) {
        switch (token) {
            case PlAsyncQueryHandler.QueryToken.GET_PRODUCT:
                mProduct = CursorReader.parseProduct(cursor);
                customizeActionBar();
                loadProduct();
                openProduct(mBundle);
                break;
        }
    }

    @Override
    public void onInsertComplete(int token, Object cookie, Uri uri) {

    }

    @Override
    public void onUpdateComplete(int token, Object cookie, int result) {
        switch (token) {
            case PlAsyncQueryHandler.QueryToken.UPDATE_PRODUCT:
                mLlProductEdit.setVisibility(View.GONE);
                mMenuDone.setVisible(false);
                if (!mProduct.isUserProduct()) {
                    mMenuEdit.setVisible(false);
                }
                openViewLayout(mProduct);
                break;
        }
    }

    @Override
    public void onDeleteComplete(int token, Object cookie, int result) {
    }

    // ===========================================================
    // Methods
    // ===========================================================

    private void customizeActionBar() {
        setActionBarTitle(mProduct.getName());
    }

    private void getData() {
        long productId = getIntent().getLongExtra(Constant.Extra.PRODUCT_ID, 0);
        mPlAsyncQueryHandler.getProduct(productId);
    }

    private void findViews() {
        mIvProductImage = (ImageView) findViewById(R.id.iv_product_image);
        mLlProductView = (LinearLayout) findViewById(R.id.ll_product_view);
        mTvProductTitle = (TextView) findViewById(R.id.tv_product_title);
        mTvProductPrice = (TextView) findViewById(R.id.tv_product_price);
        mTvProductDesc = (TextView) findViewById(R.id.tv_product_desc);
        mLlProductEdit = (LinearLayout) findViewById(R.id.ll_product_edit);
        mEtProductTitle = (EditText) findViewById(R.id.et_product_title);
        mEtProductPrice = (EditText) findViewById(R.id.et_product_price);
        mEtProductDesc = (EditText) findViewById(R.id.et_product_desc);
    }

    private void init(Bundle savedInstanceState) {
        mBundle = savedInstanceState;
        mPlAsyncQueryHandler = new PlAsyncQueryHandler(ProductActivity.this, this);
    }

    private void loadProduct() {
        if (NetworkUtil.getInstance().isConnected(this) && mProduct != null) {
            PLIntentService.start(
                    this,
                    Constant.API.PRODUCT_ITEM + String.valueOf(mProduct.getId()) + Constant.API.PRODUCT_ITEM_POSTFIX,
                    Constant.RequestType.PRODUCT_ITEM
            );

        } else {
            openViewLayout(mProduct);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(LOG_TAG, "onSaveInstanceState");

        if (mMenuDone.isVisible()) {
            outState.putBoolean("menuDone", true);
        }

        outState.putParcelable("mProduct", mProduct);
        outState.putString(getString(R.string.saved_description), String.valueOf(mEtProductDesc.getText()));
        outState.putString(getString(R.string.saved_price), String.valueOf(mEtProductPrice.getText()));
        outState.putString(getString(R.string.saved_name),  String.valueOf(mEtProductTitle.getText()));


        super.onSaveInstanceState(outState);

    }

    private void openProduct(Bundle savedInstanceState) {

        if (savedInstanceState != null && savedInstanceState.getBoolean("menuDone")) {
            mProduct = savedInstanceState.getParcelable("mProduct");
            mProduct.setName(savedInstanceState.getString(String.valueOf(R.string.saved_name)));
            mProduct.setDescription(savedInstanceState.getString(String.valueOf(R.string.saved_description)));
            mProduct.setPrice(Long.parseLong(savedInstanceState.getString(String.valueOf(R.string.saved_price))));

            openEditLayout(mProduct);
        } else {
            openViewLayout(mProduct);
        }
    }

    private void updateProduct(String name,
                               long price,
                               String description) {
        mProduct.setName(name);
        mProduct.setPrice(price);
        mProduct.setDescription(description);

        mPlAsyncQueryHandler.updateProduct(mProduct);
    }

    private void openViewLayout(Product product) {
        if (product != null) {
            mLlProductView.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(product.getImage())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mIvProductImage);
            mTvProductTitle.setText(product.getName());
            mTvProductPrice.setText(String.valueOf(product.getPrice()));
            mTvProductDesc.setText(product.getDescription());
        }
    }

    private void openEditLayout(Product product) {
        if (product != null) {
            mLlProductEdit.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(product.getImage())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mIvProductImage);
            mEtProductTitle.setText(product.getName());
            mEtProductPrice.setText(String.valueOf(product.getPrice()));
            mEtProductDesc.setText(product.getDescription());
        }
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}