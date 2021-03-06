package kr.nexters.onepage.common;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import kr.nexters.onepage.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by hoody on 2017-01-21.
 */

public class BaseActivity extends AppCompatActivity {

    BroadcastReceiver networkReceiver;
    BroadcastReceiver gpsReceiver;
    ConnectivityManager connectivityManager;
    LocationManager locManager;
    NetworkInfo networkInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        networkReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                networkConnCheck();
            }
        };

        //사용 중 GPS가 꺼졌을 경우 check
        gpsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                gpsConnCheck();
            }
        };
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void networkConnCheck() {

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        if ( networkInfo == null || !networkInfo.isConnected()) {
            showDialog("network", getString(R.string.alert_network));
        }
    }

    private void gpsConnCheck() {
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if(!locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            showDialog("gps", getString(R.string.alert_gps));
        }
    }

    public void showDialog(String type, String message) {

        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(BaseActivity.this);
        alertBuilder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.retry), null)
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                    }
                });

        Dialog dialog = alertBuilder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button positiveBtn = ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                positiveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (type) {
                            case "network" :
                                if ( networkInfo != null && networkInfo.isConnected()) {
                                    dialog.dismiss();
                                }

                                break;
                            case "gps" :
                                if(!locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                                    startActivity(intent);
                                }
                                else {
                                    dialog.dismiss();
                                }
                                break;
                        }
                    }
                });
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }

    protected void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    protected void replaceFragment(int containerViewId, Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(containerViewId, fragment)
                .commitAllowingStateLoss();
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter networkFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, networkFilter);

        IntentFilter gpsFilter = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
        registerReceiver(gpsReceiver, gpsFilter);

    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(networkReceiver);

        unregisterReceiver(gpsReceiver);
    }
}
