package com.example.administrator.my_map;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Administrator on 2017/3/27 0027.
 */

public class Activity_test extends AppCompatActivity {


    private com.ant.liao.GifView gif1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        gif1 = (com.ant.liao.GifView) findViewById(R.id.gif);
        gif1.setGifImage(R.drawable.yu);
        gif1.setShowDimension(1100,550);
    }
}
