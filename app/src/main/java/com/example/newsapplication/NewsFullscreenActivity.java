package com.example.newsapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class NewsFullscreenActivity extends AppCompatActivity {

    private ImageView imageViewNews;
    private TextView textViewNewsTitle;
    private TextView textViewContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_fullscreen);

        // Initialize views
        textViewNewsTitle = findViewById(R.id.textViewNewsTitle);
        textViewContent = findViewById(R.id.textViewNewsContent);
        imageViewNews = findViewById(R.id.imageViewNews);

        // Get data passed from the main activity
        Intent intent = getIntent();
        if (intent != null) {
            String title = intent.getStringExtra("title");
            String content = intent.getStringExtra("content");

            // Set news title and content in the views
            textViewNewsTitle.setText(title);
            textViewContent.setText(content);


            // Check if imageUrl is provided in the intent
            if (intent.hasExtra("imageUrl")) {
                String imageUrl = intent.getStringExtra("imageUrl");
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    // Load the image using Picasso
                    Picasso.get().load(imageUrl).into(imageViewNews);
                } else {
                    // Handle the case where the image URL is empty or null
                    // You can set a default image or show an error message
                    imageViewNews.setImageResource(R.drawable.default_image);
                }
            } else {
                // Handle the case where imageUrl is not provided in the intent
                // You can set a default image or show an error message
                imageViewNews.setImageResource(R.drawable.default_image);
            }
        }
    }
}
