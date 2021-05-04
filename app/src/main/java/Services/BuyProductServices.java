package Services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.qiva.jamuku.BuyActivity;
import com.qiva.jamuku.R;

public class BuyProductServices extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        PendingIntent pendingIntent = PendingIntent.getActivity((Context) this, 0, new Intent((Context) this, BuyActivity.class), 0);
        Notification notification = (new NotificationCompat.Builder((Context) this, "ALARM_SERVICE_CHANNEL")).setContentTitle(intent.getStringExtra("message")).setContentIntent(pendingIntent).setSmallIcon(R.drawable.ic_launcher_foreground).build();
        startForeground(0, notification);
        @SuppressLint("StaticFieldLeak")
        class SB extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);
                stopBuyService();
            }

            @Override
            protected String doInBackground(Void... voids) {
                return null;
            }
        }
        SB sb = new SB();
        sb.execute();
        return START_STICKY;
    }


    void stopBuyService() {
        stopService(new Intent(this, BuyProductServices.class));
    }

    public static boolean isRunning = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        isRunning = true;
    }

    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
    }
}
