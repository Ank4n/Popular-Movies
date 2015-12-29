package space.ankan.popularmovies.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import space.ankan.popularmovies.Utilities;
import space.ankan.popularmovies.activities.DetailActivity;
import space.ankan.popularmovies.activities.MainActivity;
import space.ankan.popularmovies.data.MovieInfo;
import space.ankan.popularmovies.R;

/**
 * Created by anurag on 18-Dec-15.
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieViewHolder> {

    private Activity mActivity;
    private ArrayList<MovieInfo> mMovies;

    public MovieAdapter(Activity activity, ArrayList<MovieInfo> list) {
        this.mActivity = activity;
        this.mMovies = list;
    }

    public MovieInfo getItem(int position) {
        return mMovies.get(position);
    }

    public int getPagesFetched() {
        return this.getItemCount() / 20;
    }

    public ArrayList<MovieInfo> getItems() {
        return mMovies;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(mActivity)
                .inflate(R.layout.grid_item_movies, parent, false);

        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MovieViewHolder holder, int i) {
        holder.mBoundMovie = mMovies.get(i);
        holder.mView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ((MainActivity) mActivity).onItemClick(holder.mBoundMovie);
            }
        });

        File image = Utilities.getMoviePosterImage(holder.mBoundMovie);

        if (image.exists())
            holder.mImageView.setImageBitmap(BitmapFactory.decodeFile(image.getAbsolutePath()));
        else
            Picasso.with(mActivity).load(holder.mBoundMovie.getImageurl()).into(holder.mImageView);

    }

    @Override
    public int getItemCount() {
        if (mMovies == null)
            return 0;
        return mMovies.size();
    }

    public int getCount() {
        return getItemCount();
    }

    public void add(MovieInfo movie) {
        this.mMovies.add(movie);
        this.notifyDataSetChanged();
    }

    public void clear() {
        this.mMovies.clear();
    }

    public void addAll(List<MovieInfo> movies) {
        if (!this.mMovies.isEmpty())
            return;
        this.mMovies.addAll(movies);
        this.notifyDataSetChanged();
    }

}
