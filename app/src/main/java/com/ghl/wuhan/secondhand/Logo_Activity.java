package com.ghl.wuhan.secondhand;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

public class Logo_Activity extends AppCompatActivity {
    private Handler myhandler = new Handler();
    private ImageView iv_logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logo_activity);

        iv_logo = (ImageView) findViewById(R.id.iv_logo);
        startAlphaAnimation();
        myhandler.postDelayed(r, 3000);

    }

    public void startAlphaAnimation() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
        alphaAnimation.setDuration(3000);//开始动画
        iv_logo.startAnimation(alphaAnimation);
    }

    Runnable r = new Runnable() {
        @Override
        public void run() {
            Intent intent_logo = new Intent();
            intent_logo.setClass(Logo_Activity.this, MainActivity.class);
            startActivity(intent_logo);
            finish();
        }
    };

    @Override
    protected void onDestroy() {
        if (myhandler != null) {
            myhandler.removeCallbacks(r);
            myhandler = null;
        }
        super.onDestroy();
    }
}