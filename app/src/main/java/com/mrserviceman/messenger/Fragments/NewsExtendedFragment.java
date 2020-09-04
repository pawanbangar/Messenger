package com.mrserviceman.messenger.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import com.squareup.picasso.Picasso;
import com.mrserviceman.messenger.Activity.NewsFragmentActivity;
import com.mrserviceman.messenger.Activity.WebActivity;
import com.mrserviceman.messenger.Adapter.NewsMainAdapter;
import com.mrserviceman.messenger.R;
import com.mrserviceman.messenger.Utils.Utils;

public class NewsExtendedFragment extends Fragment {

    private ImageView news_img;
    private ImageView back_news;
    private TextView headline;
    private TextView ago;
    private TextView source;
    private ImageView share;
    private int num;

    public NewsExtendedFragment(int num) {
        this.num = num;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.news_fragment, container, false);
        news_img = view.findViewById(R.id.news_img);
        back_news = view.findViewById(R.id.back_news);
        headline = view.findViewById(R.id.headline);
        ago = view.findViewById(R.id.ago);
        source = view.findViewById(R.id.news_source);
        share = view.findViewById(R.id.news_share);
        Button read_full_story = view.findViewById(R.id.read_full_story);


        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_TEXT, NewsMainAdapter.aricles.get(NewsFragmentActivity.ITEM_NUMBER).getTitle() +"\n"+ NewsMainAdapter.aricles.get(NewsFragmentActivity.ITEM_NUMBER).getUrl());
                  startActivity(Intent.createChooser(sendIntent, "Share to"));
              }
              catch (Exception ex)
              {
                  Toast.makeText(getContext(),"No App Found to share",Toast.LENGTH_SHORT).show();
              }
            }
        });

        TextView description = view.findViewById(R.id.description);
        description.setText(NewsMainAdapter.aricles.get(NewsFragmentActivity.ITEM_NUMBER).getDescription());
        read_full_story.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getContext().startActivity(new Intent(getContext(), WebActivity.class).putExtra("URL", NewsMainAdapter.aricles.get(NewsFragmentActivity.ITEM_NUMBER).getUrl()));
            }
        });

        headline.setText(NewsMainAdapter.aricles.get(NewsFragmentActivity.ITEM_NUMBER).getTitle());
        Picasso.get().load(NewsMainAdapter.aricles.get(NewsFragmentActivity.ITEM_NUMBER).getUrlToImage()).into(news_img);
        String temp = "\u2022" + Utils.DateToTimeFormat(NewsMainAdapter.aricles.get(NewsFragmentActivity.ITEM_NUMBER).getPublishedAt());

        ago.setText(temp);

        source.setText(NewsMainAdapter.aricles.get(NewsFragmentActivity.ITEM_NUMBER).getSource().getName());

        back_news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return view;


    }
}
