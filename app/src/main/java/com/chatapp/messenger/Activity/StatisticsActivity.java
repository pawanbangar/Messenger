package com.chatapp.messenger.Activity;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.flyco.dialog.entity.DialogMenuItem;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.NormalListDialog;
import com.chatapp.messenger.Adapter.StatAdapter;
import com.chatapp.messenger.R;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class StatisticsActivity extends AppCompatActivity {

    private StatAdapter adapter;
    private ArrayList<DialogMenuItem> dialogMenuItemList;
    private UsageStatsManager usageStatsManager;
    List<UsageStats> stats;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        RecyclerView recyclerView = findViewById(R.id.views_container);
        ImageView calender = findViewById(R.id.calender_analysis);


       usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);


        setupStatistics();

        calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setupStatistics();

                final NormalListDialog listDialog=new NormalListDialog(StatisticsActivity.this,dialogMenuItemList);
                listDialog.title("show Analysis of");
                listDialog.titleBgColor(Color.parseColor("#3599FC"));

                listDialog.show();


                listDialog.setOnOperItemClickL(new OnOperItemClickL() {
                    @Override
                    public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {

                        store_shared_string(dialogMenuItemList.get(position).mOperName);
                        dialogMenuItemList.set(position,new DialogMenuItem(dialogMenuItemList.get(position).mOperName,R.drawable.ic_tick));
                        setupStatistics();
                        adapter.setList(stats);
                        listDialog.cancel();

                    }
                });
            }
        });
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        List<String> list = new ArrayList<>();

        list.add("0");
        list.add("1");

        adapter=new StatAdapter(getApplicationContext(), list,stats);
        recyclerView.setAdapter(adapter);

    }
    private long getStartTime() {
        Calendar calendar = Calendar.getInstance();
        SharedPreferences sharedPref = getSharedPreferences(
                "com.messenger.StatisticsActivity.Analysis_type", Context.MODE_PRIVATE);
        String analysis_type = sharedPref.getString("com.messenger.StatisticsActivity.Analysis_type", "");

        switch (analysis_type) {
            case "Today":
                calendar.add(Calendar.DATE, -1);
                break;
            case "7 day":
                calendar.add(Calendar.WEEK_OF_YEAR, -1);
                break;
            case "Month":
                calendar.add(Calendar.MONTH, -1);
                break;
            default:
                calendar.add(Calendar.DATE, -1);
        }
        return calendar.getTimeInMillis();
    }
    private void store_shared_string(String name){
        SharedPreferences sharedPreferences=getSharedPreferences("com.messenger.StatisticsActivity.Analysis_type",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("com.messenger.StatisticsActivity.Analysis_type", name);
        editor.apply();
    }
    private void setupStatistics(){


        Map<String, UsageStats> usageStats = usageStatsManager.queryAndAggregateUsageStats(getStartTime(), System.currentTimeMillis());
        stats=new ArrayList<>();
        stats.addAll(usageStats.values());

        dialogMenuItemList=new ArrayList<>();
        dialogMenuItemList.add(new DialogMenuItem("Today", 0));
        dialogMenuItemList.add(new DialogMenuItem("7 day", 0));
        dialogMenuItemList.add(new DialogMenuItem("Month", 0));


        SharedPreferences sharedPref = getSharedPreferences(
                "com.messenger.StatisticsActivity.Analysis_type", Context.MODE_PRIVATE);
        String analysis_type = sharedPref.getString("com.messenger.StatisticsActivity.Analysis_type", "");


        switch (analysis_type) {
            case "Today":
                dialogMenuItemList.set(0,new DialogMenuItem(dialogMenuItemList.get(0).mOperName,R.drawable.ic_tick));
                break;
            case "7 day":
                dialogMenuItemList.set(1,new DialogMenuItem(dialogMenuItemList.get(1).mOperName,R.drawable.ic_tick));
                break;
            case "Month":
                dialogMenuItemList.set(2,new DialogMenuItem(dialogMenuItemList.get(2).mOperName,R.drawable.ic_tick));
                break;
                default:
                    dialogMenuItemList.set(0,new DialogMenuItem(dialogMenuItemList.get(0).mOperName,R.drawable.ic_tick));

        }
    }


}
