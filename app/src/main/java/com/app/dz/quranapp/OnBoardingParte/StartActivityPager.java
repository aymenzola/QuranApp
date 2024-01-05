package com.app.dz.quranapp.OnBoardingParte;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.app.dz.quranapp.LocationParte.LocationActivity;
import com.app.dz.quranapp.R;
import com.app.dz.quranapp.Util.SharedPreferenceManager;
import com.app.dz.quranapp.databinding.OnboardingActivityBinding;

import java.util.ArrayList;


public class StartActivityPager extends AppCompatActivity {
    private final ArrayList<ModuleFragments> list = new ArrayList<>();
    private OnboardingActivityBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OnboardingActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getColor(R.color.blan));
        }

        list.add(new ModuleFragments("الثالثة", StartFragment.newInstance(0)));
        list.add(new ModuleFragments("الثانية", StartFragment.newInstance(1)));
        list.add(new ModuleFragments("الأولى", StartFragment.newInstance(2)));

        AdapterStartFragments adapterPagerForSign = new AdapterStartFragments(getSupportFragmentManager(), list);
        binding.viewpager.setAdapter(adapterPagerForSign);
        binding.viewpager.setCurrentItem(2);

        binding.pageimage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (binding.viewpager.getCurrentItem() == 2) {
                    binding.viewpager.setCurrentItem(1);
                } else if (binding.viewpager.getCurrentItem() == 1) {
                    binding.viewpager.setCurrentItem(0);
                }
                return true;
            }
        });


        binding.btnmainpage.setOnClickListener(v -> {
            int page = binding.viewpager.getCurrentItem();
            if (page == 0) {
                binding.viewpager.setVisibility(View.GONE);
                binding.btnmainpage.setVisibility(View.GONE);
                binding.dotsLinear.setVisibility(View.GONE);
                binding.pageimage.setAnimation(R.raw.anim_04);
                binding.pageimage.playAnimation();

                binding.pageimage.addAnimatorListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        Log.e("animation", "animation end");
                        SharedPreferenceManager.getInstance(StartActivityPager.this).changeFirstTimeValue();
                        Intent intent = new Intent(StartActivityPager.this, LocationActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

            } else if (page == 1) {
                binding.viewpager.setCurrentItem(0);
            } else if (page == 2) {
                binding.viewpager.setCurrentItem(1);
            }
        });

        binding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                addDotsIndicator(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    public void addDotsIndicator(int position) {

        if (position == 0)
            binding.pageimage.setAnimation(R.raw.anim_03);
        if (position == 1)
            binding.pageimage.setAnimation(R.raw.anim_02);
        if (position == 2)
            binding.pageimage.setAnimation(R.raw.anim_1);

        binding.pageimage.playAnimation();

        if (position == 2) {
            binding.btnmainpage.setText("التالي");
            binding.dots0.setBackgroundResource(R.drawable.shapebleu_ripple);
            binding.dots1.setBackgroundResource(R.drawable.dots_shape_white);
            binding.dots2.setBackgroundResource(R.drawable.dots_shape_white);
        }
        if (position == 1) {
            binding.btnmainpage.setText("التالي");
            binding.dots0.setBackgroundResource(R.drawable.dots_shape_white);
            binding.dots1.setBackgroundResource(R.drawable.shapebleu_ripple);
            binding.dots2.setBackgroundResource(R.drawable.dots_shape_white);
        }
        if (position == 0) {
            binding.btnmainpage.setText("إنطلق بسم الله");
            binding.dots0.setBackgroundResource(R.drawable.dots_shape_white);
            binding.dots1.setBackgroundResource(R.drawable.dots_shape_white);
            binding.dots2.setBackgroundResource(R.drawable.shapebleu_ripple);
        }
    }

}
