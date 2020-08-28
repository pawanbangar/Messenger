package com.chatapp.messenger.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.messenger.Model.AnalyticsFragMent.Analytics;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.chatapp.messenger.R;

import java.util.List;

public class GridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private Context context;
    private List<Analytics> analyticsList;
    private TextView textView;

    public GridAdapter(Context context, List<Analytics> analyticsList) {
        this.context = context;
        this.analyticsList = analyticsList;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (ViewAdapter.TYPE == 0) {

            View view = LayoutInflater.from(context).inflate(R.layout.launch_app_grid_item, viewGroup, false);
            view.setOnClickListener(this);
            return new ViewHolder(view);

        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.launch_app_list_item, viewGroup, false);
            view.setOnClickListener(this);
            return new ViewHolder1(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


        Analytics content = analyticsList.get(i);
        if(ViewAdapter.TYPE==0){
            ViewHolder viewHolder1=(ViewHolder)viewHolder;
            viewHolder1.appName.setText(content.getAppName());
            String temp = content.getLaunches() + "x";
            viewHolder1.times.setText(temp);
            temp = (content.getMin()) + " Minutes " + (content.getSec()) + " sec";
            viewHolder1.app_used.setText(temp);
            viewHolder1.app_logo.setImageDrawable(content.getImage());
        }
        else{
            final ViewHolder1 viewHolder1=(ViewHolder1)viewHolder;
            viewHolder1.appName.setText(content.getAppName());
            String temp = content.getLaunches() + "x";
            viewHolder1.times.setText(temp);
            temp = (content.getMin()) + " Minutes " + (content.getSec()) + " sec";
            viewHolder1.app_used.setText(temp);
            viewHolder1.app_logo.setImageDrawable(content.getImage());
            viewHolder1.progressBar.setProgress(analyticsList.get(i).getLaunches());
            int times=0;
            for(Analytics analytics:analyticsList)
            {
                times=times+analytics.getLaunches();
            }
            for(Analytics analytics:analyticsList)
            {
                switch (analytics.getAppName()){

                    case "Youtube":
                        viewHolder1.progressBar.setColor(Color.parseColor("#ff0000"));

                        viewHolder1.progressBar.setProgressWithAnimation(calculatePercent(analyticsList.get(i).getLaunches(),times));
                        viewHolder1.progressBar.setOnProgressChangedListener(new CircularProgressBar.ProgressChangeListener() {
                            @Override
                            public void onProgressChanged(float progress) {
                                viewHolder1.progress.setText(String.valueOf((int)progress));
                            }
                        });
                        break;

                    case "WhatsApp":
                        viewHolder1.progressBar.setColor(Color.parseColor("#05F29B"));
                        viewHolder1.progressBar.setProgressWithAnimation(calculatePercent(analyticsList.get(i).getLaunches(),times));
                        viewHolder1.progressBar.setOnProgressChangedListener(new CircularProgressBar.ProgressChangeListener() {
                            @Override
                            public void onProgressChanged(float progress) {
                                viewHolder1.progress.setText(String.valueOf((int)progress));
                            }
                        });
                        break;

                    case "Duo":
                        viewHolder1.progressBar.setColor(Color.parseColor("#0D5EDE"));
                        viewHolder1.progressBar.setProgressWithAnimation(calculatePercent(analyticsList.get(i).getLaunches(),times));
                        viewHolder1.progressBar.setOnProgressChangedListener(new CircularProgressBar.ProgressChangeListener() {
                            @Override
                            public void onProgressChanged(float progress) {
                                viewHolder1.progress.setText(String.valueOf((int)progress));
                            }
                        });
                        break;

                    case "Hangouts":
                        viewHolder1.progressBar.setColor(Color.parseColor("#448C30"));
                        viewHolder1.progressBar.setProgressWithAnimation(calculatePercent(analyticsList.get(i).getLaunches(),times));
                        viewHolder1.progressBar.setOnProgressChangedListener(new CircularProgressBar.ProgressChangeListener() {
                            @Override
                            public void onProgressChanged(float progress) {
                                viewHolder1.progress.setText(String.valueOf((int)progress));
                            }
                        });
                        break;

                    case "Gmail":
                        viewHolder1.progressBar.setColor(Color.parseColor("#F24405"));
                        viewHolder1.progressBar.setProgressWithAnimation(calculatePercent(analyticsList.get(i).getLaunches(),times));
                        viewHolder1.progressBar.setOnProgressChangedListener(new CircularProgressBar.ProgressChangeListener() {
                            @Override
                            public void onProgressChanged(float progress) {
                                viewHolder1.progress.setText(String.valueOf((int)progress));
                            }
                        });
                        break;
                    case "Facebook":
                        viewHolder1.progressBar.setColor(Color.parseColor("#3DADF2"));
                        viewHolder1.progressBar.setProgressWithAnimation(calculatePercent(analyticsList.get(i).getLaunches(),times));
                        viewHolder1.progressBar.setOnProgressChangedListener(new CircularProgressBar.ProgressChangeListener() {
                            @Override
                            public void onProgressChanged(float progress) {
                                viewHolder1.progress.setText(String.valueOf((int)progress));
                            }
                        });
                        break;
                }
            }

        }

    }

    @Override
    public int getItemCount() {
        return analyticsList.size();
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        textView = view.findViewById(R.id.appName);
        switch (textView.getText().toString()) {
            case "Youtube":

                intent = context.getPackageManager().getLaunchIntentForPackage("com.google.android.youtube");
                context.startActivity(intent);
                store_sharedPref("com.uptodown.messenger.Youtube_launches", 0);
                break;
            case "WhatsApp":
                try {

                    intent = context.getPackageManager().getLaunchIntentForPackage("com.whatsapp");
                    context.startActivity(intent);
                    store_sharedPref("com.uptodown.messenger.Whatsapp_launches", 1);
                } catch (Exception ex) {

                }
                break;
            case "Duo":
                try {

                    intent = context.getPackageManager().getLaunchIntentForPackage("com.google.android.apps.tachyon");
                    context.startActivity(intent);
                    store_sharedPref("com.uptodown.messenger.Duo_launches", 2);
                } catch (Exception ex) {

                }
                break;
            case "Hangouts":
                try {

                    intent = context.getPackageManager().getLaunchIntentForPackage("com.google.android.talk");
                    context.startActivity(intent);
                    store_sharedPref("com.uptodown.messenger.Hangouts_launches", 3);
                } catch (Exception ex) {

                }
                break;
            case "Gmail":
                try {

                    intent = context.getPackageManager().getLaunchIntentForPackage("com.google.android.gm");
                    context.startActivity(intent);
                    store_sharedPref("com.uptodown.messenger.Gmail_launches", 4);
                } catch (Exception ex) {

                }
                break;
            case "Facebook":
                try {

                    intent = context.getPackageManager().getLaunchIntentForPackage("com.facebook.android");
                    context.startActivity(intent);
                    store_sharedPref("com.uptodown.messenger.Facebook_launches", 5);
                } catch (Exception ex) {

                }


        }
        notifyDataSetChanged();


    }

    private void store_sharedPref(String name, int appnumber) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(name, sharedPreferences.getInt(name, 0) + 1);
        Analytics analytics = analyticsList.get(appnumber);
        analytics.setLaunches(analytics.getLaunches() + 1);
        analyticsList.set(appnumber, analytics);
        editor.apply();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView app_logo;
        private TextView appName;
        private TextView times;
        private TextView app_used;
        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            appName = itemView.findViewById(R.id.appName);
            app_logo = itemView.findViewById(R.id.logo);
            times = itemView.findViewById(R.id.launches);
            app_used = itemView.findViewById(R.id.time_used);
        }
    }
    public class ViewHolder1 extends RecyclerView.ViewHolder {
        private ImageView app_logo;
        private TextView appName;
        private TextView times;
        private TextView app_used;
        private CircularProgressBar progressBar;
        private TextView progress;
        public ViewHolder1(@NonNull View itemView) {
            super(itemView);
            appName = itemView.findViewById(R.id.appName);
            app_logo = itemView.findViewById(R.id.logo);
            times = itemView.findViewById(R.id.launches);
            app_used = itemView.findViewById(R.id.time_used);
            progressBar=itemView.findViewById(R.id.progress_bar);
            progress=itemView.findViewById(R.id.progress);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
    private int calculatePercent(long item,long time){
        int result=0;
        try{
            result= (int)((item*100)/time);
        }
        catch (ArithmeticException ex){
            ex.printStackTrace();
        }
        return result;

    }
}

