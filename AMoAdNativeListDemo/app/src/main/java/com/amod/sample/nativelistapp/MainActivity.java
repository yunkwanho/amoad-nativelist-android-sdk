package com.amod.sample.nativelistapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    // TODO [SDK] 管理画面から取得したsidを入力してください
    private static final String SID1 = "62056d310111552c000000000000000000000000000000000000000000000000";
    private static final String TAG1 = "Ad01";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
