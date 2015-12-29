package space.ankan.popularmovies.activities;


import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import space.ankan.popularmovies.BuildConfig;
import space.ankan.popularmovies.R;
import space.ankan.popularmovies.adapter.MovieAdapter;
import space.ankan.popularmovies.data.MovieContract;
import space.ankan.popularmovies.data.MovieInfo;
import space.ankan.popularmovies.retrofit.TheMovieDb;
import space.ankan.popularmovies.retrofit.model.Raw;
import space.ankan.popularmovies.retrofit.model.Results;

public class MainActivityFragment extends Fragment implements Callback<Raw> {
    private static final String LOG_CAT = MainActivityFragment.class.getSimpleName();
    public static final String FRAGMENT_KEY = "fragment";

    private String mSortBy;
    private RecyclerView mRecyclerView;
    private MovieAdapter adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        ArrayList<MovieInfo> movieList = new ArrayList<>();

        if (adapter != null)
            movieList = adapter.getItems();

        outState.putParcelableArrayList("movies", movieList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void setRetainInstance(boolean retain) {
        super.setRetainInstance(true);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        setSorter();

        if (savedInstanceState != null) {

            ArrayList<MovieInfo> movieList = savedInstanceState.getParcelableArrayList("movies");
            if (movieList != null) {
                if (adapter == null)
                    adapter = new MovieAdapter(getActivity(), movieList);
                else
                    adapter.addAll(movieList);
            }
        }

        super.onViewStateRestored(savedInstanceState);

    }

    private void setSorter() {
        Bundle bundle = getArguments();

        switch (bundle.getInt(FRAGMENT_KEY)) {
            case MainActivity.FRAGMENT_MOST_POPULAR:
                mSortBy = getResources().getString(R.string.pref_sort_by_popularity);
                break;
            case MainActivity.FRAGMENT_HIGHEST_RATED:
                mSortBy = getResources().getString(R.string.pref_sort_by_rating);
                break;
            case MainActivity.FRAGMENT_FAVOURITES:
                mSortBy = "favt";
                break;
            default:
                mSortBy = null;
        }
    }

    private void setupRecyclerView() {
        this.setupRecyclerView(null);
    }

    private void setupRecyclerView(ArrayList<MovieInfo> movies) {
        setSorter();
        if (movies == null)
            movies = new ArrayList<MovieInfo>();
        adapter = new MovieAdapter(getActivity(), movies);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mRecyclerView.getContext(), Integer.parseInt(getActivity().getResources().getString(R.string.span_count))));

        if (getArguments().getInt(FRAGMENT_KEY) != MainActivity.FRAGMENT_FAVOURITES)
            mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    fetchMovies();
                    return false;
                }
            });
    }


    public MainActivityFragment() {

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        fetchMovies();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRecyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_main, container, false);
        setupRecyclerView();
        mRecyclerView.setAdapter(adapter);
        fetchMovies();
        return mRecyclerView;
    }

    private synchronized void fetchMovies() {

        if (mSortBy == "favt")
            fetchFavourites();

        else if (adapter.getItemCount() < 20)
            TheMovieDb.getApiClient().getMovieList(mSortBy, "1", BuildConfig.API_KEY).enqueue(this);

    }


    @Override
    public void onResponse(Response<Raw> response, Retrofit retrofit) {

        Log.e(LOG_CAT, "[response] " + response.raw());
        Results[] results = response.body().getResults();
        List<MovieInfo> movies = extractMovieInformation(results);
        adapter.addAll(movies);

        if (!movies.isEmpty() && MainActivity.FRAGMENT_MOST_POPULAR == (getArguments().getInt(FRAGMENT_KEY))) {
            ((MainActivity) getActivity()).addInitialDetailFragment(movies.get(0));
            mRecyclerView.setOnTouchListener(null);
        }

    }

    public boolean fetchFavourites() {

        if (getActivity() == null || !"favt".equals(mSortBy)) return false;

        Cursor c = getActivity().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, new String[]{MovieContract.MovieEntry._ID, MovieContract.MovieEntry.COLUMN_MOVIE_ID, MovieContract.MovieEntry.COLUMN_OVERVIEW, MovieContract.MovieEntry.COLUMN_POSTER_PATH, MovieContract.MovieEntry.COLUMN_RELEASE_DATE, MovieContract.MovieEntry.COLUMN_TITLE, MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE}, null, null, null);
        ArrayList<MovieInfo> movies = new ArrayList<>();

        if (c.moveToFirst()) {

            do {
                movies.add(new MovieInfo(c.getString(1), c.getString(5), c.getString(3), c.getString(4), c.getString(6), c.getString(2), true));
            } while (c.moveToNext());
        }

        adapter.clear();
        adapter.addAll(movies);
        c.close();
        return (!movies.isEmpty());

    }

    private List<MovieInfo> extractMovieInformation(Results[] results) {
        List<MovieInfo> movies = new ArrayList<>();
        for (int i = 0; i < results.length; i++) {
            Results res = results[i];
            movies.add(new MovieInfo(
                    res.getId(),
                    res.getTitle(),
                    res.getPoster_path(),
                    res.getRelease_date(),
                    res.getVote_average(),
                    res.getOverview(),
                    false));
        }

        return movies;
    }


    @Override
    public void onFailure(Throwable t) {
        ((MainActivity) getActivity()).onFailedToConnect();

    }

}
