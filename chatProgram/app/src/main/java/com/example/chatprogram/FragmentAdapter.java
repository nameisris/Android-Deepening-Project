package com.example.chatprogram;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

class FragmentAdapter extends FragmentPagerAdapter {

    // ViewPager에 들어갈 Fragment들을 담을 리스트
    private ArrayList<Fragment> fragments = new ArrayList<>();

    // 필수 생성자
    FragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                PersonFragment personFragment = new PersonFragment();
                return personFragment;
            case 1:
                MessageFragment messageFragment = new MessageFragment();
                return  messageFragment;
            case 2:
                SettingFragment settingFragment = new SettingFragment();
                return settingFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    // List에 Fragment를 담을 함수
    void addItem(Fragment fragment) {
        fragments.add(fragment);
    }
}