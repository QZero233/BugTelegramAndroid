package com.qzero.telegram.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.qzero.telegram.R;
import com.qzero.telegram.view.activity.ChatActivity;
import com.qzero.telegram.view.activity.UserCenterActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class NotificationUtils extends ContextWrapper {

    private Logger log= LoggerFactory.getLogger(getClass());

    private static NotificationUtils instance;

    public static final String id = "channel_1";
    public static final String name = "channel_name_1";

    private NotificationManager manager;
    private Notification notification;

    public static NotificationUtils getInstance(Context context){
        if(instance==null)
            instance=new NotificationUtils(context);
        return instance;
    }

    private NotificationUtils(Context context){
        super(context);
        getManager();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(){
        NotificationChannel channel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
        getManager().createNotificationChannel(channel);
    }
    private NotificationManager getManager(){
        if (manager == null){
            manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        return manager;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private Notification.Builder getChannelNotification(String title, String content){
        return new Notification.Builder(getApplicationContext(), id)
                .setContentTitle(title)
                .setContentText(content)
                //TODO ADD SMALL ICON AND LARGE ICON
                //.setSmallIcon(R.drawable.)
                //.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))//TODO REPLACE MY OWN ICON
                .setAutoCancel(true);
    }

    private Notification.Builder getNotification_25(String title, String content){
        return new Notification.Builder(this).setTicker("123").
                //setSmallIcon(R.mipmap.ic_launcher_bt).setLargeIcon( BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))//TODO REPLACE MY OWN ICON
                setContentText(content).setContentTitle(title);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void sendNotification(String title, String content,PendingIntent pendingIntent){
        if (Build.VERSION.SDK_INT>=26){
            createNotificationChannel();
            this.notification = getChannelNotification
                    (title, content).setContentIntent(pendingIntent).build();
            getManager().notify(1,notification);
        }else{
            this.notification = getNotification_25(title, content).setContentIntent(pendingIntent).build();
            getManager().notify(1,notification);
        }
    }

    public void cancelNotification(){
        manager.cancelAll();
    }

    public void sendMessageNotification(int freshMessageCount){
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,
                new Intent(this, UserCenterActivity.class),PendingIntent.FLAG_UPDATE_CURRENT);
        sendNotification("您有未读消息", String.format("您有 %d 条未读消息", freshMessageCount),pendingIntent);
    }

}
