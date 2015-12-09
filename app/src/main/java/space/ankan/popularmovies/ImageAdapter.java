package space.ankan.popularmovies;

import android.content.Context;
import android.graphics.Movie;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ImageAdapter extends ArrayAdapter<MoviesInformation> {
    private Context mContext;
    private ArrayList<MoviesInformation> movieList = new ArrayList<>();
    public int pagesFetched = 0;

    public ArrayList<MoviesInformation> getMovieList() {
        return movieList;
    }
    @Override
    public void addAll(MoviesInformation... items) {
        super.addAll(items);
        this.movieList.addAll(Arrays.asList(items));
    }

    @Override
    public void addAll(Collection<? extends MoviesInformation> collection) {
        super.addAll(collection);
        this.movieList.addAll(collection);
    }

    @Override
    public void add(MoviesInformation object) {
        super.add(object);
        this.movieList.add(object);
    }

    @Override
    public void clear() {
        super.clear();
        this.movieList.clear();
    }

    public ImageAdapter(Context c, List<MoviesInformation> movieList) {
        super(c, 0, movieList);
        this.movieList.addAll(movieList);
        mContext = c;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {

        MoviesInformation movieInfo = this.getItem(position);
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.grid_item_movies, parent, false);

        ImageView imageView = (ImageView) rootView.findViewById(R.id.grid_item_imageview);

        if (convertView == null)
            Picasso.with(mContext).load(movieInfo.getImageurl()).into(imageView);

        else
            imageView = (ImageView) convertView;


        return imageView;
    }

    /*// references to our images
    private Integer[] mThumbIds = {
            R.mipmap.sample_0, R.mipmap.sample_3,
            R.mipmap.sample_0, R.mipmap.sample_1,
            R.mipmap.sample_2, R.mipmap.sample_3,
            R.mipmap.sample_0, R.mipmap.sample_1,
            R.mipmap.sample_2, R.mipmap.sample_3,

    };*/
}