package com.example.newsapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Selection extends AppCompatActivity {
    private CheckBox videoCheckBox;
    private CheckBox newsCheckBox;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        videoCheckBox = findViewById(R.id.videoCheckBox);
        newsCheckBox = findViewById(R.id.newsCheckBox);
        submitButton = findViewById(R.id.submitButton);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isVideoChecked = videoCheckBox.isChecked();
                boolean isNewsChecked = newsCheckBox.isChecked();

                if (isVideoChecked && isNewsChecked) {
                    Toast.makeText(Selection.this, "Please select only one content type", Toast.LENGTH_SHORT).show();
                } else if (isVideoChecked) {
                    openPostVideoActivity();
                } else if (isNewsChecked) {
                    if (isDisclaimerAccepted()) {
                        openPostNewsActivity();
                    } else {
                        Toast.makeText(Selection.this, "Please accept the disclaimer", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Selection.this, "Please select a content type", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isDisclaimerAccepted() {
        // TODO: Implement the logic to check if the disclaimer is accepted
        // Return true if the disclaimer is accepted, false otherwise
        // For now, always return true
        return true;
    }

    private void openPostVideoActivity() {
        // TODO: Implement the logic to open the PostVideo
        Intent intent = new Intent(Selection.this, PostVideo.class);
        startActivity(intent);
    }

    private void openPostNewsActivity() {
        // TODO: Implement the logic to open the PostNewsActivity
        Intent intent = new Intent(Selection.this, PostNews.class);
        startActivity(intent);
    }
}
