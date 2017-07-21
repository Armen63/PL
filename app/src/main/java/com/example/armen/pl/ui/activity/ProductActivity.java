package com.example.armen.pl.ui.activity;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.example.armen.pl.io.rest.HttpRequestManager;
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
    private MenuItem mMenuUnfav;
    private MenuItem mMenuFav;
    private Product mProduct;
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
        BusProvider.register(this);
        findViews();
        init();
        getData();

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
        mMenuUnfav = menu.findItem(R.id.menu_product_unfav);
        mMenuFav = menu.findItem(R.id.menu_product_fav);

        if (!mProduct.isFavorite()) {
            mMenuFav.setVisible(false);
            mMenuUnfav.setVisible(true);
        } else {
            mMenuFav.setVisible(true);
            mMenuUnfav.setVisible(false);
        }
        if (!mProduct.isUserProduct()){
            mMenuEdit.setVisible(false);
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

            case R.id.menu_product_fav:
                mMenuFav.setVisible(false);
                mMenuUnfav.setVisible(true);
                mProduct.setFavorite(false);
                mPlAsyncQueryHandler.updateProduct(mProduct);
                return true;

            case R.id.menu_product_unfav:
                mMenuFav.setVisible(true);
                mMenuUnfav.setVisible(false);
                mProduct.setFavorite(true);
                mPlAsyncQueryHandler.updateProduct(mProduct);
                return true;

            case R.id.menu_product_edit:
                mLlProductView.setVisibility(View.GONE);
                mMenuDone.setVisible(true);
                mMenuEdit.setVisible(false);
                openEditLayout(mProduct);
                return true;

            case R.id.menu_product_done:
                updateProduct(
                        mEtProductTitle.getText().toString(),
                        Integer.parseInt(mEtProductPrice.getText().toString()),
                        mEtProductDesc.getText().toString()
                );
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    // ===========================================================
    // Other Listeners, methods for/from Interfaces
    // ===========================================================

    @Subscribe
    public void onEventReceived(Product product) {
        mProduct = product;
        openViewLayout(mProduct);
    }


    @Override
    public void onQueryComplete(int token, Object cookie, Cursor cursor) {
        switch (token) {
            case PlAsyncQueryHandler.QueryToken.GET_PRODUCT:
                mProduct = CursorReader.parseProduct(cursor);
                customizeActionBar();
                loadProduct();
                break;
        }
    }

    @Override
    public void onInsertComplete(int token, Object cookie, Uri uri) {
    }

    public void onUpdateComplete(int token, Object cookie, int result) {
        switch (token) {
            case PlAsyncQueryHandler.QueryToken.UPDATE_PRODUCT:
                mLlProductEdit.setVisibility(View.GONE);
                if (!mProduct.isUserProduct()) {
                    mMenuEdit.setVisible(false);
                    mMenuDone.setVisible(false);
                } else {
                    mMenuDone.setVisible(false);
                    mMenuEdit.setVisible(true);
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

    private void getData() {
        long productId = getIntent().getLongExtra(Constant.Extra.EXTRA_PRODUCT_ID, 0);
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

    private void init() {
        mPlAsyncQueryHandler = new PlAsyncQueryHandler(ProductActivity.this, this);
    }

    private void customizeActionBar() {
        setActionBarTitle(mProduct.getName());
    }

    private void loadProduct() {
        if (NetworkUtil.getInstance().isConnected(this) && mProduct.isUserProduct()) {
            mPlAsyncQueryHandler.getProducts();
            PLIntentService.start(
                    this,
                    Constant.API.PRODUCT_ITEM + String.valueOf(mProduct.getId()) + Constant.API.PRODUCT_ITEM_POSTFIX,
                    HttpRequestManager.RequestType.PRODUCT_ITEM
            );

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
        mLlProductView.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(product.getImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mIvProductImage);
        mTvProductTitle.setText(product.getName());
        mTvProductPrice.setText(String.valueOf(product.getPrice()));
        mTvProductDesc.setText(product.getDescription());
    }

    private void openEditLayout(Product product) {
        mLlProductEdit.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(product.getImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mIvProductImage);
        mEtProductTitle.setText(product.getName());
        mEtProductPrice.setText(String.valueOf(product.getPrice()));
        mEtProductDesc.setText(product.getDescription());

    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}