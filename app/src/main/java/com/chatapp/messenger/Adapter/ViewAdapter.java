package com.chatapp.messenger.Adapter;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.chatapp.messenger.Model.AnalyticsFragMent.Analytics;
import com.chatapp.messenger.api.ApiClient;
import com.chatapp.messenger.api.ApiInterface;
import com.flyco.dialog.entity.DialogMenuItem;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.NormalListDialog;
import com.squareup.picasso.Picasso;
import com.chatapp.messenger.Activity.ChatActivity;
import com.chatapp.messenger.Model.AnalyticsFragMent.SocialApps;
import com.chatapp.messenger.Model.AnalyticsFragMent.ViewNumber;
import com.chatapp.messenger.Model.News.Article;
import com.chatapp.messenger.Model.News.News;
import com.chatapp.messenger.R;
import com.chatapp.messenger.Utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<ViewNumber> analysisList;
    private Context context;
    private ViewPager viewPager;
    private List<Analytics> analyticsList;
    public static  final String API_KEY="75203a0b33ff4ab9aa75630f6e139d74";
    public static int TYPE;
    private List<Article> articles;
    private String TAG= ChatActivity.class.getSimpleName();
    public static int times;
    ArrayList<DialogMenuItem> list;
    List<UsageStats> stats;
    public TextView launches;

    public ViewAdapter(List<ViewNumber> analysisList, Context context, TextView launches) {
        this.analysisList = analysisList;
        this.context = context;
        this.launches = launches;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;

       switch(i)
       {
           case 0:
               view=LayoutInflater.from(context).inflate(R.layout.analytics_frame,viewGroup,false);
               return new ViewHolder0(view);

           case 1:
               view= LayoutInflater.from(context).inflate(R.layout.weekly_analysis,viewGroup,false);
               return new ViewAdapter.ViewHolder1(view);

           case 2:
               view=LayoutInflater.from(context).inflate(R.layout.social_and_explore,viewGroup,false);
               return new ViewAdapter.ViewHolder2(view);

       }
        return null;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        switch (i){
            case 0:
                ViewHolder0 viewHolder0=(ViewHolder0)viewHolder;
                setupViewHolder0(viewHolder0);
                break;
            case 1:
                ViewHolder1 viewHolder1=(ViewHolder1)viewHolder;
                setupViewHolder1(viewHolder1);
                break;
            case 2:
                ViewHolder2 viewHolder2=(ViewHolder2)viewHolder;
                setupViewHolder2(viewHolder2);
        }

    }
    private void store_shared_string(String name){
        SharedPreferences sharedPreferences=context.getSharedPreferences("com.messenger.Analysis_type",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("com.messenger.Analysis_type", name);
        editor.apply();
    }
    private long getStartTime() {
        Calendar calendar = Calendar.getInstance();
        SharedPreferences sharedPref = context.getSharedPreferences(
                "com.messenger.Analysis_type", Context.MODE_PRIVATE);
        String analysis_type = sharedPref.getString("com.messenger.Analysis_type", "");

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
                calendar.add(Calendar.MONTH, -1);
        }
        return calendar.getTimeInMillis();
    }
    private void setupViewHolder1(final ViewHolder1 viewHolder1) {

        viewHolder1.recyclerViewForApps.setLayoutManager(new GridLayoutManager(context,3));

        viewHolder1.calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPref = context.getSharedPreferences(
                        "com.messenger.Analysis_type", Context.MODE_PRIVATE);
                String analysis_type = sharedPref.getString("com.messenger.Analysis_type", "");

                list = new ArrayList<>();
                list.add(new DialogMenuItem("Today", 0));
                list.add(new DialogMenuItem("7 day", 0));
                list.add(new DialogMenuItem("Month", 0));

                switch (analysis_type) {
                    case "Today":
                        list.set(0,new DialogMenuItem(list.get(0).mOperName,R.drawable.ic_tick));
                        break;
                    case "7 day":
                        list.set(1,new DialogMenuItem(list.get(1).mOperName,R.drawable.ic_tick));
                        break;
                    case "Month":
                        list.set(2,new DialogMenuItem(list.get(2).mOperName,R.drawable.ic_tick));
                        break;

                        default:
                             list.set(2,new DialogMenuItem(list.get(2).mOperName,R.drawable.ic_tick));


                }

                final NormalListDialog listDialog=new NormalListDialog(context,list);
                listDialog.title("show Analysis of");
                listDialog.titleBgColor(Color.parseColor("#3599FC"));

                listDialog.show();

                listDialog.setOnOperItemClickL(new OnOperItemClickL() {
                    @Override
                    public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                        list.set(position,new DialogMenuItem(list.get(position).mOperName,R.drawable.ic_tick));
                        store_shared_string(list.get(position).mOperName);
                        notifyDataSetChanged();
                        listDialog.cancel();
                    }
                });
            }
        });
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        stats=new ArrayList<>();
        Map<String, UsageStats> usageStats = usageStatsManager.queryAndAggregateUsageStats(getStartTime(), System.currentTimeMillis());
        List<UsageStats> stats = new ArrayList<>();
        stats.addAll(usageStats.values());

        TYPE=0;
        analyticsList=new ArrayList<>();




        long youtube=0,whatsapp=0,Duo=0,hangout=0,gmail=0,facebook=0;
        for(UsageStats usageStats1:stats){
            switch(usageStats1.getPackageName()){

                case "com.google.android.youtube":
                    youtube=usageStats1.getTotalTimeInForeground();
                    youtube=youtube/1000;
                    break;
                case "com.whatsapp":
                    whatsapp=usageStats1.getTotalTimeInForeground();
                    whatsapp=whatsapp/1000;
                    break;
                case "com.google.android.apps.tachyon":
                    Duo=usageStats1.getTotalTimeInForeground();
                    Duo=Duo/1000;
                    break;
                case "com.google.android.talk":
                    hangout=usageStats1.getTotalTimeInForeground();
                    hangout=hangout/1000;
                    break;
                case "com.google.android.gm":
                    gmail=usageStats1.getTotalTimeInForeground();
                    gmail=gmail/1000;
                    break;
            }
        }


        analyticsList.add(new Analytics(handle_sharedPref("com.uptodown.messenger.Youtube_launches") , "Youtube", ContextCompat.getDrawable(context, R.drawable.youtube_logo), youtube));
            analyticsList.add(new Analytics(handle_sharedPref("com.uptodown.messenger.Whatsapp_launches"), "WhatsApp", ContextCompat.getDrawable(context, R.drawable.whatsapp_logo), whatsapp));
            analyticsList.add(new Analytics(handle_sharedPref("com.uptodown.messenger.Duo_launches"), "Duo", ContextCompat.getDrawable(context, R.drawable.duo_logo),Duo));
            analyticsList.add(new Analytics(handle_sharedPref("com.uptodown.messenger.Hangouts_launches"),"Hangouts",ContextCompat.getDrawable(context,R.drawable.hangout_logo),hangout));
            analyticsList.add(new Analytics(handle_sharedPref("com.uptodown.messenger.Gmail_launches"), "Gmail", ContextCompat.getDrawable(context, R.drawable.gmail_logo), gmail));
            analyticsList.add(new Analytics(handle_sharedPref("com.uptodown.messenger.Facebook_launches"), "Facebook", ContextCompat.getDrawable(context, R.drawable.ic_facebook), facebook));
        times=0;
        for(Analytics analytics:analyticsList)
        {
            times=times+analytics.getLaunches();
        }
        viewHolder1.times_total.setText(Integer.toString(times));
        String temp=times+"x";
        launches.setText(temp);
            GridAdapter gridAdapter = new GridAdapter(context, analyticsList);
            viewHolder1.recyclerViewForApps.setAdapter(gridAdapter);
        viewHolder1.viewChanger.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    TYPE=1;
                    viewHolder1.recyclerViewForApps.setLayoutManager(new LinearLayoutManager(context));
                    GridAdapter gridAdapter=new GridAdapter(context,analyticsList);
                    viewHolder1.recyclerViewForApps.setAdapter(gridAdapter);
                }
                else{
                    TYPE=0;
                    viewHolder1.recyclerViewForApps.setLayoutManager(new GridLayoutManager(context,3));
                    GridAdapter gridAdapter = new GridAdapter(context, analyticsList);
                    viewHolder1.recyclerViewForApps.setAdapter(gridAdapter);
                }
            }
        });
    }

    private int handle_sharedPref(String name){
        SharedPreferences sharedPref = context.getSharedPreferences(
                name, Context.MODE_PRIVATE);
        return sharedPref.getInt(name, 0);
    }

    private void setupViewHolder0(ViewHolder0 viewHolder0) {

        viewHolder0.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager= (ViewPager)view.getParent().getParent().getParent().getParent();
                viewPager.setCurrentItem(0);
            }
        });
        viewHolder0.scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        viewHolder0.toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){

                    //setup Widget

                }
                else{

                }

            }
        });

    }

    private void setupViewHolder2(final ViewHolder2 viewHolder2){
        List<SocialApps> socialAppsList=new ArrayList<>();
            socialAppsList.add(new SocialApps("9Gag","https://upload.wikimedia.org/wikipedia/commons/thumb/9/97/9GAG_new_logo.svg/2000px-9GAG_new_logo.svg.png","https://9gag.com/"));
            socialAppsList.add(new SocialApps("AOL","https://upload.wikimedia.org/wikipedia/commons/thumb/b/b6/AOL_logo.svg/800px-AOL_logo.svg.png","https://www.aol.com/"));
            socialAppsList.add(new SocialApps("badoo","https://badoocdn.com/badoo-share.png?v2","https://badoo.com/"));
            socialAppsList.add(new SocialApps("BuzzFeed","https://a16z.com/wp-content/uploads/2016/09/logo_buzzfeed.png","https://www.buzzfeed.com/"));
            socialAppsList.add(new SocialApps("classmates","https://cdn2.iconfinder.com/data/icons/social-media-2102/100/social_media_network-10-512.png","https://www.classmates.com/"));
            socialAppsList.add(new SocialApps("Cyworld","http://www.logotypes101.com/logos/986/64E8DB173564E4115CCD0D8E3ABC8B1A/Cyworld.png","https://cy.cyworld.com/cyMain"));
            socialAppsList.add(new SocialApps("dailymotion","https://i.dlpng.com/static/png/457585_preview.png","https://www.dailymotion.com/us"));
            socialAppsList.add(new SocialApps("digg","https://upload.wikimedia.org/wikipedia/commons/thumb/1/18/Digg-new.svg/162px-Digg-new.svg.png","http://digg.com/"));
            socialAppsList.add(new SocialApps("FANDANGO","https://upload.wikimedia.org/wikipedia/commons/thumb/2/23/Fandango_2014.svg/1200px-Fandango_2014.svg.png","https://www.fandango.com/"));
            socialAppsList.add(new SocialApps("flickr","https://upload.wikimedia.org/wikipedia/commons/9/9b/Flickr_logo.png","https://www.flickr.com/"));
            socialAppsList.add(new SocialApps("Flipboard","https://upload.wikimedia.org/wikipedia/commons/5/5c/Flipboard_isotype.png","https://flipboard.com/"));
            socialAppsList.add(new SocialApps("Gmail","https://www.estrelladelmar.com/wp-content/uploads/2018/11/gmail-logo.jpg","https://www.gmail.com"));
            socialAppsList.add(new SocialApps("Google+","https://upload.wikimedia.org/wikipedia/commons/thumb/2/2d/Google_Plus_logo_2015.svg/1024px-Google_Plus_logo_2015.svg.png","https://plus.google.com/"));
            socialAppsList.add(new SocialApps("Linkedin","https://upload.wikimedia.org/wikipedia/commons/c/ca/LinkedIn_logo_initials.png","https://www.linkedin.com"));
            socialAppsList.add(new SocialApps("mail.ru","https://upload.wikimedia.org/wikipedia/commons/thumb/b/bf/Mail.Ru_logo.svg/1280px-Mail.Ru_logo.svg.png","https://mail.ru/"));
            socialAppsList.add(new SocialApps("medium","https://miro.medium.com/max/666/1*uLuWzCXfq2rt1t_TkuLB8A.png","https://medium.com/"));
            socialAppsList.add(new SocialApps("Meetme","https://upload.wikimedia.org/wikipedia/commons/6/63/Meetme_Logo.png","https://www.meetme.com/"));
            socialAppsList.add(new SocialApps("meetup","http://www.sclance.com/pngs/meetup-logo-png/meetup_logo_png_858222.png","https://www.meetup.com/"));
            socialAppsList.add(new SocialApps("myspace","https://images.vexels.com/media/users/3/137385/isolated/preview/7c68663e3e3707a866e4c7ab74808959-myspace-icon-logo-by-vexels.png","https://myspace.com/"));
            socialAppsList.add(new SocialApps("NETFLIX","https://www.stickpng.com/assets/images/580b57fcd9996e24bc43c529.png","https://www.netflix.com"));
            socialAppsList.add(new SocialApps("Outlook","https://upload.wikimedia.org/wikipedia/commons/thumb/0/0b/Microsoft_Outlook_2013_logo.svg/1043px-Microsoft_Outlook_2013_logo.svg.png","https://outlook.live.com"));
            socialAppsList.add(new SocialApps("Pinterest","https://www.stickpng.com/assets/images/580b57fcd9996e24bc43c52e.png","https://outlook.live.com"));
            socialAppsList.add(new SocialApps("Reddit","https://cdn0.iconfinder.com/data/icons/most-usable-logos/120/Reddit-512.png","https://www.reddit.com/"));
            socialAppsList.add(new SocialApps("slack","https://mondrian.mashable.com/uploads%252Fstory%252Fthumbnail%252F89817%252F93e41468-260d-45de-9b2e-65132154f8bd.png%252F950x534.png?signature=GCaiO6VP2G8twfim10NMUwLrW64=&source=https%3A%2F%2Fblueprint-api-production.s3.amazonaws.com","https://slack.com/intl/en-in/"));
            socialAppsList.add(new SocialApps("SoundCloud","https://www.drupal.org/files/project-images/soundcloud-logo-l.jpg","https://soundcloud.com/"));
            socialAppsList.add(new SocialApps("Topface","https://upload.wikimedia.org/wikipedia/commons/thumb/5/5f/Topface_logo.png/600px-Topface_logo.png","https://topface.com/"));
            socialAppsList.add(new SocialApps("Tumblr","https://sourceout.s3-us-west-1.amazonaws.com/uploads/library_image/image/1766/gray-logo.png","https://www.tumblr.com/"));
            socialAppsList.add(new SocialApps("Twoo","https://ahyq.icu/images/4065139807_pt-the-dating-chat.png","https://www.twoo.com/"));
            socialAppsList.add(new SocialApps("Vimeo","https://i0.wp.com/scienceatcal.berkeley.edu/wp-content/uploads/2013/10/Vimeo-Mobile-App-Icon.png?fit=512%2C512","https://vimeo.com/"));
            socialAppsList.add(new SocialApps("Vine","https://www.stickpng.com/assets/images/580b57fcd9996e24bc43c541.png","https://vine.co/"));
            socialAppsList.add(new SocialApps("VK","https://www.stickpng.com/assets/images/584c3c8a1fc21103bb375ba7.png","https://vk.com/"));
            socialAppsList.add(new SocialApps("Weheartit","https://upload.wikimedia.org/wikipedia/en/f/f3/WHI_logo_2016.png","https://weheartit.com/"));
            socialAppsList.add(new SocialApps("Yahoo","http://pluspng.com/img-png/yahoo-png-yahoo-logo-png-800.png","https://in.yahoo.com/"));
            socialAppsList.add(new SocialApps("Yandex","https://upload.wikimedia.org/wikipedia/commons/thumb/8/80/Yandex_Browser_logo.svg/1024px-Yandex_Browser_logo.svg.png",""));
            socialAppsList.add(new SocialApps("Zoosk","https://files.datingscout.com/files/20466/zoosk-logo.png","https://yandex.com/"));
            socialAppsList.add(new SocialApps("Facebook","https://image.flaticon.com/icons/png/512/124/124010.png","https://www.facebook.com/"));
            socialAppsList.add(new SocialApps("twitter","https://www.stickpng.com/assets/images/580b57fcd9996e24bc43c53e.png","https://twitter.com"));
            socialAppsList.add(new SocialApps("Instagram","http://pluspng.com/img-png/instagram-png-instagram-png-logo-1455.png","https://twitter.com"));
            socialAppsList.add(new SocialApps("Snapchat","http://pluspng.com/img-png/logo-snapchat-png-snapchat-logo-png-600.png","https://www.snapchat.com/"));
            socialAppsList.add(new SocialApps("Quora","https://upload.wikimedia.org/wikipedia/commons/thumb/9/91/Quora_logo_2015.svg/1280px-Quora_logo_2015.svg.png","https://www.quora.com/"));
            viewHolder2.socil_app.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));
        SocialMediaAdapter socialMediaAdapter=new SocialMediaAdapter(socialAppsList,context);
        viewHolder2.socil_app.setAdapter(socialMediaAdapter);
        Picasso.get().load("https://e27.co/wp-content/uploads/2018/07/YeeCall.png").into(viewHolder2.image);
        Picasso.get().load("https://www.qr-code-generator.com/wp-content/themes/qr/img-v4/qr-codes-on/gallery/posters/04-social-media-studio51-poster@3x.png").into(viewHolder2.imageView);

        viewHolder2.news_recycler.setLayoutManager(new LinearLayoutManager(context));
        viewHolder2.news_recycler.setHasFixedSize(true);
        viewHolder2.news_recycler.setNestedScrollingEnabled(false);
        articles=new ArrayList<>();
        loadJson(viewHolder2);
    }

    private void loadJson(final ViewHolder2 viewHolder2) {
        ApiInterface apiInterface= ApiClient.getApiClient()
                .create(ApiInterface.class);
        String country= Utils.getCountry();
        Call<News> call;
        call=apiInterface.getNews("in",API_KEY);
        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                if(response.isSuccessful()&&response.body().getArticleList()!=null){
                    if(!articles.isEmpty()){
                        articles.clear();
                    }
                    else
                    {
                        articles=response.body().getArticleList();
                        NewsMainAdapter newsMainAdapter=new NewsMainAdapter(articles,context);
                        viewHolder2.news_recycler.setAdapter(newsMainAdapter);
                        newsMainAdapter.notifyDataSetChanged();
                    }
                }
                else{
                    Toast.makeText(context,"Failed To Load",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return analysisList.size();
    }

    @Override
    public int getItemViewType(int position) {

        ViewNumber number=analysisList.get(position);
        return number.getPosition();
    }


    public class ViewHolder1 extends RecyclerView.ViewHolder {
        private ImageButton calender;
        private ToggleButton viewChanger;
        private TextView times_total;
        private RecyclerView recyclerViewForApps;
        public ViewHolder1(@NonNull View itemView) {
            super(itemView);
            calender=itemView.findViewById(R.id.calender);
            viewChanger=itemView.findViewById(R.id.view_changer);
            times_total=(TextView)itemView.findViewById(R.id.times_total);
            recyclerViewForApps=itemView.findViewById(R.id.list_recycler);
            TYPE=0;
        }
    }
    public class ViewHolder0 extends RecyclerView.ViewHolder{

        private Switch toggle;
        private ConstraintLayout constraintLayout;
        private ImageView scan;

        public ViewHolder0(@NonNull View itemView) {
            super(itemView);
            toggle=itemView.findViewById(R.id.toggle);
            constraintLayout=itemView.findViewById(R.id.constraintLayout);
            scan=itemView.findViewById(R.id.scan);

        }
    }
    public class ViewHolder2 extends RecyclerView.ViewHolder{
        RecyclerView socil_app;
        ImageView image;
        ImageView imageView;
        RecyclerView news_recycler;
        public ViewHolder2(@NonNull View itemView) {
            super(itemView);
            socil_app=itemView.findViewById(R.id.social_recycler);
            image=itemView.findViewById(R.id.explore_img1);
            imageView=itemView.findViewById(R.id.explore_img3);
            news_recycler=itemView.findViewById(R.id.news_recycler);
        }
    }

}