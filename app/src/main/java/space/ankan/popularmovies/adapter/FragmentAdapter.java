package space.ankan.popularmovies.adapter;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import space.ankan.popularmovies.activities.MainActivityFragment;

/**
 * Created by anurag on 19-Dec-15.
 */
public class FragmentAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> mFragments = new ArrayList<>();
    private final List<String> mFragmentTitles = new ArrayList<>();

    @Override
    public Parcelable saveState() {
        return super.saveState();
    }

    public FragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(int fragmentType, String title) {

        MainActivityFragment fragment = new MainActivityFragment();
        Bundle args = new Bundle();
        args.putInt(MainActivityFragment.FRAGMENT_KEY, fragmentType);
        fragment.setArguments(args);

        this.addFragment(fragment, title);

    }

    public void addFragment(Fragment fragment, String title){
        mFragments.add(fragment);
        mFragmentTitles.add(title);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitles.get(position);
    }
}

