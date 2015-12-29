package space.ankan.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import space.ankan.popularmovies.data.MovieContract.MovieEntry;

/**
 * Created by anurag on 18-Dec-15.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY," +
                MovieEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL," +
                MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL," +
                MovieEntry.COLUMN_POSTER_PATH + " TEXT UNIQUE NOT NULL," +
                MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL," +
                MovieEntry.COLUMN_TITLE + " TEXT NOT NULL," +
                MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL);";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
