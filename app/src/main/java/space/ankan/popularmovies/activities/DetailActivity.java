package space.ankan.popularmovies.activities;

import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import space.ankan.popularmovies.Utilities;
import space.ankan.popularmovies.data.MovieContract;
import space.ankan.popularmovies.data.MovieInfo;
import space.ankan.popularmovies.R;


public class DetailActivity extends AppCompatActivity {


    @Bind(R.id.favourite_button)
    FloatingActionButton mFavouriteButton;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCtl;

    @Bind(R.id.movie_poster)
    ImageView mMoviePoster;

    private MovieInfo mMovieInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mMovieInfo = getIntent().getExtras().getParcelable(DetailActivityFragment.MOVIE_INFO);

        if (mMovieInfo != null) {

            mCtl.setTitle(mMovieInfo.getTitle());

            File image = Utilities.getMoviePosterImage(mMovieInfo);
            if (!mMovieInfo.isFavourite()) {
                Cursor c = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, new String[]{MovieContract.MovieEntry._ID}, MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?", new String[]{mMovieInfo.getId()}, null);
                mMovieInfo.setFavourite(c.moveToFirst());
                c.close();
            }

            formatFAB();

            if (image.exists())
                mMoviePoster.setImageBitmap(BitmapFactory.decodeFile(image.getAbsolutePath()));
            else
                Picasso.with(this).load(mMovieInfo.getImageurl()).into(mMoviePoster);
        }

        if (savedInstanceState == null) {

            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailActivityFragment.MOVIE_INFO, mMovieInfo);

            DetailActivityFragment fragment = new DetailActivityFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.favourite_button)
    public void saveFavourite(View v) {

        if (mMovieInfo.isFavourite()) {
            getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?", new String[]{mMovieInfo.getId()});
            Toast.makeText(this, mMovieInfo.getTitle() + " deleted from favourites.", Toast.LENGTH_SHORT).show();
        } else {
            getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, Utilities.getContentValuesFromMovieInfo(mMovieInfo));
            Utilities.downloadImage(mMovieInfo, this);
            Toast.makeText(this, mMovieInfo.getTitle() + " saved to favourites.", Toast.LENGTH_SHORT).show();
        }
        mMovieInfo.setFavourite(!mMovieInfo.isFavourite());
        formatFAB();

    }

    private void formatFAB() {

        if (mMovieInfo.isFavourite())
            mFavouriteButton.setImageResource(R.drawable.star_bookmarked);
        else
            mFavouriteButton.setImageResource(R.drawable.star_unbookmarked);

    }
}
