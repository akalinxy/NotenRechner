package com.linxy.gradeorganizer.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.linxy.gradeorganizer.R;
import com.linxy.gradeorganizer.activities.StartupActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShopFragment extends Fragment {


    public static final String TAG = ShopFragment.class.getSimpleName();


    private CardView cvNotPremium;
    private Button buttonBuy;
    private boolean hasPremium = false;

    Activity activity;

    public static ShopFragment getInstance(){
        return new ShopFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_shop, container, false);
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
        } else {
            cvNotPremium.setVisibility(View.VISIBLE);
        }
        return v;
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        this.activity = activity;
    }

    public interface BuyPremiumButtonClick {
         void OnPremiumClick();
    }

}
