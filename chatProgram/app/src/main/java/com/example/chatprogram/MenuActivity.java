package com.example.chatprogram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

public class MenuActivity extends AppCompatActivity {
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // 액션바
        getSupportActionBar().setTitle("채팅 프로그램");

        // 뷰페이저
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager()));
        viewPager.setCurrentItem(0); // 현재 ViewPager를 0번째 프래그먼트로 설정 (초기화면)

        // 탭 3개 생성
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.newTab();
        tabLayout.newTab();
        tabLayout.newTab();

        // 어댑터
        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(fragmentAdapter); // ViewPager와 FragmentAdapter 연결

        // ViewPager에 fragmentAdapter를 통해 Fragment 추가
        PersonFragment personFragment = new PersonFragment();
        MessageFragment messageFragment = new MessageFragment();
        SettingFragment settingFragment = new SettingFragment();
        fragmentAdapter.addItem(personFragment);
        fragmentAdapter.addItem(messageFragment);
        fragmentAdapter.addItem(settingFragment);
        fragmentAdapter.notifyDataSetChanged();

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}