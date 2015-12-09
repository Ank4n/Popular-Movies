package space.ankan.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;
import java.util.List;

/**
 * Created by anurag on 29-Nov-15.
 */
public class MoviesInformation implements Parcelable {

    public static final String RESULT_LIST = "results";
    public static final String POSTER_PATH = "poster_path";
    public static final String TITLE = "title";
    public static final String RELEASE_DATE = "release_date";
    public static final String VOTE_COUNT = "vote_count";
    public static final String SYNOPSIS = "overview";
    public static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    private static final String DEFAULT_IMAGE_SIZE = "w500";

    private static String imageSize = DEFAULT_IMAGE_SIZE;
    private static final List<String> availableSizes = Arrays.asList("w92", "w154", "w185", "w342", "w500", "w780", "original");

    private String title;
    private String imageRelativePath;
    private String releaseDate;
    private String voteCount;
    private String synopsis;

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

    public String getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(String voteCount) {
        this.voteCount = voteCount;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public MoviesInformation(String title, String relativePath, String releaseDate, String voteCount, String synopsis) {
        this.title = title;
        this.imageRelativePath = relativePath;
        this.releaseDate = releaseDate;
        this.voteCount = voteCount;
        this.synopsis = synopsis;
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
        dest.writeStringArray(new String[]{this.title, this.imageRelativePath, this.releaseDate, this.voteCount, this.synopsis});
    }

    public static final Parcelable.Creator<MoviesInformation> CREATOR
            = new Parcelable.Creator<MoviesInformation>() {
        public MoviesInformation createFromParcel(Parcel in) {
            return new MoviesInformation(in);
        }

        @Override
        public MoviesInformation[] newArray(int size) {
            return new MoviesInformation[0];
        }
    };

    public MoviesInformation(Parcel in) {
        String[] data = new String[5];
        in.readStringArray(data);
        this.title = data[0];
        this.imageRelativePath = data[1];
        this.releaseDate = data[2];
        this.voteCount = data[3];
        this.synopsis = data[4];
    }

}
