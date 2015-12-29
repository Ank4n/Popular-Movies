package space.ankan.popularmovies.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import space.ankan.popularmovies.R;
import space.ankan.popularmovies.Utilities;
import space.ankan.popularmovies.adapter.FragmentAdapter;
import space.ankan.popularmovies.data.MovieInfo;

public class MainActivity extends AppCompatActivity {

    public static final int FRAGMENT_HIGHEST_RATED = 1;
    public static final int FRAGMENT_MOST_POPULAR = 2;
    public static final int FRAGMENT_FAVOURITES = 3;
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private static final String PAGER_STATE = "state";
    private static ViewPager mViewPager;
    private boolean mTwoPane;
    private Boolean welcome;

    @Bind(R.id.tabs)
    TabLayout tabs;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Utilities.checkWritePermissions(this);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(toolbar);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        if (mViewPager != null) setupViewPager(mViewPager);

        mViewPager.setSaveEnabled(true);
        mViewPager.setOffscreenPageLimit(2);

        tabs.setupWithViewPager(mViewPager);

        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new WelcomeFragment(), DETAILFRAGMENT_TAG)
                        .commit();
                welcome = true;
            }
        } else mTwoPane = false;

        if (savedInstanceState != null) {
            mViewPager.setCurrentItem(savedInstanceState.getInt(PAGER_STATE));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Utilities.REQUEST_WRITE_STORAGE == requestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Utilities.init();
            } else
                Utilities.permissionErrorToast(this);
        }
    }

    public void addInitialDetailFragment(MovieInfo movie) {
        if (welcome!= null && welcome)
            this.addDetailFragment(movie);
    }

    private void addDetailFragment(MovieInfo movie) {

        Bundle args = new Bundle();
        args.putParcelable(DetailActivityFragment.MOVIE_INFO, movie);
        DetailActivityFragment frag = new DetailActivityFragment();
        frag.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.movie_detail_container, frag, DETAILFRAGMENT_TAG)
                .commit();

        welcome = false;

    }

    public void onItemClick(MovieInfo movie) {

        if (mTwoPane)
            addDetailFragment(movie);

        else {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(DetailActivityFragment.MOVIE_INFO, movie).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (welcome != null && !welcome)
            welcome = null;
        refreshFavourites();
    }

    private void setupViewPager(ViewPager viewPager) {
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());
        adapter.addFragment(FRAGMENT_MOST_POPULAR, getResources().getString(R.string.tab_most_popular));
        adapter.addFragment(FRAGMENT_HIGHEST_RATED, getResources().getString(R.string.tab_highest_rated));
        adapter.addFragment(FRAGMENT_FAVOURITES, getResources().getString(R.string.tab_favourites));
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        int state = mViewPager.getCurrentItem();
        outState.putInt(PAGER_STATE, state);
        super.onSaveInstanceState(outState);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public boolean refreshFavourites() {
        MainActivityFragment favourites = (MainActivityFragment) ((FragmentAdapter) mViewPager.getAdapter()).getItem(2);
        if (favourites != null)
            return favourites.fetchFavourites();

        return false;
    }

    public void onFailedToConnect() {
        if (welcome == null) {
            Toast.makeText(this, "Could not connect to internet", Toast.LENGTH_SHORT).show();
            if (refreshFavourites())
                mViewPager.setCurrentItem(2);
            welcome = false;

        } else if (welcome)
            ((WelcomeFragment) getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG)).onFailedToConnect();

    }


}
