package com.chatapp.messenger.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.chatapp.messenger.Fragments.NewsExtendedFragment;
import com.chatapp.messenger.ViewPagers.ViewPager;
import com.chatapp.messenger.Adapter.NewsMainAdapter;
import com.chatapp.messenger.Adapter.ViewPageAdapter;
import com.chatapp.messenger.R;
import com.chatapp.messenger.Utils.CustPagerTransformer;


public class NewsFragmentActivity extends AppCompatActivity {

    private ViewPager viewPager;
    public static int ITEM_NUMBER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_fragment);
        viewPager=findViewById(R.id.news_viewpager);
        viewPager.setPageTransformer(false, new CustPagerTransformer(this));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                ITEM_NUMBER=position;
            }

            @Override
            public void onPageSelected(int position) {
                ITEM_NUMBER=position;

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setupViewPager(viewPager, NewsMainAdapter.aricles.size());

    }
    private void setupViewPager(ViewPager viewPager,int size) {
        ViewPageAdapter adapter = new ViewPageAdapter(getSupportFragmentManager());
        for(int i=0;i<size;i++){
            adapter.addFragment(new NewsExtendedFragment(i),"fragment"+i);
        }

        viewPager.setAdapter(adapter);
        ITEM_NUMBER=getIntent().getIntExtra("number",0);
        viewPager.setCurrentItem(getIntent().getIntExtra("number",0),true);
    }

}
