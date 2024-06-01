package com.example.newsapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsapplication.NewsFullscreenActivity;
import com.example.newsapplication.R;
import com.example.newsapplication.models.News;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private Context context;
    private List<News> newsList;
    private NewsItemClickListener itemClickListener;

    public interface NewsItemClickListener {
        void onNewsItemClick(String title, String content, String imageUrl);


    }

    public NewsAdapter(Context context, List<News> newsList, NewsItemClickListener itemClickListener) {
        this.context = context;
        this.newsList = newsList;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.news_item, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        News newsItem = newsList.get(position);
        holder.bind(newsItem);
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    class NewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView textViewTitle;
        private TextView textViewContent;
        private TextView textViewDateTime;
        private ImageView imageViewNews;
        private CardView cardView;

        private FrameLayout frameLayout;

        NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewContent = itemView.findViewById(R.id.textViewContent);
            textViewDateTime = itemView.findViewById(R.id.textViewDateTime);
            imageViewNews = itemView.findViewById(R.id.imageViewNews);
            cardView = itemView.findViewById(R.id.cardviewN);
            frameLayout = itemView.findViewById(R.id.newsItem);

            // Set click listener
            cardView.setOnClickListener(this);
        }

        void bind(News newsItem) {
            textViewTitle.setText(newsItem.getTitle());
            textViewContent.setText(newsItem.getContent());
            textViewDateTime.setText(newsItem.getDatePosted());

            String imageUrl = newsItem.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Picasso.get().load(imageUrl).into(imageViewNews);
            } else {
                // Handle the case where the image URL is empty or null
                // You can set a default image or show an error message
                imageViewNews.setImageResource(R.drawable.default_image);
            }
        }

        @Override
        public void onClick(View v) {
            // Pass the clicked news item data to the click listener
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                News clickedItem = newsList.get(position);
                itemClickListener.onNewsItemClick(clickedItem.getTitle(), clickedItem.getContent(), clickedItem.getImageUrl());
            }
        }
    }
}
