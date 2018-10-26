package com.project.jhunting1;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.project.jhunting1.Adapters.ViewPagerAdapter;
import com.project.jhunting1.Views.FragmentAllJobs;
import com.project.jhunting1.Views.FragmentPlayerProfile;
import com.project.jhunting1.Views.FragmentPlayerJobApplication;

public class PlayerActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        Bundle playerInfo = getIntent().getExtras();

        tabLayout = findViewById(R.id.playertablayout);
        viewPager = findViewById(R.id.playerviewpager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        FragmentPlayerProfile fragmentPlayerProfile = new FragmentPlayerProfile();
        FragmentAllJobs fragmentAllJobs = new FragmentAllJobs();
        FragmentPlayerJobApplication fragmentPlayerJobApplication = new FragmentPlayerJobApplication();

        if (playerInfo != null){
            fragmentPlayerProfile.setArguments(playerInfo);
            fragmentAllJobs.setArguments(playerInfo);
            fragmentPlayerJobApplication.setArguments(playerInfo);
        }

        adapter.AddFragment(fragmentPlayerProfile, "My Profile");
        adapter.AddFragment(fragmentAllJobs, "My Jobs");
        adapter.AddFragment(fragmentPlayerJobApplication, "Job Applications");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}
