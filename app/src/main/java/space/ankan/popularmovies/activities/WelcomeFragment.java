package space.ankan.popularmovies.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import space.ankan.popularmovies.R;

public class WelcomeFragment extends Fragment {

    @Bind(R.id.wait_image)
    ImageView waitImage;

    public WelcomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_welcome, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    public void onFailedToConnect() {
        waitImage.setImageResource(R.drawable.no_internet);
    }

}
