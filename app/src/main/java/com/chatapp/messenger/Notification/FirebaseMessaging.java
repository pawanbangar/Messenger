package com.chatapp.messenger.Notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;

import com.chatapp.messenger.Activity.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessaging extends FirebaseMessagingService {

    FirebaseUser firebaseUser;
    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {

        String sent = remoteMessage.getData().get("sent");
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null&&sent.equals(firebaseUser.getUid())){
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){

                    sendOandAboveNotification(remoteMessage);
                }
                else{

                    sendNormalNotification(remoteMessage);
                }
        }
    }

    private void sendOandAboveNotification(RemoteMessage remoteMessage) {

        String user=remoteMessage.getData().get("user");
        String icon=remoteMessage.getData().get("icon");
        String title=remoteMessage.getData().get("title");
        String body=remoteMessage.getData().get("body");

        RemoteMessage.Notification notification=remoteMessage.getNotification();

        int i=Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent=new Intent(this, Message.class);
        Bundle bundle=new Bundle();
        bundle.putString("UserId",user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,i,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri defSounUri=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        OreoandAboveNotification oreoandAboveNotification=new OreoandAboveNotification(this);

        Notification.Builder builder=oreoandAboveNotification.getONotifications(title,body,pendingIntent,defSounUri,icon);

        NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        int j=0;
        if(i>0){

            j=i;
        }
        notificationManager.notify(j,builder.build());



    }

    private void sendNormalNotification(RemoteMessage remoteMessage) {
        String user=remoteMessage.getData().get("user");
        String icon=remoteMessage.getData().get("icon");
        String title=remoteMessage.getData().get("title");
        String body=remoteMessage.getData().get("body");

        RemoteMessage.Notification notification=remoteMessage.getNotification();

        int i=Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent=new Intent(this,Message.class);
        Bundle bundle=new Bundle();
        bundle.putString("UserId",firebaseUser.getUid());
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,i,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri defSounUri=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentText(body)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setSound(defSounUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        int j=0;
        if(i>0){

            j=i;
        }
        notificationManager.notify(j,builder.build());



    }
}