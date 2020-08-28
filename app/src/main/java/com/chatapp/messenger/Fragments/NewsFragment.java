package com.chatapp.messenger.Fragments;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.messenger.Adapter.ViewAdapter;
import com.chatapp.messenger.api.ApiClient;
import com.chatapp.messenger.api.ApiInterface;
import com.chatapp.messenger.Activity.WebActivity;
import com.chatapp.messenger.Adapter.NewsMainAdapter;
import com.chatapp.messenger.Model.News.Article;
import com.chatapp.messenger.Model.News.News;
import com.chatapp.messenger.R;
import com.chatapp.messenger.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsFragment extends Fragment {

    private RecyclerView news_recycler;
    private List<Article> articles;
    public NewsFragment() {

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view=inflater.inflate(R.layout.fragment_news,container,false);
            news_recycler=view.findViewById(R.id.news_recycler);
            news_recycler.setHasFixedSize(true);
            news_recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        ImageView share=view.findViewById(R.id.share);
        SearchView googleSearch=view.findViewById(R.id.google_search);


        ImageView facebook=view.findViewById(R.id.facebook);
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), WebActivity.class);
                intent.putExtra("URL","https://www.facebook.com/");
                startActivity(intent);
            }
        });

        googleSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                Intent intent=new Intent(getContext(), WebActivity.class);
                intent.putExtra("URL", "https://www.google.com/search?q="+s.replace(" ","+"));
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
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
       articles=new ArrayList<>();
       loadJson();
      return view;

    }
    private void loadJson() {
        ApiInterface apiInterface= ApiClient.getApiClient()
                .create(ApiInterface.class);
        String country= Utils.getCountry();
        Call<News> call;
        call=apiInterface.getNews(country, ViewAdapter.API_KEY);
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
                        NewsMainAdapter newsMainAdapter=new NewsMainAdapter(articles,getContext());
                        news_recycler.setAdapter(newsMainAdapter);
                        newsMainAdapter.notifyDataSetChanged();
                    }
                }
                else{
                    Toast.makeText(getContext(),"Failed To Load",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {

            }
        });
    }
}
