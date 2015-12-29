package space.ankan.popularmovies.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import space.ankan.popularmovies.data.MovieInfo;
import space.ankan.popularmovies.R;

/**
 * Created by anurag on 18-Dec-15.
 */
public class MovieViewHolder extends RecyclerView.ViewHolder {

    public final View mView;
    public final ImageView mImageView;
    public MovieInfo mBoundMovie;

    public MovieViewHolder(View view) {
        super(view);
        mView = view;
        mImageView = (ImageView) view.findViewById(R.id.grid_item_imageview);
    }
}
