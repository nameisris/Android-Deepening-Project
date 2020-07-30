package com.example.ex06;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    LinearLayout drawerView;
    TabLayout tab; // 탭은 페이저에 대한 선택항목
    ViewPager pager; // 페이저는 탭의 선택에 따른 선택된 화면
    ArrayList<Fragment> array; // Fragment가 여러 개이므로, ArrayList에 넣어줌 (Fragment 형을 받음)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("카카오검색");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 왼쪽 상단 버튼 (홈버튼)
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu); // (왼쪽 상단) 버튼 아이콘 설정

        // 아이디
        drawerLayout = findViewById(R.id.drawerLayout); // activity_main.xml (서랍)
        drawerView = findViewById(R.id.drawerView); // drawer_view.xml (서랍의 내용물)
        tab = findViewById(R.id.tab); // tab 아이디
        pager = findViewById(R.id.pager); // pager 아이디

        // 새로운 탭 생성 및 텍스트 설정
        tab.addTab(tab.newTab().setText("블로그")); // 첫 번째 탭 (0번)
        tab.addTab(tab.newTab().setText("도서")); // 두 번째 탭 (1번)
        tab.addTab(tab.newTab().setText("지역")); // 세 번째 탭 (2번)
        // 코딩한 순서대로 탭의 순서가 결정됨

        // 탭 아이콘 설정
        tab.getTabAt(0).setIcon(R.drawable.ic_blog); // 첫 번째 탭 (0번 탭)의 아이콘을 ic_blog로 설정
        tab.getTabAt(1).setIcon(R.drawable.ic_book);
        tab.getTabAt(2).setIcon(R.drawable.ic_local);

        // ArrayList 생성 및 Fragment 삽입
        array = new ArrayList<>();
        array.add(new BlogFragment()); // ArrayList인 array에 프레그먼트인 BlogFragment를 넣음
        array.add(new BookFragment());
        array.add(new LocalFragment());

        // 페이저 어댑터 생성
        PagerAdapter ad = new PagerAdapter(getSupportFragmentManager());
        pager.setAdapter(ad);

        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tab)); // 드래그 해도 페이저가 바뀌도록
        // 탭 클릭 이벤트
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
    class PagerAdapter extends FragmentStatePagerAdapter{

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
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(drawerView); // 서랍인 drawerLayout을 openDrawer()를 이용하여, 서랍의 내용물 격인 drawerView를 열어줌

        }
        return super.onOptionsItemSelected(item);
    }
}