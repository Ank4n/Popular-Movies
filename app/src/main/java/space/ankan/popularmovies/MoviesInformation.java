package space.ankan.popularmovies;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by anurag on 29-Nov-15.
 */
public class MoviesInformation {

    public static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    private static final String DEFAULT_IMAGE_SIZE = "w500";

    private static String imageSize = DEFAULT_IMAGE_SIZE;
    private static final List<String> availableSizes = Arrays.asList("w92", "w154", "w185", "w342", "w500", "w780", "original");

    private String imageRelativePath;

    public MoviesInformation (String relativePath){
        this.imageRelativePath = relativePath;
    }

    public static void setImageSize(String size){
        if (!availableSizes.contains(size))
            imageSize = DEFAULT_IMAGE_SIZE;
        else
            imageSize = size;

    }
    public String getImageurl(){

        return BASE_IMAGE_URL + imageSize + this.imageRelativePath;
    }
}
