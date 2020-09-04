package com.mrserviceman.messenger.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mrserviceman.messenger.Activity.NewsFragmentActivity;
import com.mrserviceman.messenger.Utils.Utils;
import com.squareup.picasso.Picasso;
import com.mrserviceman.messenger.Model.News.Article;
import com.mrserviceman.messenger.R;

import java.util.List;

public class NewsMainAdapter extends RecyclerView.Adapter<NewsMainAdapter.ViewHolder> implements View.OnClickListener{
    public static List<Article> aricles;
    private Context context;

    public NewsMainAdapter(List<Article> aricles, Context context) {
        this.aricles = aricles;
        this.context = context;
    }

    @NonNull
    @Override
    public NewsMainAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(context).inflate(R.layout.news_item_short,viewGroup,false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsMainAdapter.ViewHolder viewHolder, int i) {

        Article model=aricles.get(i);
        viewHolder.news_short_img.setBackground(Utils.getRandomDrawbleColor());
        try{
            Picasso.get().load(model.getUrlToImage()).fit().into(viewHolder.news_short_img);
        }
        catch (Exception ex){

        }
        viewHolder.source.setText(model.getSource().getName());
        String temp="\u2022"+Utils.DateToTimeFormat(model.getPublishedAt());
        viewHolder.time.setText(temp);
        viewHolder.headline.setText(model.getTitle());

    }
    @Override
    public int getItemCount() {
        return aricles.size();
    }

    @Override
    public void onClick(View view) {

        Intent intent=new Intent(context, NewsFragmentActivity.class);
        intent.putExtra("number",getFragmentNumber(((TextView)view.findViewById(R.id.headline)).getText().toString()));
        context.startActivity(intent);


    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView headline,source,time;
        private ImageView news_short_img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            headline=itemView.findViewById(R.id.headline);
            source=itemView.findViewById(R.id.source);
            time=itemView.findViewById(R.id.ago);
            news_short_img=itemView.findViewById(R.id.image_news_short);
        }
    }
    private int getFragmentNumber(String title){
        int num=0;
        for(int i=0;i<aricles.size();i++){
            Article article=aricles.get(i);
            if(article.getTitle().equals(title)){
                num =i;
                break;
            }
        }
        return num;

    }
}
