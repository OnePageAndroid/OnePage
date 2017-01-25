package kr.nexters.onepage.splash;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import java.util.ArrayList;
import java.util.List;
import kr.nexters.onepage.R;
import kr.nexters.onepage.common.BaseActivity;
import kr.nexters.onepage.common.NetworkManager;
import kr.nexters.onepage.common.PropertyManager;
import kr.nexters.onepage.common.model.ServerResponse;
import kr.nexters.onepage.main.MainActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends BaseActivity {

    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        checkProgress();

    }

    //퍼미션 체크(주소록, 위치)
    private void initPermission() {
        new TedPermission(this)
                .setPermissionListener(permissionListener)
                .setDeniedMessage("권한이 거부되어있습니다.\n\n[설정] > [권한]에 들어가서 권한을 켜주세요.")
                .setPermissions(
                        Manifest.permission.GET_ACCOUNTS,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .check();

    }

    //퍼미션 획득시 로그인, 실패시 토스트 및 종료
    PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            AccountManager accountManager = AccountManager.get(getApplicationContext());
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED) {
                Account accounts[] = accountManager.getAccounts();

                List<String> myGoogleIds = new ArrayList<>();

                //쉐어드프리퍼런스에 있는 구글 아이디랑 같은지 검사한다.
                //같으면 그 아이디로 로그인, 다르면 첫번쨰 구글계정으로 로그인한다.
                //쉐어드리퍼런스에 구글아이디가 없을 시 가장 첫번쨰 구글아이디를 아이디로 한다.
                for (Account account : accounts) {
                    if (account.type.equals("com.google")) {
                        String id = account.name;

                        if (PropertyManager.getInstance().getId().equals(id)) {
                            login(id);
                            return;
                        }

                        myGoogleIds.add(id);
                    }
                }

                if (!myGoogleIds.isEmpty()) {
                    PropertyManager.getInstance().setId(myGoogleIds.get(0));
                    login(myGoogleIds.get(0));
                } else {
                    Toast.makeText(SplashActivity.this, "맙소사 구글아이디가 없다니...", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(SplashActivity.this, "[설정] > [권한]에 들어가서 권한을 켜주세요.", Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    private void login(String id) {
        //네트워크 처리 성공일시 메인으로 넘김
        Log.d(SplashActivity.class.getSimpleName(), "login id = " + id);

        NetworkManager.getInstance().getApi()
                .login(id)
                .enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                        if (response.isSuccessful() && response.body() != null &&response.body().isSuccess()) {
                            Log.d(SplashActivity.class.getSimpleName(), response.body().message);

                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                        finish();
                    }

                    @Override
                    public void onFailure(Call<ServerResponse> call, Throwable t) {
                        Toast.makeText(SplashActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }


    // Check GPS on / off
    // If gps is on, proceed
    private void checkProgress(){
        if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            new Handler().postDelayed(this::initPermission, 1000);
        }
        else {
            showGpsDialog();
        }
    }

    private void showGpsDialog() {

        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(SplashActivity.this);
        alertBuilder.setMessage(getString(R.string.alert_gps))
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
                Button positiveBtn = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                positiveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            startActivity(intent);
                        } else {
                            checkProgress();
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }

}