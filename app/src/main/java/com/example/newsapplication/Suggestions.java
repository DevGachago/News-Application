package com.example.newsapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.newsapplication.models.Messages;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Suggestions extends AppCompatActivity {

    private DatabaseReference messagesRef;
    private EditText suggestionEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestions);

        // Initialize the Firebase database reference
        messagesRef = FirebaseDatabase.getInstance().getReference().child("messages").child("suggestions");

        suggestionEditText = findViewById(R.id.editTextSuggestion);
        Button sendButton = findViewById(R.id.buttonSendSuggestion);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSuggestion();
            }
        });
    }

    private void sendSuggestion() {
        String suggestion = suggestionEditText.getText().toString().trim();

        if (!suggestion.isEmpty()) {
            // Generate a unique key for the suggestion
            String suggestionId = messagesRef.push().getKey();

            // Create a new Messages instance for the suggestion
            Messages suggestionMessage = new Messages(suggestionId, suggestion, "User", System.currentTimeMillis(), "suggestion");

            // Save the suggestion to the database
            messagesRef.child(suggestionId).setValue(suggestionMessage);

            // Clear the input field
            suggestionEditText.setText("");

            Toast.makeText(this, "Suggestion sent successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Please enter a suggestion", Toast.LENGTH_SHORT).show();
        }
    }
}
