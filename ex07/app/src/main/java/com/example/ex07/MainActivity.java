package com.example.ex07;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawerLayout; // 서랍 (activity_main)
    LinearLayout drawerView; // 서랍 내용물 (drawer_view)
    TabLayout tab; // 탭
    ViewPager pager; // 뷰 페이저
    ArrayList<Fragment> array = new ArrayList<>(); // Fragment를 넣어줄 ArrayList 생성

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 상단 액션바 설정
        getSupportActionBar().setTitle("카카오 검색"); // 액션바 타이틀 텍스트 값 설정
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 액션바 왼쪽 상단 아이콘 설정
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu); // 액션바 왼쪽 상단 아이콘 변경

        // 드로워 레이아웃 설정
        drawerLayout = findViewById(R.id.drawerLayout);
        drawerView = findViewById(R.id.drawerView);

        // 탭 및 뷰 페이저
        tab = findViewById(R.id.tab);
        pager = findViewById(R.id.pager);

        // 탭 추가
        tab.addTab(tab.newTab().setText("블로그 검색"));
        tab.addTab(tab.newTab().setText("도서 검색"));
        tab.addTab(tab.newTab().setText("지역 검색"));

        // 탭 아이콘 설정
        tab.getTabAt(0).setIcon(R.drawable.ic_blog); // 첫 번째 탭 (0번 탭)의 아이콘을 ic_blog로 설정
        tab.getTabAt(1).setIcon(R.drawable.ic_book);
        tab.getTabAt(2).setIcon(R.drawable.ic_map);

        // ArrayList인 array에 Fragment들을 add
        array.add(new BlogFragment());
        array.add(new BookFragment());
        array.add(new LocalFragment());

        // 뷰 페이저에 adapt
        PagerAdapter ad = new PagerAdapter(getSupportFragmentManager());
        pager.setAdapter(ad);

        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tab)); // 드래그 해도 페이저가 바뀌도록 설정
        // 탭 이벤트 설정 (탭을 바꿀 때마다 페이저가 바뀌도록)
        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() { // Tab이 Selected 될 때마다
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition()); // Tab이 Selected 될 때마다 포지션을 바꿔줌 (페이저 값이 바뀜)
            }


            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    // 페이저 어댑터 정의
    class PagerAdapter extends FragmentStatePagerAdapter {

        public PagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return array.get(position);
        }

        @Override
        public int getCount() {
            return array.size();
        }
    }

    // 메인 액티비티 상의 선택된 아이템
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home: // 액션바 왼쪽 상단 버튼을 선택했을 경우
                drawerLayout.openDrawer(drawerView); // 서랍인 drawerLayout(activity_main)에서 내용물인 drawerView(drawer_view)를 open
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}