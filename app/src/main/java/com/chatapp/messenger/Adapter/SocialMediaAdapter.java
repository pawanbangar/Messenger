package com.chatapp.messenger.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.messenger.Activity.WebActivity;
import com.squareup.picasso.Picasso;
import com.chatapp.messenger.Model.AnalyticsFragMent.SocialApps;
import com.chatapp.messenger.R;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class SocialMediaAdapter extends RecyclerView.Adapter<SocialMediaAdapter.ViewHolder> implements View.OnClickListener {
  private List<SocialApps> socialList;
  private Context context;

    public SocialMediaAdapter(List<SocialApps> socialList, Context context) {
        this.socialList = socialList;
        this.context = context;
    }

    @NonNull
    @Override
    public SocialMediaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(context).inflate(R.layout.social_media_item,viewGroup,false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        SocialApps socialApps=socialList.get(i);
        Picasso.get().load(socialApps.getUrl()).into(viewHolder.image);
        viewHolder.imageText.setText(socialApps.getAppName());

    }
    public  Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Toast.makeText(context,"Image Ready",Toast.LENGTH_SHORT).show();
            Drawable d = Drawable.createFromStream(is, "src_name");
            return d;
        } catch (Exception e) {
            Toast.makeText(context,"Exception in loading",Toast.LENGTH_SHORT).show();

            return null;
        }
    }

    @Override
    public int getItemCount() {
        return socialList.size();
    }

    @Override
    public void onClick(View view) {
        Intent intent=new Intent(context, WebActivity.class);
        intent.putExtra("URL",getSocialObject(((TextView)view.findViewById(R.id.social_media_text)).getText().toString()));
        context.startActivity(intent);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView image;
        private TextView imageText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image=itemView.findViewById(R.id.social_media_img);
            imageText=itemView.findViewById(R.id.social_media_text);
        }
    }
    private String getSocialObject(String appName){
        String name=null;
        for(SocialApps social:socialList){
            if(social.getAppName().equals(appName)){
                name=social.getWebsite();
                break;
            }
        }
        return name;

    }
}
