package com.besideu.source;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MainFragmentPagerAdapter extends FragmentPagerAdapter {

	private ArrayList<Fragment> mFragmentList;
	
	public MainFragmentPagerAdapter(FragmentManager fm) 
	{
		super(fm);
	}
	
	public MainFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragmentList) 
	{
		super(fm);
		this.mFragmentList = fragmentList;
	}

	@Override
	public Fragment getItem(int arg0) 
	{
		return mFragmentList.get(arg0);
	}

	@Override
	public int getCount() 
	{
		return mFragmentList.size();
	}

	@Override
	public int getItemPosition(Object object) 
	{
		return super.getItemPosition(object);
	}
	

}
