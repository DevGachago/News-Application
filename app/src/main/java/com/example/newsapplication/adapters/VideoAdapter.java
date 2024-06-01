package com.example.newsapplication.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.newsapplication.R;
import com.example.newsapplication.models.VideoData;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;

import java.util.List;

public class VideoAdapter extends ArrayAdapter<VideoData> {
    private Context mContext;
    private List<VideoData> mVideoList;
    private ExoPlayer player;
    private SparseArray<MediaItem> mediaItemsMap = new SparseArray<>();

    public VideoAdapter(Context context, List<VideoData> videoList) {
        super(context, 0, videoList);
        mContext = context;
        mVideoList = videoList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.videos, parent, false);
        }

        VideoData currentVideo = mVideoList.get(position);

        TextView videoTitle = convertView.findViewById(R.id.textVideoTitle);
        TextView videoDescription = convertView.findViewById(R.id.textVideoDescription);
        PlayerView playerView = convertView.findViewById(R.id.player_view);

        videoTitle.setText(currentVideo.getTitle());
        videoDescription.setText(currentVideo.getDescription());

        String videoUrl = currentVideo.getVideoUrl();
        if (videoUrl != null && !videoUrl.isEmpty()) {
            if (player == null) {
                player = new ExoPlayer.Builder(mContext).build();
                playerView.setPlayer(player);
            }

            MediaItem mediaItem = MediaItem.fromUri(Uri.parse(videoUrl));
            player.setMediaItem(mediaItem);
            player.prepare();
            playerView.setVisibility(View.VISIBLE);
        } else {
            playerView.setVisibility(View.GONE);
            Log.e("VideoAdapter", "Invalid video URL at position: " + position);
        }

        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        // Release all players when data set changes
        releasePlayer();
    }

    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }
}
