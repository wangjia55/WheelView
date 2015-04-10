package com.jacob.wheelview;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import java.util.Arrays;
import java.util.List;


public class MainActivity extends FragmentActivity {

    private List<String> mListItem = Arrays.asList("China","American","Japan","England","French","Italy");

    private WheelView wheelView ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wheelView = (WheelView) findViewById(R.id.wheelview);
        wheelView.setOffset(2);
        wheelView.setItems(mListItem);
        wheelView.setSelection(3);
        wheelView.setOnWheelPickerListener(new WheelView.OnWheelPickerListener() {
            @Override
            public void wheelSelect(int position, String content) {
                Log.e("TAG",position+"+"+content);
            }
        });

    }

}
