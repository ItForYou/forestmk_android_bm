package kr.co.itforone.forestmk_android;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.os.Build;
import android.os.Vibrator;
import android.support.v4.app.INotificationSideChannel;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class My_Firebase_Messaging_Service extends FirebaseMessagingService {
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d("remote","Message:"+s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> pushMessage = remoteMessage.getData();

        Log.d("onMessageReceived","init");

        if(remoteMessage.getData().size() > 0) {

            if (remoteMessage != null && remoteMessage.getData().size() > 0) {
                sendNotification(remoteMessage);
            }
        }
        //  sendNotification(remoteMessage);
    }
    public void sendNotification(RemoteMessage remoteMessage){
        int notify_id = 2101;
        String messageBody=remoteMessage.getData().get("message");
        String subject=remoteMessage.getData().get("subject");
        String goUrl=remoteMessage.getData().get("goUrl");
        String tag=remoteMessage.getData().get("tag");
        String channelId = "noti";
      //  Log.d("bundle","Message:"+remoteMessage.getNotification().getTag().toString());

        if(tag.equals("chat")){
            notify_id = 2101;
        }
        else {
            notify_id = 2102;
        }


        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("goUrl",goUrl);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 15157 /* Request code */,
                intent, PendingIntent.FLAG_ONE_SHOT);

        /**/
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(subject)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setVibrate(new long[]{100,150})
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody));

        Notification notification = notificationBuilder.build();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100,150});
            channel.enableLights(true);
            notificationManager.createNotificationChannel(channel);

            AudioAttributes att = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();


        }

        notificationManager.notify(notify_id /* ID of notification */, notification);
      /*  Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(300);*/

    }
}
