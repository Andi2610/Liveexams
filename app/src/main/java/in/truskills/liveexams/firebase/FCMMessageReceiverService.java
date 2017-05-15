package in.truskills.liveexams.firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

//import com.google.firebase.messaging.FirebaseMessagingService;
//import com.google.firebase.messaging.RemoteMessage;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import in.truskills.liveexams.MainScreens.MainActivity;
import in.truskills.liveexams.R;
import in.truskills.liveexams.authentication.SplashScreen;

/**
 * It executes when a message is obtained from firebase..
 * This is implemented for the students to get notifications when:
 * 1. Reports are generated
 * 2. Exam details change like: date,time etc.
 * Functions:
 * 1. onMessageReceived(): to receive a message from firebase and call sendNotification() function..
 * 2. sendNotification(): to prepare a notification with the received message with a vibration sound, message body which onClick will open the SplashScreen of the app..
 */

public class FCMMessageReceiverService
        extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        //When message from firebase is recieved, show it to the user through a notification..
        sendNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
    }

    private void sendNotification(String messageBody,String body) {

        //For notification building and it's onClick event..

        //Open SplashScreen activity onClick on notification..
        Intent intent = new Intent(this, SplashScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent,
                PendingIntent.FLAG_ONE_SHOT);
        //Insert a vibration sound when notification arrives on phone..
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.app_icon)
                .setContentTitle(messageBody)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        //Create and show notification..
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notificationBuilder.build());
    }

}
