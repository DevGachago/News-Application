package com.example.newsapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ContactUs extends AppCompatActivity {

    private DatabaseReference messagesRef;
    private EditText messageEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        // Initialize the Firebase database reference
        messagesRef = FirebaseDatabase.getInstance().getReference().child("messages");

        messageEditText = findViewById(R.id.editTextMessage);
        Button sendButton = findViewById(R.id.buttonSend);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void sendMessage() {
        String message = messageEditText.getText().toString().trim();

        if (!message.isEmpty()) {
            // Generate a unique key for the message
            String messageId = messagesRef.push().getKey();

            // Save the message to the database
            messagesRef.child(messageId).setValue(message);

            // Clear the input field
            messageEditText.setText("");

            Toast.makeText(this, "Message sent successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
        }
    }
}
