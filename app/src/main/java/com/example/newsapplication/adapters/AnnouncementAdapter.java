package com.example.newsapplication.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.newsapplication.R;
import com.example.newsapplication.models.Announcement;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AnnouncementAdapter extends BaseAdapter {
    private Context context;
    private List<Announcement> announcementList;

    private ExoPlayer player;

    public AnnouncementAdapter(Context context, List<Announcement> announcementList) {
        this.context = context;
        this.announcementList = announcementList;
    }

    @Override
    public int getCount() {
        return announcementList.size();
    }

    @Override
    public Object getItem(int position) {
        return announcementList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_announcement, parent, false);
        }

        TextView titleView = convertView.findViewById(R.id.announcement_title);
        TextView descriptionView = convertView.findViewById(R.id.announcement_description);
        ImageView imageView = convertView.findViewById(R.id.announcement_image);
        PlayerView videoView = convertView.findViewById(R.id.announcement_video);
        TextView editDate = convertView.findViewById(R.id.textViewDateTime);
        TextView viewsCount = convertView.findViewById(R.id.views_count);

        Announcement announcement = announcementList.get(position);
        

        titleView.setText(announcement.getTitle());
        descriptionView.setText(announcement.getDescription());
        viewsCount.setText(String.valueOf(announcement.getViewsCount()));
        editDate.setText(announcement.getDatePosted());

        viewsCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementViewCounter(announcement);
            }
        });



        if (announcement.isImage()) {
            imageView.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.GONE);
            Picasso.get().load(announcement.getMediaUrl()).into(imageView);
        } else {
            imageView.setVisibility(View.GONE);
            videoView.setVisibility(View.VISIBLE);
            initializePlayer(videoView, announcement.getMediaUrl());

        }

        return convertView;

    }


    private void incrementViewCounter(Announcement announcement) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("announcements")
                .child(announcement.getId())
                .child("viewsCount");
        databaseReference.setValue(announcement.getViewsCount() + 1);
    }
    private void initializePlayer(PlayerView playerView, String mediaUrl) {
        ExoPlayer player = new ExoPlayer.Builder(context).build();
        playerView.setPlayer(player);
        MediaItem mediaItem = MediaItem.fromUri(mediaUrl);
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();
    }
}

