package com.chatapp.messenger.Adapter;


import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class ViewPageAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> fragments;
    private ArrayList<String> titles;
    private ArrayList<String> tags;
    public ViewPageAdapter(FragmentManager fs)
    {
        super(fs);
        this.fragments=new ArrayList<>();
        this.titles=new ArrayList<>();
        this.tags=new ArrayList<>();

    }

    @Override
    public Fragment getItem(int i) {
        return fragments.get(i);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    public void addFragment(Fragment fragment,String title ){
        fragments.add(fragment);
        titles.add(title);
        tags.add(title);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return super.getPageTitle(position);
    }

    public ArrayList<String> getTitles() {
        return titles;
    }

    public ArrayList<String> getTags() {
        return tags;
    }
}
