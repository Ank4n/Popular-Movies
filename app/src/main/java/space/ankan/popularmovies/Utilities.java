package space.ankan.popularmovies;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

import space.ankan.popularmovies.data.MovieContract;
import space.ankan.popularmovies.data.MovieInfo;

/**
 * Created by anurag on 19-Dec-15.
 */
public class Utilities {

    public static final String LOCAL_IMAGE_DIR = "/popular_movies";
    public static final int REQUEST_WRITE_STORAGE = 100;

    public static void init() {
        File imageDir = new File(Environment.getExternalStorageDirectory()
                + Utilities.LOCAL_IMAGE_DIR);
        if (!imageDir.exists()) {
            if (imageDir.mkdirs())
                Log.e("MovieProvider", "created dir " + imageDir.getAbsolutePath());
            else
                Log.e("MovieProvider", "unable to create dir " + imageDir.getAbsolutePath());
        }
    }

    public static Uri buildYoutubeUrl(String key){
       return Uri.parse("https://www.youtube.com/watch?").buildUpon().appendQueryParameter("v", key).build();
    }
    public static void permissionErrorToast(Context context) {
        Toast.makeText(context, "Please grant permission to write to sd card for this app to work correctly", Toast.LENGTH_LONG).show();
    }

    public static boolean checkWritePermissions(Activity activity) {
        boolean hasWritePermission = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        if (!hasWritePermission) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        }
        return hasWritePermission;
    }

    public static File getMoviePosterImage(MovieInfo movieInfo) {
        String filename = movieInfo.getId() + ".jpg";
        return new File(Environment.getExternalStorageDirectory()
                + Utilities.LOCAL_IMAGE_DIR, filename);

    }

    public static String formatReleaseDateText(String in) {

        StringBuilder text = new StringBuilder();
        String preText = "Released on ";
        String[] date = in.split("-");
        if (date.length < 3)
            return preText + in;
        String year = date[0];
        String month = date[1];
        String day = date[2];
        text.append(preText).append(Integer.parseInt(day));

        if (day.charAt(0) == '1')
            text.append("th");
        else if (day.charAt(1) == '1')
            text.append("st");
        else if (day.charAt(1) == '2')
            text.append("nd");
        else if (day.charAt(1) == '3')
            text.append("rd");
        else
            text.append("th");

        text.append(" of ");

        switch (Integer.parseInt(month)) {
            case 1:
                text.append("January");
                break;
            case 2:
                text.append("February");
                break;
            case 3:
                text.append("March");
                break;
            case 4:
                text.append("April");
                break;
            case 5:
                text.append("May");
                break;
            case 6:
                text.append("June");
                break;
            case 7:
                text.append("July");
                break;
            case 8:
                text.append("August");
                break;
            case 9:
                text.append("September");
                break;
            case 10:
                text.append("October");
                break;
            case 11:
                text.append("November");
                break;
            case 12:
                text.append("December");
                break;
            default:
                return (preText + in);
        }

        text.append(", ").append(year);
        return text.toString();

    }

    public static synchronized void downloadImage(MovieInfo movie, Activity activity) {

        if (!(ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            permissionErrorToast(activity);
            return;
        }

        File image = getMoviePosterImage(movie);
        Uri uri = Uri.parse(movie.getImageurl());

        if (image.exists())
            return;

        DownloadManager downloader = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(
                uri);

        request.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE)
                .setDestinationInExternalPublicDir(Utilities.LOCAL_IMAGE_DIR, image.getName());

        downloader.enqueue(request);

    }

    public static ContentValues getContentValuesFromMovieInfo(MovieInfo movieInfo) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieInfo.getId());
        contentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movieInfo.getSynopsis());
        contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movieInfo.getImageurl());
        contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movieInfo.getReleaseDate());
        contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movieInfo.getTitle());
        contentValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movieInfo.getVoteAverage());
        return contentValues;
    }
}
