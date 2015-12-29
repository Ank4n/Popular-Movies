package space.ankan.popularmovies.activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import space.ankan.popularmovies.BuildConfig;
import space.ankan.popularmovies.R;
import space.ankan.popularmovies.Utilities;
import space.ankan.popularmovies.data.MovieContract;
import space.ankan.popularmovies.data.MovieInfo;
import space.ankan.popularmovies.data.Review;
import space.ankan.popularmovies.data.Trailer;
import space.ankan.popularmovies.retrofit.TheMovieDb;
import space.ankan.popularmovies.retrofit.model.Raw;
import space.ankan.popularmovies.retrofit.model.Results;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements Callback<Raw> {

    private static final String LOG_CAT = MainActivityFragment.class.getSimpleName();
    public static final String MOVIE_INFO = "movie";

    private MovieInfo mMovieInfo;

    @Nullable
    @Bind(R.id.movie_poster)
    ImageView mMoviePoster;

    @Nullable
    @Bind(R.id.favourite_button)
    FloatingActionButton mFavouriteButton;

    @Nullable
    @Bind(R.id.movie_title)
    TextView movieTitle;

    @Bind(R.id.vote_average)
    TextView mVoteAverage;

    @Bind(R.id.release_date)
    TextView mReleaseDate;

    @Bind(R.id.synopsis)
    TextView mSynopsis;

    @Bind(R.id.vote_average_bar)
    RatingBar mRatingBar;

    @Bind(R.id.trailer_card)
    CardView mTrailerCard;

    @Bind(R.id.trailer)
    TextView mTrailerDesc;

    @Bind(R.id.trailer_next)
    ImageView mTrailerNext;

    @Bind(R.id.trailer_prev)
    ImageView mTrailerPrevious;

    @Bind(R.id.review)
    TextView mReviewText;

    @Bind(R.id.review_next)
    ImageView mReviewNext;

    @Bind(R.id.review_prev)
    ImageView mReviewPrevious;

    @Bind(R.id.trailer_layout)
    LinearLayout mTrailerLayout;

    @Bind(R.id.trailer_title)
    TextView mTrailerTitle;

    @Bind(R.id.review_layout)
    LinearLayout mReviewLayout;

    @Bind(R.id.review_title)
    TextView mReviewTitle;

    private ShareActionProvider mShareActionProvider;
    private ArrayList<Trailer> mTrailers;
    private ArrayList<Review> mReviews;
    private int currTrailers;
    private int currReview;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, rootView);

        setHasOptionsMenu(true);
        fillBasicViews();
        hideTrailersAndReviews();
        fetchTrailersAndReviews();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_detail, menu);
        MenuItem item = menu.findItem(R.id.menu_share_trailer);
        mShareActionProvider = (android.support.v7.widget.ShareActionProvider) MenuItemCompat.getActionProvider(item);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();
        mTrailerDesc.setBackgroundColor(Color.TRANSPARENT);
    }

    // Click actions for views*******

    @OnClick(R.id.trailer_next)
    public void nextTrailer(View v) {
        if (mTrailers == null)
            mTrailers = new ArrayList<Trailer>();

        if (mTrailers.size() == currTrailers) {
            mTrailerNext.setVisibility(View.INVISIBLE);
            Toast.makeText(getActivity(), "No more trailers for you. You are watching too many trailers lately!", Toast.LENGTH_SHORT).show();

        } else if (mTrailers.size() > currTrailers)
            mTrailerDesc.setText(mTrailers.get(currTrailers++).getName());


        if (currTrailers > 1)
            mTrailerPrevious.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.trailer_prev)
    public void previousTrailer(View v) {
        if (mTrailers == null)
            mTrailers = new ArrayList<Trailer>();

        if (currTrailers > 1) {
            Trailer trailer = mTrailers.get(--currTrailers - 1);
            mTrailerDesc.setText(trailer.getName());
        }

        if (currTrailers == 1)
            mTrailerPrevious.setVisibility(View.INVISIBLE);

        if (currTrailers < mTrailers.size())
            mTrailerNext.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.review_next)
    public void nextReview(View v) {
        if (mReviews == null)
            mReviews = new ArrayList<Review>();

        if (mReviews.size() == currReview) {
            mReviewNext.setVisibility(View.INVISIBLE);
            Toast.makeText(getActivity(), "Sorry.. Thats all we have for now!", Toast.LENGTH_SHORT).show();

        } else if (mReviews.size() > currReview)
            mReviewText.setText(mReviews.get(currReview++).toString());

        if (currReview > 1)
            mReviewPrevious.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.review_prev)
    public void previousReview(View v) {
        if (mReviews == null)
            mReviews = new ArrayList<Review>();

        if (currReview > 1) {
            Review review = mReviews.get(--currReview - 1);
            mReviewText.setText(review.toString());
        }

        if (currReview == 1)
            mReviewPrevious.setVisibility(View.INVISIBLE);

        if (currReview < mReviews.size())
            mReviewNext.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.trailer_card)
    public void launchTrailer(View v) {
        mTrailerDesc.setBackgroundColor(Color.GRAY);
        Intent i = new Intent(Intent.ACTION_VIEW)
                .setData(Utilities.buildYoutubeUrl(mTrailers.get(currTrailers - 1).getKey()));

        if (i.resolveActivity(getActivity().getPackageManager()) != null)
            getActivity().startActivity(i);

    }

    @Nullable
    @OnClick(R.id.favourite_button)
    public void saveFavourite(View v) {

        ContentResolver contentResolver = getActivity().getContentResolver();

        if (mMovieInfo.isFavourite()) {
            contentResolver.delete(MovieContract.MovieEntry.CONTENT_URI, MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?", new String[]{mMovieInfo.getId()});
            Toast.makeText(getActivity(), mMovieInfo.getTitle() + " deleted from Favourites.", Toast.LENGTH_SHORT).show();

        } else {
            contentResolver.insert(MovieContract.MovieEntry.CONTENT_URI, Utilities.getContentValuesFromMovieInfo(mMovieInfo));
            Utilities.downloadImage(mMovieInfo, getActivity());
            Toast.makeText(getActivity(), mMovieInfo.getTitle() + " saved to Favourites.", Toast.LENGTH_SHORT).show();

        }

        ((MainActivity) getActivity()).refreshFavourites();
        mMovieInfo.setFavourite(!mMovieInfo.isFavourite());
        formatFAB();

    }

    private void unhideTrailers() {
        mTrailerLayout.setVisibility(View.VISIBLE);
        mTrailerTitle.setVisibility(View.VISIBLE);
    }

    private void unhideReviews() {
        mReviewLayout.setVisibility(View.VISIBLE);
        mReviewTitle.setVisibility(View.VISIBLE);
    }

    private void hideTrailersAndReviews() {

        mTrailerNext.setVisibility(View.INVISIBLE);
        mTrailerPrevious.setVisibility(View.INVISIBLE);
        mReviewNext.setVisibility(View.INVISIBLE);
        mReviewPrevious.setVisibility(View.INVISIBLE);
        mReviewTitle.setVisibility(View.INVISIBLE);
        mReviewLayout.setVisibility(View.INVISIBLE);
        mTrailerTitle.setVisibility(View.INVISIBLE);
        mTrailerLayout.setVisibility(View.INVISIBLE);
    }

    private void fetchTrailersAndReviews() {

        mTrailers = new ArrayList<>();
        mReviews = new ArrayList<>();
        currTrailers = 0;
        currReview = 0;
        if (mMovieInfo != null) {
            TheMovieDb.getApiClient().getMovieTrailers(mMovieInfo.getId(), BuildConfig.API_KEY).enqueue(this);
            TheMovieDb.getApiClient().getMovieReviews(mMovieInfo.getId(), "1", BuildConfig.API_KEY).enqueue(this);
        }
    }

    private void fillBasicViews() {

        if (getArguments() != null)
            mMovieInfo = getArguments().getParcelable(MOVIE_INFO);
        else
            Log.e(LOG_CAT, "Null arguments in the fragment");

        if (mMovieInfo != null) {
            mVoteAverage.setText(mMovieInfo.getVoteAverage() + "/10");
            mRatingBar.setRating(Float.parseFloat(mMovieInfo.getVoteAverage()) / 2);
            mReleaseDate.setText(Utilities.formatReleaseDateText(mMovieInfo.getReleaseDate()));
            mSynopsis.setText(mMovieInfo.getSynopsis());

            if (movieTitle != null)
                movieTitle.setText(mMovieInfo.getTitle());

            if (mMoviePoster == null) return;

            if (!mMovieInfo.isFavourite()) {
                Cursor c = getActivity().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, new String[]{MovieContract.MovieEntry._ID}, MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?", new String[]{mMovieInfo.getId()}, null);
                mMovieInfo.setFavourite(c.moveToFirst());
                c.close();
            }

            formatFAB();

            File image = Utilities.getMoviePosterImage(mMovieInfo);
            if (image.exists())
                mMoviePoster.setImageBitmap(BitmapFactory.decodeFile(image.getAbsolutePath()));
            else
                Picasso.with(getActivity()).load(mMovieInfo.getImageurl()).into(mMoviePoster);

        }
    }

    private void formatFAB() {

        if (mMovieInfo.isFavourite())
            mFavouriteButton.setImageResource(R.drawable.star_bookmarked);
        else
            mFavouriteButton.setImageResource(R.drawable.star_unbookmarked);

    }


    private void setShareIntent() {

        String shareText = Utilities.buildYoutubeUrl(mTrailers.get(0).getKey()).toString();

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        shareIntent.setType("text/plain");
        if (mShareActionProvider != null)
            mShareActionProvider.setShareIntent(shareIntent);
    }

    // Retrofit callbacks

    @Override
    public void onResponse(Response<Raw> response, Retrofit retrofit) {

        Results[] results = response.body().getResults();

        if (results.length == 0) return;

        if (results[0].getName() != null) {

            for (int i = 0; i < results.length; i++)
                mTrailers.add(new Trailer(results[i].getName(), results[i].getKey()));

            mTrailerNext.setVisibility(View.VISIBLE);
            mTrailerDesc.setText(mTrailers.get(currTrailers++).getName());
            setShareIntent();
            unhideTrailers();

        } else {
            for (int i = 0; i < results.length; i++)
                mReviews.add(new Review(results[i].getAuthor(), results[i].getContent()));

            unhideReviews();
            mReviewNext.setVisibility(View.VISIBLE);
            mReviewText.setText(mReviews.get(currReview++).toString());
        }
    }

    @Override
    public void onFailure(Throwable t) {
    }

}
