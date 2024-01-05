package com.app.dz.quranapp.LocationParte;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.app.dz.quranapp.MainActivity;
import com.app.dz.quranapp.OnBoardingParte.StartActivityPager;
import com.app.dz.quranapp.R;
import com.app.dz.quranapp.Util.SharedPreferenceManager;
import com.app.dz.quranapp.databinding.ActivitySplashBinding;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {

    ActivitySplashBinding binding;
    private boolean iSFirstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         binding = ActivitySplashBinding.inflate(getLayoutInflater());
         setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getColor(R.color.white));
        }
        //Glide.with(this).load(R.drawable.splash_logo).into(binding.rounded);
    }

    @Override
    protected void onStart() {
        super.onStart();


        /*
        Thread th = new Thread() {
            public void run() {
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Intent intent;
                    if (SharedPreferenceManager.getInstance(SplashScreenActivity.this).iSFirstTime()){
                        intent = new Intent(SplashScreenActivity.this,StartActivityPager.class);
                    }else {
                        intent = new Intent(SplashScreenActivity.this,MainActivity.class);
                    }
                    startActivity(intent);
                    finish();


                }
            }
        };
        th.start();
*/
        // Define a Handler
        Handler handler = new Handler(Looper.getMainLooper());

// Create a Runnable to run on the background thread
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // Set the animation and play it
                iSFirstTime = SharedPreferenceManager.getInstance(SplashScreenActivity.this).iSFirstTime();
                binding.lottieAnimationView.setAnimation(R.raw.logojson);
                binding.lottieAnimationView.playAnimation();

                //long duration = binding.lottieAnimationView.getAnimation().getDuration();
                //Log.e("duration","duration "+duration );


                binding.lottieAnimationView.addAnimatorListener(new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        Log.e("animation","animation end");
                        Intent intent;
                        if (iSFirstTime){
                            intent = new Intent(SplashScreenActivity.this,StartActivityPager.class);
                        }else {
                            intent = new Intent(SplashScreenActivity.this,MainActivity.class);
                        }
                        startActivity(intent);
                        finish();
                    }
                });
            }
        };

        // Start the background thread
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Run the Runnable on the background thread
                handler.post(runnable);
            }
        });
        thread.start();

        /*AlphaAnimation fadeIn = new AlphaAnimation(0,1);
        fadeIn.setDuration(1500);
        binding.rounded.startAnimation(fadeIn);
      */
    }
}