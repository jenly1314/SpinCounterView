package com.king.spincounter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.king.view.spincounterview.SpinCounterView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private SpinCounterView scv;

    private Random mRandom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRandom = new Random();

        scv = (SpinCounterView) findViewById(R.id.scv);
        scv.setFormat(SpinCounterView.RMB_FORMAT);

        scv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scv.showAnimation(random(100));
            }
        });
    }


    private int random(int n){
        return mRandom.nextInt(n);
    }
}
