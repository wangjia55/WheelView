package com.jacob.wheelview;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

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
        wheelView.setItems(mListItem);

    }

}
