package com.ghl.wuhan.secondhand.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ghl.wuhan.secondhand.R;
import com.ghl.wuhan.secondhand.find_activity.find_buy_activity;
import com.ghl.wuhan.secondhand.find_activity.find_sale_activity;


public class find_fragment extends Fragment {
    private TextView tv_tan,tv_qiu;
    private ImageView image_ti;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.find_fragment,container,false);
        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        tv_tan=(TextView)getActivity().findViewById(R.id.tv_tan);
        tv_qiu=(TextView) getActivity().findViewById(R.id.tv_qiu);
        image_ti=(ImageView)getActivity().findViewById(R.id.image_ti);
        //设置默认状态白底蓝字
        tv_tan.setEnabled(false);
        tv_tan.setTextColor(Color.parseColor("#0895e7"));

        tv_tan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_tan.setEnabled(false);
                tv_tan.setTextColor(Color.parseColor("#0895e7"));
                tv_qiu.setEnabled(true);
                tv_qiu.setTextColor(Color.parseColor("#ffffff"));
                image_ti.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent =new Intent(getActivity(),find_sale_activity.class);
                        startActivity(intent);
                    }
                });

            }
        });

        image_ti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getActivity(),find_sale_activity.class);
                startActivity(intent);
            }
        });

        tv_qiu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_tan.setEnabled(true);
                tv_tan.setTextColor(Color.parseColor("#ffffff"));
                tv_qiu.setEnabled(false);
                tv_qiu.setTextColor(Color.parseColor("#0895e7"));

                image_ti.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(),find_buy_activity.class);
                        startActivity(intent);
                    }
                });
            }
        });


    }
}
