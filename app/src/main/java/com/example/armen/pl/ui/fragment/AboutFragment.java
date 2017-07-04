package com.example.armen.pl.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.armen.pl.R;
import com.example.armen.pl.io.sevice.BroadcastService;
import com.example.armen.pl.util.Constant;

public class AboutFragment extends BaseFragment implements View.OnClickListener {
    public static final String mBroadcastStringAction = "com.aca.broadcast.string";

    private TextView mTextView;
    private IntentFilter mIntentFilter;


    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = AboutFragment.class.getSimpleName();

    // ===========================================================
    // Fields
    // ===========================================================

    private Bundle mArgumentData;

    public AboutFragment() {
    }

    // ===========================================================
    // Constructors
    // ===========================================================

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    public static AboutFragment newInstance(Bundle args) {
        AboutFragment fragment = new AboutFragment();
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
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(mBroadcastStringAction);

        Intent serviceIntent = new Intent(getActivity(), BroadcastService.class);
        getContext().startService(serviceIntent);
    }

    @Override
    public void onResume() {
        super.onResume();
        getContext().registerReceiver(mReceiver, mIntentFilter);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mTextView.setText(mTextView.getText() + "Broadcast From Service: \n");
            switch (intent.getAction()) {
                case mBroadcastStringAction:
                    mTextView.setText(mTextView.getText() + intent.getStringExtra("Data") + "\n\n");
                    break;
            }
            Intent stopIntent = new Intent(getActivity(), BroadcastService.class);
            context.stopService(stopIntent);

        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        findViews(view);
        setListeners();
        getData();
        customizeActionBar();
        return view;
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

    // ===========================================================
    // Methods
    // ===========================================================

    private void setListeners() {

    }

    private void findViews(View view) {
        mTextView = (TextView) view.findViewById(R.id.tv_fragment_about_data);
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