package space.ankan.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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
    public static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    private static int pagesFetched;

    private String imageSize;
    private ImageAdapter adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public MainActivityFragment() {

    }

    @Override
    public void onStart() {
        super.onStart();
        pagesFetched = 0;
        if (adapter != null)
            adapter.clear();
        new FetchMovies().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);
        List<MoviesInformation> movieList = new ArrayList<>();
        /* fake data
        movieList.add(new MoviesInformation("/D6e8RJf2qUstnfkTslTXNTUAlT.jpg"));
        movieList.add(new MoviesInformation("/mSvpKOWbyFtLro9BjfEGqUw5dXE.jpg"));
        movieList.add(new MoviesInformation("/5JU9ytZJyR3zmClGmVm9q4Geqbd.jpg"));
        movieList.add(new MoviesInformation("/jjBgi2r5cRt36xF6iNUEhzscEcb.jpg"));
        movieList.add(new MoviesInformation("/g23cs30dCMiG4ldaoVNP1ucjs6.jpg"));
        movieList.add(new MoviesInformation("/l3tmn2WOAIgLyGP7zcsTYkl5ejH.jpg"));
        movieList.add(new MoviesInformation("/q0R4crx2SehcEEQEkYObktdeFy.jpg"));
        movieList.add(new MoviesInformation("/cWERd8rgbw7bCMZlwP207HUXxym.jpg"));
        movieList.add(new MoviesInformation("/A7HtCxFe7Ms8H7e7o2zawppbuDT.jpg"));
        movieList.add(new MoviesInformation("/kqjL17yufvn9OVLyXYpvtyrFfak.jpg"));
        movieList.add(new MoviesInformation("/vlTPQANjLYTebzFJM1G4KeON0cb.jpg"));
        movieList.add(new MoviesInformation("/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg"));
        movieList.add(new MoviesInformation("/69Cz9VNQZy39fUE2g0Ggth6SBTM.jpg"));*/

        adapter = new ImageAdapter(getActivity(), movieList);
        gridView.setAdapter(adapter);

        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                FetchMovies fetchMovies = new FetchMovies();
                if (firstVisibleItem + visibleItemCount == pagesFetched * 20 && totalItemCount > 0) {
                    Toast.makeText(getActivity(), "First Visible Item = " + firstVisibleItem + " Visible Item Count = " + visibleItemCount + " Total Item Count = " + totalItemCount, Toast.LENGTH_SHORT).show();
                    fetchMovies.execute();
                }
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "Position: " + position + " id: " + id, Toast.LENGTH_SHORT).show();
            }
        });

        if (pagesFetched == 0)
            new FetchMovies().execute();
        return rootView;
    }


    public class FetchMovies extends AsyncTask<String, Void, List<MoviesInformation>> {
        final String BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
        final String QUERY_PARAM = "q";
        final String SORT_PARAM = "sort_by";
        final String API_KEY_PARAM = "api_key";
        final String PAGE_PARAM = "page";
        List<MoviesInformation> movieList;

        @Override
        protected List<MoviesInformation> doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            pagesFetched++;

            String sortBy = "popularity.desc";

            try {
                String page = String.valueOf(pagesFetched);
                Log.v(LOG_CAT, "fetching page " + page + " of 1000");

                Uri uri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, sortBy)
                        .appendQueryParameter(PAGE_PARAM, page)
                        .appendQueryParameter(API_KEY_PARAM, BuildConfig.API_KEY)
                        .build();

                URL url = new URL(uri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream == null)
                    return null;

                StringBuffer buffer = new StringBuffer();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;

                while ((line = reader.readLine()) != null)
                    buffer.append(line + "\n");

                if (buffer.length() == 0)
                    return null;

                movieList = getMovieInformationFromJson(buffer.toString());

            } catch (java.io.IOException e) {
                Log.e(LOG_CAT, "Please check your Internet Connection");
                pagesFetched--;

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

            if (pagesFetched == 1 && adapter.getCount() == 20)
                return;

            if (adapter.getCount() > (pagesFetched-1)*20) {
                Log.e(LOG_CAT, "Possible duplicate values fetched. Pages Fetched = " + (pagesFetched - 1) + " Total items in data set = " + adapter.getCount());
                return;
            }

            adapter.addAll(moviesInfo);

        }

        private List<MoviesInformation> getMovieInformationFromJson(String json) throws JSONException {

            List<MoviesInformation> moviesInformation = new ArrayList<>();
            JSONObject movies = new JSONObject(json);
            JSONArray results = movies.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {

                String moviePosterUrl = results.getJSONObject(i).getString("poster_path");
                MoviesInformation movieInfo = new MoviesInformation(moviePosterUrl);
                moviesInformation.add(movieInfo);
            }

            return moviesInformation;
        }
    }
}
