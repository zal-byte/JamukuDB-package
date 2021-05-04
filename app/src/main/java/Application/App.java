package Application;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {
    public static String CHANNEL_ID = "ALARM_SERVICE_CHANNEL";

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel("ALARM_SERVICE_CHANNEL", "Alarm Service Channel", NotificationManager.IMPORTANCE_DEFAULT);
            ((NotificationManager) getSystemService(NotificationManager.class)).createNotificationChannel(channel);
        }
    }

    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }
}
