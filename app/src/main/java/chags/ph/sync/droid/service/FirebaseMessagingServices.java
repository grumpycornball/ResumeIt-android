package chags.ph.sync.droid.service;



import android.content.Intent;
import android.net.Uri;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class FirebaseMessagingServices extends FirebaseMessagingService  {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData().size() > 0) {
            String videoId=null;
            float time=0.0f;
            SharedPrefManager sharedPrefManager = SharedPrefManager.getSharedPrefManager(this);
            try{

                 sharedPrefManager.setToken(remoteMessage.getData().getOrDefault("link","null").toString(),"message");
                 videoId=remoteMessage.getData().getOrDefault("link",null);
                 time= Float.parseFloat(remoteMessage.getData().getOrDefault("time",null).toString());
            }catch(Exception e){
                sharedPrefManager.setToken(e.getMessage(),"message");
            }



//           Second thoughts
            if(videoId!=null)
            {
                //Code to handle youtube activity

                if(videoId.contains("youtube"))
                {
                    String id=videoId.split("\\?v=")[1].split("&")[0];

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://www.youtube.com/watch?v="+id+"&t="+(int)time));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    this.startActivity(intent);

                } else if (videoId.contains("netflix"))
                {
                    try {
                            String id=videoId.split("watch/")[1].split("\\?trackId")[0];
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("https://www.netflix.com/watch/"+id+"?t="+(int)time));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            this.startActivity(intent);
                        }
                    catch(Exception e)
                        {
                            // netflix app isn't installed, send to website.
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(videoId));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            this.startActivity(intent);
                        }
                }else
                {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(videoId));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    this.startActivity(intent);

                }


            }


        }
    }

    @Override
    public void onNewToken(String token) {

        if(token!=null)
        {
            SharedPrefManager sharedPrefManager = SharedPrefManager.getSharedPrefManager(this);
            sharedPrefManager.setToken(token,"token");
        }
    }

}
