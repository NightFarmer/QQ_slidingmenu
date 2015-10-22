package com.zhangfan.qq_slidingmenu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.zhangfan.qqslidingmenu.SlidingMenu;

public class MainActivity extends AppCompatActivity {

    private SlidingMenu slidingMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final View viewById = findViewById(R.id.button);
        slidingMenu = (SlidingMenu) findViewById(R.id.slidingMenu);
        slidingMenu.setOnHorizontalScrollListener(new SlidingMenu.OnHorizontalScrollListener() {
            @Override
            public void onScroll(float scale) {
                ViewHelper.setAlpha(viewById, scale);
            }
        });
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return 100;
            }

            @Override
            public Object getItem(int position) {
                return position;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final TextView textView = new TextView(MainActivity.this);
                textView.setText("xxxxx");
                return textView;
            }
        });
    }



    public void toggleMenu(View view){
        slidingMenu.toggle();
    }
}
