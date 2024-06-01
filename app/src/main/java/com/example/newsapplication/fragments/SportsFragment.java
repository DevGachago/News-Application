package com.example.newsapplication.fragments;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.newsapplication.NewsFullscreenActivity;
import com.example.newsapplication.R;
import com.example.newsapplication.adapters.NewsAdapter;
import com.example.newsapplication.models.News;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SportsFragment extends Fragment implements NewsAdapter.NewsItemClickListener {

    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private List<News> newsList;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sports, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewNews);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        newsList = new ArrayList<>();
        newsAdapter = new NewsAdapter(getContext(), newsList, this);
        recyclerView.setAdapter(newsAdapter);

        // Handle swipe refresh
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh the news items
                refreshNews();
            }
        });

        loadNewsItems();

        return view;
    }

    private void refreshNews() {
        // Reload news items from Firebase
        loadNewsItems();
    }

    private void loadNewsItems() {
        DatabaseReference newsRef = FirebaseDatabase.getInstance().getReference("news");

        Query query = newsRef.orderByChild("category").equalTo("Sports");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                newsList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    News newsItem = snapshot.getValue(News.class);
                    newsList.add(newsItem);
                }

                newsAdapter.notifyDataSetChanged();

                // Hide the refresh indicator
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error retrieving news items: " + databaseError.getMessage());

                // Hide the refresh indicator
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void openNewsFullscreen(String title, String content, String imageUrl) {
        Intent intent = new Intent(getActivity(), NewsFullscreenActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("content", content);
        intent.putExtra("imageUrl", imageUrl);
        startActivity(intent);
    }


    @Override
    public void onNewsItemClick(String title, String content, String imageUrl) {
        // Handle the item click event here
        openNewsFullscreen(title, content, imageUrl);
    }
}
