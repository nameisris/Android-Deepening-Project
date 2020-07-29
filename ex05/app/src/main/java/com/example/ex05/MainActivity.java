package com.example.ex05;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    TabLayout tab;
    ArrayList<Fragment> fragments; // 프레그먼트를 ArrayList에 넣음
    PagerAdapter ad;
    ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 탭 아이디 받기
        tab = findViewById(R.id.tab);
        // 탭 추가
        tab.addTab(tab.newTab().setText("도서검색")); // 새로운 탭을 생성 및 add
        tab.addTab(tab.newTab().setText("영화검색"));
        tab.addTab(tab.newTab().setText("지역검색"));
        // 탭 리스너
        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) { // 탭이 selected 됐을 때
                pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        // 탭 아이콘 설정
        tab.getTabAt(0).setIcon(R.drawable.ic_book); // 0번째 탭에는 ic_book을 icon으로 set
        tab.getTabAt(1).setIcon(R.drawable.ic_movie);
        tab.getTabAt(2).setIcon(R.drawable.ic_map);

        // 프레그먼트 생성
        fragments = new ArrayList<>();
        // 프레그먼트 추가
        fragments.add(new BookFragment()); // fragments에 BookFragment 더해줌
        fragments.add(new MovieFragment());
        fragments.add(new LocalFragment());
        // 탭 추가와 프레그먼트 추가의 순서가 각각 똑같아야 함

        // 어댑터 생성
        ad = new PagerAdapter(getSupportFragmentManager(), 0);
        // 어댑터를 Pager와 adapt
        pager = findViewById(R.id.pager);
        pager.setAdapter(ad);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tab)); // pager를 바꿀 때마다 드래그로 바꾸면 같이 바뀜

    }

    // 어댑터
    class PagerAdapter extends FragmentStatePagerAdapter{

        public PagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size(); // add를 2번 했으므로 2만큼을 반환
        }
    }
}