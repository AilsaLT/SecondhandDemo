package com.ghl.wuhan.secondhand.sort_activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ghl.wuhan.secondhand.R;

public class sort_classify_activity extends AppCompatActivity {
    private ImageView iv_back;

    private TextView text_commdity_sort;
    private String Commoditytype;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sort_classify_activity);

        text_commdity_sort = (TextView) findViewById(R.id.tv_1);

        Intent intent = getIntent();
        Commoditytype = intent.getStringExtra("Commoditype");
        if (null != Commoditytype && !Commoditytype.equals("")) {
            text_commdity_sort.setText(Commoditytype);
        }

        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }



}

