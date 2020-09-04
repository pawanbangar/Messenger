
package com.mrserviceman.messenger.Activity;

import android.app.AppOpsManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Process;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.mrserviceman.messenger.Fragments.AccountFragMent;
import com.mrserviceman.messenger.Fragments.AnalyticsFragMent;
import com.mrserviceman.messenger.Fragments.ChatFragMent;
import com.mrserviceman.messenger.Fragments.NewsFragment;
import com.mrserviceman.messenger.Fragments.NotificationFragMent;
import com.mrserviceman.messenger.Utils.Dialogue;
import com.mrserviceman.messenger.ViewPagers.ViewPager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mrserviceman.messenger.R;
import com.mrserviceman.messenger.Adapter.ViewPageAdapter;

import static android.app.AppOpsManager.OPSTR_GET_USAGE_STATS;
import static androidx.core.app.AppOpsManagerCompat.MODE_ALLOWED;

public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    MenuItem prevMenuItem;
    BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.chat:

                    viewPager.setCurrentItem(0);

                    return true;
                case R.id.analytics:

                    viewPager.setCurrentItem(1);

                    return true;
                case R.id.feed:

                    viewPager.setCurrentItem(2);
                    return true;
                case R.id.notification:

                    viewPager.setCurrentItem(3);

                    return true;
                case R.id.account:

                    viewPager.setCurrentItem(4);

                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        viewPager=new ViewPager(getApplicationContext());
        viewPager.canScrollHorizontally(0);
        viewPager=findViewById(R.id.viewpager);
        viewPager.setScrollEnable(false);
        setupViewPager(viewPager);
        viewPager.setNestedScrollingEnabled(false);
        viewPager.setCurrentItem(0);
        final BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                }
                else
                {
                    navigation.getMenu().getItem(0).setChecked(false);
                }
                navigation.getMenu().getItem(position).setChecked(true);
                prevMenuItem = navigation.getMenu().getItem(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if(!checkForPermission(getApplicationContext())){
            Dialogue dialogue=new Dialogue(MainActivity.this);
            dialogue.show(getSupportFragmentManager(),"Permission Required");
        }
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-6605349658509803~8536929057");
        AdView adView =findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);


    }
    private boolean checkForPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(OPSTR_GET_USAGE_STATS, Process.myUid(), context.getPackageName());
        return mode == MODE_ALLOWED;
    }


    @Override
    public void onBackPressed() {
        //  super.onBackPressed();
        Toast.makeText(getBaseContext(),"Heyyy",Toast.LENGTH_SHORT).show();
        getFragmentManager().popBackStackImmediate();
        finish();
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPageAdapter adapter = new ViewPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new ChatFragMent(),"Chats");
        adapter.addFragment(new AnalyticsFragMent(),"Analytics");
        adapter.addFragment(new NewsFragment(),"News");
        adapter.addFragment(new NotificationFragMent(),"Notification");
        adapter.addFragment(new AccountFragMent(),"Account");
        viewPager.setAdapter(adapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
//            if(id == R.id.menu_new_content_twitter){
//         do something
//           }
        return super.onOptionsItemSelected(item);
    }
}
