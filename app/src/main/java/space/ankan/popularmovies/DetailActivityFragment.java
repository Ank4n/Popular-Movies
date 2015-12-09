package space.ankan.popularmovies;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        MoviesInformation movieInfo = (MoviesInformation) getActivity().getIntent().getExtras().getParcelable("movie");
        getActivity().setTitle(movieInfo.getTitle());

        ImageView moviePoster = (ImageView) rootView.findViewById(R.id.movie_poster);
        Picasso.with(getContext()).load(movieInfo.getImageurl()).into(moviePoster);

        TextView voteCount = (TextView) rootView.findViewById(R.id.vote_count);
        voteCount.setText(movieInfo.getVoteCount());

        TextView releaseDate = (TextView) rootView.findViewById(R.id.release_date);
        releaseDate.setText(formatDateText(movieInfo.getReleaseDate()));

        TextView synopsis = (TextView) rootView.findViewById(R.id.synopsis);
        synopsis.setText(movieInfo.getSynopsis());

        return rootView;
    }

    private String formatDateText(String in) {

        StringBuilder text = new StringBuilder();
        String[] date = in.split("-");
        String year = date[0];
        String month = date[1];
        String day = date[2];
        text.append("Releasing on " + Integer.parseInt(day));

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
                return ("Releasing on " + in);
        }
        text.append(", " + year);
        return text.toString();

    }

}
