package space.ankan.popularmovies;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private static final String LOG_CAT = MainActivityFragment.class.getSimpleName();
    private static String sortBy;
    private static boolean sortChanged = false;
    private static boolean fetching = false;

    private ImageAdapter adapterByPopularity;
    private ImageAdapter adapterByRating;
    private ImageAdapter adapter;
    private GridView mGridView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        ArrayList<MoviesInformation> movieList = new ArrayList<>();

        if (adapter != null)
            movieList = adapter.getMovieList();

        outState.putParcelableArrayList("movies", movieList);
        outState.putInt("pages", adapter.pagesFetched);
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
        if (savedInstanceState != null) {

            ArrayList<MoviesInformation> movieList = savedInstanceState.getParcelableArrayList("movies");
            adapter.pagesFetched = savedInstanceState.getInt("pages");
            if (movieList != null) {
                if (adapter == null)
                    adapter = new ImageAdapter(getActivity(), movieList);
                else
                    adapter.addAll(movieList);
            }
        }
        resetAdapter();

        super.onViewStateRestored(savedInstanceState);

    }

    private void resetAdapter() {
        if (sortChanged) {
            String sorterText = getContext().getString(R.string.pref_sort_by_popularity).equals(sortBy) ? "Popularity" : "Rating";
            Toast.makeText(getContext(), "Showing movies based on " + sorterText, Toast.LENGTH_SHORT).show();
            sortChanged = false;
            mGridView.setAdapter(adapter);
        }
        new FetchMovies().execute();
    }

    private void checkSortPreference() {

        Context context = getContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String sortByKey = context.getString(R.string.pref_sort_by_key);
        String sortByValue = prefs.getString(sortByKey,
                context.getString(R.string.pref_sort_by_popularity));

        if (getContext().getString(R.string.pref_sort_by_popularity).equals(sortByValue))
            adapter = adapterByPopularity;
        else
            adapter = adapterByRating;

        if (sortBy == null || (!sortBy.equals(sortByValue))) {
            sortBy = sortByValue;
            sortChanged = true;
        }
    }


    public MainActivityFragment() {

    }

    @Override
    public void onStart() {
        super.onStart();
//        pagesFetched = 0;
//        if (adapter != null)
//            adapter.clear();
//       new FetchMovies().execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkSortPreference();
        resetAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mGridView = (GridView) rootView.findViewById(R.id.gridview_movies);

        adapterByPopularity = new ImageAdapter(getActivity(), new ArrayList<MoviesInformation>());
        adapterByRating = new ImageAdapter(getActivity(), new ArrayList<MoviesInformation>());
        checkSortPreference();

        mGridView.setAdapter(adapter);

        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                FetchMovies fetchMovies = new FetchMovies();
                if (firstVisibleItem + visibleItemCount == adapter.pagesFetched * 20 && totalItemCount > 0 && !fetching) {
                    fetchMovies.execute();
                }
            }
        });

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                MoviesInformation movieInfo = adapter.getItem(position);
                intent.putExtra("movie", movieInfo);
                startActivity(intent);
            }
        });

        return rootView;
    }


    public class FetchMovies extends AsyncTask<String, Void, List<MoviesInformation>> {
        final String BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
        final String SORT_PARAM = "sort_by";
        final String API_KEY_PARAM = "api_key";
        final String PAGE_PARAM = "page";
        List<MoviesInformation> movieList;

        @Override
        protected List<MoviesInformation> doInBackground(String... params) {
            fetching = true;
            HttpURLConnection urlConnection;
            BufferedReader reader;

            if (adapter.pagesFetched >= 10)
                return null;

            adapter.pagesFetched++;

            try {
                String page = String.valueOf(adapter.pagesFetched);
                Log.v(LOG_CAT, "fetching page " + page + " of 10 with " + sortBy + " as the sorting parameter");

                Uri uri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, sortBy)
                        .appendQueryParameter(PAGE_PARAM, page)
                        .appendQueryParameter(API_KEY_PARAM, BuildConfig.API_KEY)
                        .build();
                Log.v(LOG_CAT, uri.toString());
                Log.v(LOG_CAT, "Adapter Count: [" + adapter.getCount() + "]");
                URL url = new URL(uri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream == null)
                    return null;

                StringBuilder builder = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;

                while ((line = reader.readLine()) != null)
                    builder.append(line).append("\n");

                if (builder.length() == 0)
                    return null;

                movieList = getMovieInformationFromJson(builder.toString());

            } catch (java.io.IOException e) {
                Log.e(LOG_CAT, "Please check your Internet Connection");
                adapter.pagesFetched--;

            } catch (JSONException e) {
                e.printStackTrace();
            }


            return movieList;
        }


        @Override
        protected void onPostExecute(List<MoviesInformation> moviesInfo) {
            super.onPostExecute(moviesInfo);

            if (moviesInfo == null || moviesInfo.isEmpty())
                return;

            if (adapter.pagesFetched == 1 && adapter.getCount() == 20)
                return;

            if (adapter.getCount() > (adapter.pagesFetched - 1) * 20) {
                Log.e(LOG_CAT, "Possible duplicate values fetched. Pages Fetched = " + (adapter.pagesFetched - 1) + " Total items in data set = " + adapter.getCount());
                return;
            }

            adapter.addAll(moviesInfo);
            fetching = false;

        }

        private List<MoviesInformation> getMovieInformationFromJson(String json) throws JSONException {

            List<MoviesInformation> moviesInformation = new ArrayList<>();
            JSONObject movies = new JSONObject(json);
            JSONArray results = movies.getJSONArray(MoviesInformation.RESULT_LIST);

            for (int i = 0; i < results.length(); i++) {

                JSONObject movie = results.getJSONObject(i);

                MoviesInformation movieInfo = new MoviesInformation(
                        movie.getString(MoviesInformation.TITLE),
                        movie.getString(MoviesInformation.POSTER_PATH),
                        movie.getString(MoviesInformation.RELEASE_DATE),
                        movie.getString(MoviesInformation.VOTE_COUNT),
                        movie.getString(MoviesInformation.SYNOPSIS));

                moviesInformation.add(movieInfo);
            }

            return moviesInformation;
        }


    }
}
