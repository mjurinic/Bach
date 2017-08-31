package hr.foi.mjurinic.bach.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.foi.mjurinic.bach.R;
import hr.foi.mjurinic.bach.adapters.LiveStreamsAdapter;
import hr.foi.mjurinic.bach.models.API.LiveStreamResponse;

public class HomeFragment extends Fragment {

    @BindView(R.id.live_streams_swiperefresh)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.live_streams_recycler)
    RecyclerView recyclerView;

    @BindView(R.id.toolbar_primary_color)
    Toolbar toolbar;

    private boolean isRefreshed;
    private LiveStreamsAdapter liveStreamsAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);

        toolbar.setTitle("Live");

        initLayouts();
        initAdapter(generateDummyStreams()); // TODO Fetch list from API and initAdapter from fetch callback

        return view;
    }

    private List<LiveStreamResponse> generateDummyStreams() {
        List<LiveStreamResponse> liveStreams = new ArrayList<>();

        liveStreams.add(new LiveStreamResponse("https://68.media.tumblr.com/avatar_880babe3a380_128.png", "Mislav Jurinic", "@mjurinic"));
        liveStreams.add(new LiveStreamResponse("https://avatarfiles.alphacoders.com/806/80696.jpg", "Moislav Jurinic", "@mjurinic"));
        liveStreams.add(new LiveStreamResponse("https://pbs.twimg.com/profile_images/724290593677942784/HvuwiWk5_400x400.jpg", "Moistlav Jurinic", "@mjurinic"));
        liveStreams.add(new LiveStreamResponse("https://pbs.twimg.com/profile_images/3769275733/1b8758c009b24c901956f80e78544ce8.jpeg", "Moistmax Jurinic", "@mjurinic"));
        liveStreams.add(new LiveStreamResponse("https://68.media.tumblr.com/avatar_880babe3a380_128.png", "Mislav Jurinic", "@mjurinic"));
        liveStreams.add(new LiveStreamResponse("https://avatarfiles.alphacoders.com/806/80696.jpg", "Moislav Jurinic", "@mjurinic"));
        liveStreams.add(new LiveStreamResponse("https://pbs.twimg.com/profile_images/724290593677942784/HvuwiWk5_400x400.jpg", "Moistlav Jurinic", "@mjurinic"));
        liveStreams.add(new LiveStreamResponse("https://pbs.twimg.com/profile_images/3769275733/1b8758c009b24c901956f80e78544ce8.jpeg", "Moistmax Jurinic", "@mjurinic"));
        liveStreams.add(new LiveStreamResponse("https://68.media.tumblr.com/avatar_880babe3a380_128.png", "Mislav Jurinic", "@mjurinic"));
        liveStreams.add(new LiveStreamResponse("https://avatarfiles.alphacoders.com/806/80696.jpg", "Moislav Jurinic", "@mjurinic"));
        liveStreams.add(new LiveStreamResponse("https://pbs.twimg.com/profile_images/724290593677942784/HvuwiWk5_400x400.jpg", "Moistlav Jurinic", "@mjurinic"));
        liveStreams.add(new LiveStreamResponse("https://pbs.twimg.com/profile_images/3769275733/1b8758c009b24c901956f80e78544ce8.jpeg", "Moistmax Jurinic", "@mjurinic"));

        return liveStreams;
    }

    private void initAdapter(List<LiveStreamResponse> liveStreams) {
        liveStreamsAdapter = new LiveStreamsAdapter(getActivity().getApplicationContext(), liveStreams);
        recyclerView.setAdapter(liveStreamsAdapter);
    }

    private void initLayouts() {
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefreshed = true;
                swipeRefreshLayout.setRefreshing(false);
                // TODO Fetch from API
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
    }
}
