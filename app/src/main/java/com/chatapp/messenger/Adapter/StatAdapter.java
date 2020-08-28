package com.chatapp.messenger.Adapter;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.davidmiguel.multistateswitch.MultiStateSwitch;
import com.davidmiguel.multistateswitch.State;
import com.davidmiguel.multistateswitch.StateListener;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.chatapp.messenger.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class StatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements StateListener {
    private Context mContext;
    private List<String> list;
    private List<UsageStats> usageStatsList;
    public long times;
    public StatAdapter(Context context, List<String> list,List<UsageStats> usageStats) {
            this.list=list;
            this.mContext=context;
            usageStatsList=usageStats;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if(viewType==1){
                View view= LayoutInflater.from(mContext).inflate(R.layout.pie_chart,parent,false);
                return new StatAdapter.ViewHolder(view);
            }
            else{
                View view= LayoutInflater.from(mContext).inflate(R.layout.stat_top_element,parent,false);
                return new StatAdapter.ViewHolder1(view);
            }


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(list.get(position).equals("1")){
            ViewHolder viewHolder=(ViewHolder)holder;
            viewHolder.pieChart.setCenterText("Statistics");

            long youtube=0,whatsapp=0,Duo=0,hangout=0,gmail=0,facebook=0;
            times=0;
            for(UsageStats usageStats1:usageStatsList) {
                switch (usageStats1.getPackageName()) {

                    case "com.google.android.youtube":
                        youtube = usageStats1.getTotalTimeInForeground();
                        youtube = youtube / 1000;
                        times=times+youtube;
                        break;
                    case "com.whatsapp":
                        whatsapp = usageStats1.getTotalTimeInForeground();
                        whatsapp = whatsapp / 1000;
                        times=times+whatsapp;
                        break;
                    case "com.google.android.apps.tachyon":
                        Duo = usageStats1.getTotalTimeInForeground();
                        Duo = Duo / 1000;
                        times=times+Duo;
                        break;
                    case "com.google.android.talk":
                        hangout = usageStats1.getTotalTimeInForeground();
                        hangout = hangout / 1000;
                        times=times+hangout;
                        break;
                    case "com.google.android.gm":
                        gmail = usageStats1.getTotalTimeInForeground();
                        gmail = gmail / 1000;
                        times=times+gmail;
                        break;
                }
            }

            store_shared_int(times);

            List<PieEntry> entries = new ArrayList<>();
            if(calculatePercent(youtube,times)!=0){
                entries.add(new PieEntry(calculatePercent(youtube,times), "Youtube"));
            }
            if(calculatePercent(whatsapp,times)!=0){
                entries.add(new PieEntry(calculatePercent(whatsapp,times),"Whatsapp"));
            }
            if(calculatePercent(Duo,times)!=0){
                entries.add(new PieEntry(calculatePercent(Duo,times),"Duo"));
            }
            if(calculatePercent(gmail,times)!=0){
                entries.add(new PieEntry(calculatePercent(gmail,times),"Gmail"));
            }
            if(calculatePercent(hangout,times)!=0){
                entries.add(new PieEntry(calculatePercent(hangout,times),"Hangout"));
            }
            PieDataSet set = new PieDataSet(entries,"By time");
            set.setColors(new int[] { R.color.col1, R.color.col2, R.color.col3, R.color.col4 ,R.color.col5}, mContext);
            viewHolder.pieChart.setBackgroundColor(Color.parseColor("#ffffff"));
            PieData data = new PieData(set);
            viewHolder.pieChart.setData(data);
            viewHolder.pieChart.invalidate();
            viewHolder.pieChart.setDescription(null);
            viewHolder.multiStateSwitch.addStatesFromStrings(Arrays.asList("use times", "use duration"));
            viewHolder.multiStateSwitch.setBackgroundColor(Color.parseColor("#ffffff"));
            viewHolder.card_piechart.setBackgroundResource(R.drawable.pure_white_rounded);
            viewHolder.multiStateSwitch.addStateListener(this);
        }
        else{
            ViewHolder1 holder1=(ViewHolder1)holder;
            holder1.dur_stat.setBackgroundResource(R.drawable.pure_white_rounded);
            holder1.durat.setBackgroundResource(R.drawable.pure_white_rounded);
            String temp=ViewAdapter.times+" times";
            holder1.opened_times.setText(temp);
            holder1.days.setText("0 days");
            calculateTotalTime();
            temp=(times/60)+" minute";
            holder1.app_use_duration.setText(temp);

        }


    }
    private void store_shared_int(long time){
        SharedPreferences sharedPreferences=mContext.getSharedPreferences("com.messenger.StatisticsActivity.Analysis_time_used",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("com.messenger.StatisticsActivity.Analysis_time_used", time);
        editor.apply();
    }
    private int calculatePercent(long item,long time){
        int result=0;
        try{
            result= (int)((item*100)/time);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return result;

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
       if(list.get(position).equals("0")){
           return 0;
       }
       else
       {
           return 1;
       }
    }
    private long getStartTime() {
        Calendar calendar = Calendar.getInstance();
        SharedPreferences sharedPref = mContext.getSharedPreferences(
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
    public void calculateTotalTime(){

        UsageStatsManager usageStatsManager = (UsageStatsManager) mContext.getSystemService(Context.USAGE_STATS_SERVICE);

        Map<String, UsageStats> usageStats = usageStatsManager.queryAndAggregateUsageStats(getStartTime(), System.currentTimeMillis());
        usageStatsList =new ArrayList<>();
        usageStatsList.addAll(usageStats.values());

        long youtube=0,whatsapp=0,Duo=0,hangout=0,gmail=0,facebook=0;
        times=0;
        for(UsageStats usageStats1:usageStatsList) {
            switch (usageStats1.getPackageName()) {

                case "com.google.android.youtube":
                    youtube = usageStats1.getTotalTimeInForeground();
                    youtube = youtube / 1000;
                    times=times+youtube;
                    break;
                case "com.whatsapp":
                    whatsapp = usageStats1.getTotalTimeInForeground();
                    whatsapp = whatsapp / 1000;
                    times=times+whatsapp;
                    break;
                case "com.google.android.apps.tachyon":
                    Duo = usageStats1.getTotalTimeInForeground();
                    Duo = Duo / 1000;
                    times=times+Duo;
                    break;
                case "com.google.android.talk":
                    hangout = usageStats1.getTotalTimeInForeground();
                    hangout = hangout / 1000;
                    times=times+hangout;
                    break;
                case "com.google.android.gm":
                    gmail = usageStats1.getTotalTimeInForeground();
                    gmail = gmail / 1000;
                    times=times+gmail;
                    break;
            }
        }
    }
    @Override
    public void onStateSelected(int stateIndex, @NonNull State state) {
        int youtube=0,whatsapp=0,duo=0,hangout=0,gmail=0,facebook=0;
        if(stateIndex==0){
            youtube=handle_sharedPref("com.uptodown.messenger.Youtube_launches");
            whatsapp=handle_sharedPref("com.uptodown.messenger.Whatsapp_launches");
            duo=handle_sharedPref("com.uptodown.messenger.Duo_launches");
            hangout=handle_sharedPref("com.uptodown.messenger.Hangouts_launches");
            gmail=handle_sharedPref("com.uptodown.messenger.Gmail_launches");
            facebook=handle_sharedPref("com.uptodown.messenger.Facebook_launches");
        }

    }
    private int handle_sharedPref(String name){
        SharedPreferences sharedPref = mContext.getSharedPreferences(
                name, Context.MODE_PRIVATE);
        return sharedPref.getInt(name, 0);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private PieChart pieChart;
        private MultiStateSwitch multiStateSwitch;
        private CardView card_piechart;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            multiStateSwitch=itemView.findViewById(R.id.multistate);
            pieChart=itemView.findViewById(R.id.progress_circular);
            card_piechart=itemView.findViewById(R.id.card_piechart);
        }
    }
    public class ViewHolder1 extends RecyclerView.ViewHolder{
            private CardView dur_stat;
            private CardView durat;
        private TextView days;
        private TextView opened_times;
        private TextView app_use_duration;
        public ViewHolder1(@NonNull View itemView) {
            super(itemView);
            dur_stat=itemView.findViewById(R.id.apps_stat);
            durat=itemView.findViewById(R.id.durat);
            days=itemView.findViewById(R.id.days);
            opened_times=itemView.findViewById(R.id.times);
            app_use_duration=itemView.findViewById(R.id.duration);
        }
    }
    public void setList(List<UsageStats> list_usage){
        usageStatsList=list_usage;
        notifyDataSetChanged();
    }
}
