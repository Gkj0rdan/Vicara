package com.example.vicara;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;


import androidx.core.app.NotificationCompat;

public class NetworkSchedulerService extends JobService implements
        ConnectivityReceiver.ConnectivityReceiverListener {

    private static final String TAG = NetworkSchedulerService.class.getSimpleName();
    public static final String CHANNEL_ID = "Vicara";
    public static RemoteViews notificationView;
    public static NotificationManager mNotificationManager;
    public static Notification notification;
    BluetoothAdapter btAdapter;
    ConnectivityManager cm;
    NetworkInfo netInfo;

    private ConnectivityReceiver mConnectivityReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        mConnectivityReceiver = new ConnectivityReceiver(this);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();
    }


    /**
     * When the app's NetworkConnectionActivity is created, it starts this service. This is so that the
     * activity and this service can communicate back and forth. See "setUiCallback()"
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        showNotification();
        return START_STICKY;
    }

    private void showNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent bluetoothIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Vicara Notification")
                .setContentText("Vicara")
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(notificationView)
                .setSmallIcon(R.drawable.app_icon)
                .setContentIntent(pendingIntent)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(1, notification);
    }


    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i(TAG, "onStartJob" + mConnectivityReceiver);
        registerReceiver(mConnectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mConnectivityReceiver, filter);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i(TAG, "onStopJob");
        unregisterReceiver(mConnectivityReceiver);
        return true;
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        updateNetworkStatus(isConnected);

    }

    private void updateNetworkStatus(boolean isConnected) {
        if (isConnected)
            notificationView.setImageViewResource(R.id.network_indicator, R.drawable.network_on);
        else
            notificationView.setImageViewResource(R.id.network_indicator, R.drawable.network_off);
        mNotificationManager.notify(1, notification);
    }

    public static void updateBluetoothStatus(boolean isConnected) {
        if (isConnected)
            notificationView.setImageViewResource(R.id.bt_indicator, R.drawable.bluetooth_on);
        else
            notificationView.setImageViewResource(R.id.bt_indicator, R.drawable.bluetooth_off);
        mNotificationManager.notify(1, notification);
    }

    private void createNotificationChannel() {
        notificationView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.notification_view);
        if (btAdapter.isEnabled())
            notificationView.setImageViewResource(R.id.bt_indicator, R.drawable.bluetooth_on);
        else
            notificationView.setImageViewResource(R.id.bt_indicator, R.drawable.bluetooth_off);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID,
                    "Vicara", NotificationManager.IMPORTANCE_LOW);
            mNotificationManager = getSystemService(NotificationManager.class);
            mNotificationManager.createNotificationChannel(serviceChannel);
        }
    }


}