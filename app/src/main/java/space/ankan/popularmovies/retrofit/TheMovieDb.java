package space.ankan.popularmovies.retrofit;

import retrofit.Call;
import retrofit.Converter;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import space.ankan.popularmovies.retrofit.model.Raw;

/**
 * Created by anurag on 19-Dec-15.
 */
public class TheMovieDb {

    private static TheMovieDbApiInterface service;

    public interface TheMovieDbApiInterface {

        @GET("/3/discover/movie?certification_country=US&vote_count.gte=10")
        Call<Raw> getMovieList(@Query("sort_by") String sort, @Query("page") String page, @Query("api_key") String apiKey);

        @GET("/3/movie/{id}/videos")
        Call<Raw> getMovieTrailers(@Path("id") String id, @Query("api_key") String apiKey);

        @GET("/3/movie/{id}/reviews")
        Call<Raw> getMovieReviews(@Path("id") String id, @Query("page") String page, @Query("api_key") String apiKey);
    }

    public static TheMovieDbApiInterface getApiClient() {

        if (service != null)
            return service;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org")
                .addConverterFactory((Converter.Factory) GsonConverterFactory.create())
                .build();
        service = retrofit.create(TheMovieDbApiInterface.class);

        return service;
    }

}
