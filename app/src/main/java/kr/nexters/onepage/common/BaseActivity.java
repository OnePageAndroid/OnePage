package kr.nexters.onepage.common;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import kr.nexters.onepage.R;

/**
 * Created by hoody on 2017-01-21.
 */

public class BaseActivity extends AppCompatActivity {
    BroadcastReceiver networkReceiver;
    ConnectivityManager connectivityManager;
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
    }

    private void networkConnCheck() {

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        if ( networkInfo == null || !networkInfo.isConnected()) {
            showDialog();
        }
    }

    private void showDialog() {

        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(BaseActivity.this);
        alertBuilder.setMessage(getString(R.string.alert_network))
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
                        if ( networkInfo != null && networkInfo.isConnected()) {
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, filter);

    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(networkReceiver);
    }
}
