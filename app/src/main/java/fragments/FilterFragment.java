package fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.caleb.sift11.R;

/**
 * Created by Caleb on 11/23/2016.
 */

public class FilterFragment extends Fragment {
    View filterView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        filterView = inflater.inflate(R.layout.filter_layout, container, false);
        return filterView;
    }
}
