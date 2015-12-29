package space.ankan.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;
import java.util.List;

/**
 * Created by anurag on 29-Nov-15.
 */
public class MovieInfo implements Parcelable {

    public static final String RESULT_LIST = "results";
    public static final String POSTER_PATH = "poster_path";
    public static final String TITLE = "title";
    public static final String RELEASE_DATE = "release_date";
    public static final String VOTE_AVERAGE = "vote_average";
    public static final String SYNOPSIS = "overview";
    public static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    private static final String DEFAULT_IMAGE_SIZE = "w500";

    private static String imageSize = DEFAULT_IMAGE_SIZE;
    private static final List<String> availableSizes = Arrays.asList("w92", "w154", "w185", "w342", "w500", "w780", "original");

    private String id;
    private String title;
    private String imageRelativePath;
    private String releaseDate;
    private String voteAverage;
    private String synopsis;
    private Boolean favourite;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public Boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(Boolean favourite) {
        this.favourite = favourite;
    }

    public MovieInfo(String id, String title, String relativePath, String releaseDate, String voteAverage, String synopsis, boolean favourite) {
        this.id = id;
        this.title = title;
        this.imageRelativePath = relativePath;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        this.synopsis = synopsis;
        this.favourite = favourite;
    }

    public static void setImageSize(String size) {
        if (!availableSizes.contains(size))
            imageSize = DEFAULT_IMAGE_SIZE;
        else
            imageSize = size;

    }

    public String getImageurl() {

        return BASE_IMAGE_URL + imageSize + this.imageRelativePath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{this.id, this.title, this.imageRelativePath, this.releaseDate, this.voteAverage, this.synopsis, this.favourite.toString()});
    }

    public static final Parcelable.Creator<MovieInfo> CREATOR
            = new Parcelable.Creator<MovieInfo>() {
        public MovieInfo createFromParcel(Parcel in) {
            return new MovieInfo(in);
        }

        @Override
        public MovieInfo[] newArray(int size) {
            return new MovieInfo[0];
        }
    };

    public MovieInfo(Parcel in) {
        String[] data = new String[7];
        in.readStringArray(data);
        int count = 0;
        this.id = data[count++];
        this.title = data[count++];
        this.imageRelativePath = data[count++];
        this.releaseDate = data[count++];
        this.voteAverage = data[count++];
        this.synopsis = data[count++];
        this.favourite = Boolean.parseBoolean(data[count++]);
    }

}
