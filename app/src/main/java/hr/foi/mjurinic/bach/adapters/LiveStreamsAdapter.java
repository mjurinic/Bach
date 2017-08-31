package hr.foi.mjurinic.bach.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.foi.mjurinic.bach.R;
import hr.foi.mjurinic.bach.models.API.LiveStreamResponse;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class LiveStreamsAdapter extends RecyclerView.Adapter<LiveStreamsAdapter.ViewHolder> {

    private Context context;
    private List<LiveStreamResponse> liveStreams;

    public LiveStreamsAdapter(Context context, List<LiveStreamResponse> liveStreams) {
        this.context = context;
        this.liveStreams = liveStreams;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.live_stream_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Glide.with(context)
                .load(liveStreams.get(position).getAvatar())
                .bitmapTransform(new CropCircleTransformation(context))
                .into(holder.avatar);

        holder.name.setText(liveStreams.get(position).getName());
        holder.nickname.setText(liveStreams.get(position).getNickname());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO what happens when you click on the stream
            }
        });
    }

    @Override
    public int getItemCount() {
        return liveStreams.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_avatar)
        ImageView avatar;

        @BindView(R.id.tv_name)
        TextView name;

        @BindView(R.id.tv_nickname)
        TextView nickname;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
