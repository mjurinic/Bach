package hr.foi.mjurinic.bach.fragments.stream;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.foi.mjurinic.bach.R;
import hr.foi.mjurinic.bach.utils.adapters.ViewPagerAdapter;

public class StreamFragment extends Fragment {

    @BindView(R.id.stream_view_pager)
    ViewPager viewPager;

    List<Fragment> fragments;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stream, container, false);
        ButterKnife.bind(this, view);

        fragments = new ArrayList<>();
        fragments.add(new ConnectionTypeFragment());
        fragments.add(new QrFragment());

        viewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager(), fragments));
        viewPager.setCurrentItem(0);

        return view;
    }

    public void changeActiveFragment(int position) {
        viewPager.setCurrentItem(position);
    }
}
