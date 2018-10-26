package com.project.jhunting1;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.project.jhunting1.Adapters.ViewPagerAdapter;
import com.project.jhunting1.Views.FragmentLogin;
import com.project.jhunting1.Views.FragmentRegister;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FragmentRegister fragmentRegister = new FragmentRegister();


    public MainActivity(){

    }

    public MainActivity(String type){
        fragmentRegister.setType(type);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tabLayout = findViewById(R.id.maintablayout);
        viewPager = findViewById(R.id.mainviewpager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.AddFragment(new FragmentLogin(), "Login");
        adapter.AddFragment(fragmentRegister, "Register");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        Bundle registerRoleType = getIntent().getExtras();
        if (registerRoleType != null) {
            String registrationRole = registerRoleType.getString("Role");
            if (!registrationRole.equals("")) {
                fragmentRegister.setType(registrationRole);
                viewPager.setCurrentItem(1);
            }
        }


    }
}
