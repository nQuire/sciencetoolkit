package org.greengin.sciencetoolkit.common.ui.base;


import org.greengin.sciencetoolkit.common.ui.base.SettingsControlledActivity;
import org.greengin.sciencetoolkit.common.ui.base.widgets.StkViewPager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;

public abstract class SwipeActivity extends SettingsControlledActivity implements ActionBar.TabListener {
	SectionsPagerAdapter mSectionsPagerAdapter;

	StkViewPager mViewPager;
	
	public SwipeActivity(boolean hasParent) {
		super(-1, hasParent);
	}
	
	abstract public int getContentViewLayoutId();
	abstract public int getViewPagerLayoutId();
	abstract public int getTabCount();
	abstract public CharSequence getTabTitle(int position);
	abstract public Fragment createTabFragment(int position);
	abstract public int getOnResumeTab();
	abstract public void setOnResumeTab(int position);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getContentViewLayoutId());

		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		mViewPager = (StkViewPager) findViewById(getViewPagerLayoutId());
		mViewPager.setAdapter(mSectionsPagerAdapter);

		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				setOnResumeTab(position);
				actionBar.setSelectedNavigationItem(position);
			}
		});

		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab().setText(mSectionsPagerAdapter.getPageTitle(i)).setTabListener(this));
		}
	}
	
	public void setPagingEnabled(boolean enabled) {
		mViewPager.setPagingEnabled(enabled);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (getOnResumeTab() >= 0) {
			getSupportActionBar().setSelectedNavigationItem(getOnResumeTab());
		}
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		if (mViewPager.getCurrentItem() != tab.getPosition()) {
			mViewPager.setCurrentItem(tab.getPosition());
		}
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	public void setTab(int position) {
		getSupportActionBar().setSelectedNavigationItem(position);
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		Fragment[] fragments;

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);

			fragments = new Fragment[getTabCount()];
		}

		@Override
		public Fragment getItem(int position) {
			if (fragments[position] == null) {
				fragments[position] = createTabFragment(position);
			}
			return fragments[position];
		}

		@Override
		public int getCount() {
			return getTabCount();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return getTabTitle(position);
		}
	}
}
