package com.linxy.gradeorganizer.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.linxy.gradeorganizer.R;
import com.linxy.gradeorganizer.StartupActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShopFragment extends Fragment {


    public ShopFragment() {
        // Required empty public constructor
    }


    private CardView cvPremium;
    private CardView cvNotPremium;
    private Button buttonBuy;
    private boolean hasPremium = false;

    Activity activity;
    BuyPremiumButtonClick buyPremium;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_shop, container, false);
        cvPremium = (CardView) v.findViewById(R.id.cardview_premiumpurchased);
        cvNotPremium = (CardView) v.findViewById(R.id.cardview_premiumunpurchased);
        buttonBuy = (Button) v.findViewById(R.id.buyPremium);

        buttonBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BuyPremiumButtonClick) activity).OnPremiumClick();
            }
        });


        hasPremium = StartupActivity.PREMIUM;

        if(hasPremium){
            cvNotPremium.setVisibility(View.GONE);
            cvPremium.setVisibility(View.VISIBLE);

        } else {
            cvNotPremium.setVisibility(View.VISIBLE);
            cvPremium.setVisibility(View.GONE);
        }
        return v;
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        this.activity = activity;
    }

    public interface BuyPremiumButtonClick {
        public void OnPremiumClick();
    }

}
