package bluefirelabs.mojo.background_tasks;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import bluefirelabs.mojo.MainHub;
import bluefirelabs.mojo.handlers.MyNotificationManager;

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    private static final String TAG = "FirebaseMessaging: ";
    private static final String USERNAME = "username";
    private static final String EMAIL = "email";
    //private static final String IMAGEURL = "imageUrl";
    private static final String UID = "uid";
    private static final String TEXT = "text";
    private static final String TITLE = "title";
    private static final String BODY = "body";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Map<String, String> payload = remoteMessage.getData();
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            String username = payload.get(USERNAME);
            String email = payload.get(EMAIL);
            //String imageUrl = payload.get(IMAGEURL);
            String uid = payload.get(UID);
            String text = payload.get(TEXT);

            showNotification(payload);
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {

            Map<String, String> payload = remoteMessage.getData();
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            //String username = payload.get(USERNAME);
            //String email = payload.get(EMAIL);
            //String imageUrl = payload.get(IMAGEURL);
            String uid = payload.get(UID);
            String message = payload.get("message");
            //String text = payload.get(TEXT);
            //String title = payload.get(TITLE);
            //String body = payload.get(BODY);

            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            Log.d(TAG, "Message data payload: " + remoteMessage.getNotification().getTitle());
            //showNotification(payload);
            notifyUser(message, uid);
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

        //notifyUser(remoteMessage.getFrom(), remoteMessage.getNotification().getBody());

        //notifyUser(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
    }

    private void showNotification(Map<String, String> payload) {
        MyNotificationManager myNotificationManager = new MyNotificationManager(getApplicationContext());
        myNotificationManager.showNotification(payload.get("customeruid"), payload.get("vendoruid"), new Intent(getApplicationContext(), MainHub.class));     //what happens when the notification is clicked
                                                                                                                                                  //TODO: Add more variables such as imageUrl for a custom notificatoin view
    }

    public void notifyUser(String from, String notification){
        MyNotificationManager myNotificationManager = new MyNotificationManager(getApplicationContext());
        myNotificationManager.showNotification(from, notification, new Intent(getApplicationContext(), MainHub.class));     //what happens when the notification is clicked
    }
}
