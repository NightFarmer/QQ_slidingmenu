package com.zhangfan.qq_slidingmenu;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.zhangfan.qqslidingmenu.SlidingMenu;
import com.zhangfan.qqslidingmenu.SlidingContentViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.slidingMenu)
    SlidingMenu slidingMenu;

    @Bind(R.id.viewPager)
    SlidingContentViewPager viewPager;

    @Bind(R.id.layout_main_content)
    ViewGroup layout_main_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        final View viewById = findViewById(R.id.button);
        slidingMenu.setOnHorizontalScrollListener(new SlidingMenu.OnHorizontalScrollListener() {
            @Override
            public void onScroll(float scale) {
                ViewHelper.setAlpha(viewById, scale);
            }
        });

        viewPager.setParent(slidingMenu);
        final List<Fragment> fragments = new ArrayList<>();
        fragments.add(new InfoFragment());
        fragments.add(new InfoFragment());
        fragments.add(new InfoFragment());

        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        });
    }



    public void toggleMenu(View view){
        slidingMenu.toggle();
    }
}
