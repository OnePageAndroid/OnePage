package kr.nexters.onepage.splash;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import kr.nexters.onepage.intro.IntroActivity;
import kr.nexters.onepage.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //2초후 인트로로 넘기는 핸들러
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, IntroActivity.class));
            finish();
        }, 2000);
    }
}
