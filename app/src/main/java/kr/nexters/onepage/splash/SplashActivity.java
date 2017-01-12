package kr.nexters.onepage.splash;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.List;

import kr.nexters.onepage.R;
import kr.nexters.onepage.common.PropertyManager;
import kr.nexters.onepage.main.MainActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //1초후 퍼미션 체크로 넘기는 핸들러
        new Handler().postDelayed(this::initPermission, 1000);
    }

    //퍼미션 체크(주소록, 위치)
    private void initPermission() {
        new TedPermission(this)
                .setPermissionListener(permissionListener)
                .setDeniedMessage("권한이 거부되어있습니다.\n\n[설정] > [권한]에 들어가서 권한을 켜주세요.")
                .setPermissions(
                        Manifest.permission.GET_ACCOUNTS,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
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

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
