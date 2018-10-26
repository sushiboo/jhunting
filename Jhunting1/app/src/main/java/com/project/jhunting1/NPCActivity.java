package com.project.jhunting1;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.project.jhunting1.Adapters.ViewPagerAdapter;
import com.project.jhunting1.Views.FragmentNPCJobApplication;
import com.project.jhunting1.Views.FragmentNPCJobs;
import com.project.jhunting1.Views.FragmentNPCSearchPlayer;
import com.project.jhunting1.Views.FragmentNpcProfile;

public class NPCActivity extends AppCompatActivity{

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle npcInfo = getIntent().getExtras();

        setContentView(R.layout.activity_npc);
        tabLayout = findViewById(R.id.npctablayout);
        viewPager = findViewById(R.id.npcviewpager);

        FragmentNpcProfile fragmentNpcProfile = new FragmentNpcProfile();
        FragmentNPCJobs fragmentNPCJobs = new FragmentNPCJobs();
        FragmentNPCJobApplication fragmentNPCJobApplication = new FragmentNPCJobApplication();
        FragmentNPCSearchPlayer fragmentNPCSearchPlayer = new FragmentNPCSearchPlayer();

        if (npcInfo!=null) {
            fragmentNpcProfile.setArguments(npcInfo);
            fragmentNPCJobs.setArguments(npcInfo);
            fragmentNPCJobApplication.setArguments(npcInfo);
            fragmentNPCSearchPlayer.setArguments(npcInfo);
        }

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.AddFragment(fragmentNpcProfile, "My Profile");
        adapter.AddFragment(fragmentNPCJobs, "My Jobs");
        adapter.AddFragment(fragmentNPCJobApplication, "Job Applications");
        adapter.AddFragment(fragmentNPCSearchPlayer, "Search Players");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

    }
}
