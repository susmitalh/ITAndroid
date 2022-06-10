package com.locatocam.app.views.createrolls;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.locatocam.app.R;
import com.locatocam.app.views.createrolls.sound.LocalAudioFragment;
import com.locatocam.app.views.createrolls.sound.WebAudioFragment;


// this is the class which will add the selected soung to the created video
public class ListSoundWebActivity extends AppCompatActivity {


    private static final int NUM_PAGES = 2;
    public static ViewPager2 viewPager;
    private FragmentStateAdapter pagerAdapter;
    private String[] titles = new String[]{"Trending","Browse"};
    ImageButton back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_audio_main);
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        viewPager = findViewById(R.id.pager);
        viewPager.setUserInputEnabled(false);
        pagerAdapter = new MyPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        TabLayout tabLayout =( TabLayout) findViewById(R.id.tablayout);
        new TabLayoutMediator(tabLayout, viewPager,(tab, position) -> tab.setText(titles[position])).attach();

    }

    public void trimAndSave(String audioname,String final_uri,String audio_start) {
        Intent intent = new Intent();
        intent.putExtra("sound_name",audioname);
        intent.putExtra("sound_uri", final_uri);
        intent.putExtra("start", String.valueOf(audio_start));
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private class MyPagerAdapter extends FragmentStateAdapter {

        public MyPagerAdapter(FragmentActivity fa) {
            super(fa);
        }


        @Override
        public Fragment createFragment(int pos) {
            switch (pos) {
                case 0: {
                    return new WebAudioFragment();
                }
                case 1: {
                    return new LocalAudioFragment();
                }
                default:
                    return new WebAudioFragment();
            }
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }


    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
// If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.d
            super.onBackPressed();
        } else {
// Otherwise, select the previous step.
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }


}