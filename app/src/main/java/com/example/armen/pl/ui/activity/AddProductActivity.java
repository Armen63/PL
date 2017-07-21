package com.example.armen.pl.ui.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.armen.pl.R;
import com.example.armen.pl.db.entity.Product;
import com.example.armen.pl.db.handler.PlAsyncQueryHandler;
import com.example.armen.pl.util.AppUtil;
import com.example.armen.pl.util.Constant;

public class AddProductActivity extends BaseActivity implements View.OnClickListener, PlAsyncQueryHandler.AsyncQueryListener {

    private ImageView mImage;
    private EditText mEtName;
    private EditText mEtPrice;
    private EditText mEtDescription;
    private Button mBtnAdd;
    private Product mProduct;


    private static final String LOG_TAG = AddProductActivity.class.getSimpleName();
    public static final String ADD_PRODUCT = "ADD_PRODUCT";
    private static final String EMPTY = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViews();
        setListeners();
        customizeActionBar();

    }

    private void customizeActionBar() {
        setActionBarTitle("Add Product");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void findViews() {
        mImage = (ImageView) findViewById(R.id.iv_add_product);
        Glide.with(this).load(Constant.API.BANAN)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mImage);
        mEtName = (EditText) findViewById(R.id.et_add_product_name);
        mEtPrice = (EditText) findViewById(R.id.et_add_product_price);
        mEtDescription = (EditText) findViewById(R.id.et_add_product_description);
        mBtnAdd = (Button) findViewById(R.id.btn_product_add_item);
    }

    private void setListeners() {
        mBtnAdd.setOnClickListener(this);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_add;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_product_add_item:
                if (!isValid()) {
                    Toast.makeText(this, "null or empty field(s)", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendData();
        }
    }

    private void sendData() {

        Intent data = new Intent();
        Long price = Long.valueOf(mEtPrice.getText().toString());
        data.putExtra(ADD_PRODUCT,
                new Product(System.currentTimeMillis(),
                        mEtName.getText().toString(),
                        price,
                        Constant.API.BANAN,
                        mEtDescription.getText().toString(),
                        false,
                        true)
        );
        /////

        mProduct = new Product();
        mProduct.setId(System.currentTimeMillis());
        mProduct.setImage(Constant.API.BANAN);
        mProduct.setName(mEtName.getText().toString());
        mProduct.setPrice(price);
        mProduct.setDescription(mEtDescription.getText().toString());
        mProduct.setUserProduct(true);
        mProduct.setDescription(mEtDescription.getText().toString());
        PlAsyncQueryHandler handler = new PlAsyncQueryHandler(getApplicationContext(), this);
//        PlQueryHandler.addProduct(this,mProduct);
        handler.addProduct(mProduct);

        setResult(RESULT_OK, data);
        AppUtil.sendNotification(
                getBaseContext(),
                "Added product","Product with name '"  + mProduct.getName()+"' has added",
                "{1}");
        finish();
    }

    @Override
    public void onQueryComplete(int token, Object cookie, Cursor cursor) {

    }

    @Override
    public void onInsertComplete(int token, Object cookie, Uri uri) {
        switch (token) {
            case PlAsyncQueryHandler.QueryToken.ADD_PRODUCT:
                Intent result = new Intent();
                result.putExtra(ADD_PRODUCT, mProduct);
                setResult(RESULT_OK, result);
                finish();
                break;
        }
    }

    @Override
    public void onUpdateComplete(int token, Object cookie, int result) {

    }

    @Override
    public void onDeleteComplete(int token, Object cookie, int result) {

    }

    private boolean isValid() {

        if (mEtName.getText().toString() == null || mEtName.getText().toString().equals(EMPTY)) {
            return false;
        }

        if (mEtPrice.getText().toString() == null || mEtPrice.getText().toString().equals(EMPTY)) {
            return false;
        }

        if (mEtDescription.getText().toString() == null || mEtDescription.getText().toString().equals(EMPTY)) {
            return false;
        }

        if (Long.parseLong(mEtPrice.getText().toString()) >= Integer.MAX_VALUE) {
            Toast.makeText(this, "your price > MAX PRICE", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
