package com.chatapp.messenger.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.messenger.Adapter.ViewAdapter;
import com.chatapp.messenger.Activity.StatisticsActivity;
import com.chatapp.messenger.Activity.WebActivity;
import com.chatapp.messenger.Model.AnalyticsFragMent.ViewNumber;
import com.chatapp.messenger.R;

import java.util.ArrayList;
import java.util.List;

public class AnalyticsFragMent extends Fragment{
    //VisibleForTesting for variables below
    public ImageView launches;
    public TextView text_for_launches;
    private ImageView share;
    public AnalyticsFragMent() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         View view=inflater.inflate(R.layout.fragment_analytics, container, false);
        SearchView google_search = view.findViewById(R.id.google_search);
        launches=view.findViewById(R.id.launches);
        text_for_launches=view.findViewById(R.id.text_for_launches);
        google_search.setIconified(false);
        google_search.clearFocus();
        share=view.findViewById(R.id.share);
        ImageView facebook=view.findViewById(R.id.facebook);
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), WebActivity.class);
                intent.putExtra("URL","https://www.facebook.com/");
                startActivity(intent);
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Heyy Try This All in One Messaging App I have Downloaded from uptodown Store");
                startActivity(Intent.createChooser(sendIntent, "Share With"));
            }
        });
        RecyclerView recyclerView = view.findViewById(R.id.recycler);
         recyclerView.setHasFixedSize(true);
         recyclerView.setNestedScrollingEnabled(false);
         recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        google_search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                Intent intent=new Intent(getContext(),WebActivity.class);
                intent.putExtra("URL", "https://www.google.com/search?q="+s.replace(" ","+"));
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

         launches.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 startActivity(new Intent(getContext(), StatisticsActivity.class));
             }
         });



        List<ViewNumber> viewNumberList = new ArrayList<>();
         viewNumberList.clear();
         viewNumberList.add(new ViewNumber(0));
         viewNumberList.add(new ViewNumber(1));
         viewNumberList.add(new ViewNumber(2));
        ViewAdapter viewAdapter = new ViewAdapter(viewNumberList, getContext(),text_for_launches);
         recyclerView.setAdapter(viewAdapter);

         return view;

    }
}