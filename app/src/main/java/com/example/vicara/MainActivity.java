package com.example.vicara;



import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends Activity {
TextView txtNetworkStatus, txtBluetoothStatus;
Switch aBluetoothSwitch;
BluetoothAdapter btAdapter;
ConnectivityManager cm;
NetworkInfo netInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initResources();
        aBluetoothSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (aBluetoothSwitch.isChecked()){
                    txtBluetoothStatus.setText(R.string.bt_on);
                    txtBluetoothStatus.setTextColor(getResources().getColor(R.color.green));
                    btAdapter.enable();
                }
                else {
                    txtBluetoothStatus.setText(R.string.bt_off);
                    txtBluetoothStatus.setTextColor(getResources().getColor(R.color.design_default_color_error));
                    btAdapter.disable();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        if(btAdapter == null){
            btAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        else if(btAdapter.isEnabled()){
            txtBluetoothStatus.setText(R.string.bt_on);
            txtBluetoothStatus.setTextColor(getResources().getColor(R.color.green));
            aBluetoothSwitch.setChecked(true);
        }
        else {
            txtBluetoothStatus.setText(R.string.bt_off);
            txtBluetoothStatus.setTextColor(getResources().getColor(R.color.design_default_color_error));
            aBluetoothSwitch.setChecked(false);
        }

        if(isOnline(MainActivity.this)){
            switch (netInfo.getTypeName()){
                case "MOBILE":
                    txtNetworkStatus.setText(R.string.network_connected_mobile);
                    txtNetworkStatus.setTextColor(getResources().getColor(R.color.green));
                    break;
                case "WIFI":
                    txtNetworkStatus.setText(R.string.network_connected_wifi);
                    txtNetworkStatus.setTextColor(getResources().getColor(R.color.green));
                    break;
            }
        }
        else{
            txtNetworkStatus.setText(R.string.network_disconnected);
            txtNetworkStatus.setTextColor(getResources().getColor(R.color.design_default_color_error));
        }
    }

    private void initResources() {
        txtNetworkStatus = findViewById(R.id.network_value);
        txtBluetoothStatus = findViewById(R.id.bluetooth_status);
        aBluetoothSwitch = findViewById(R.id.bluetooth_toggle);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        cm = (ConnectivityManager) MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();
    }

    public boolean isOnline(Context context) {
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }


}