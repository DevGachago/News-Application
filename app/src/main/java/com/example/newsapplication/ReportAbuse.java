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

public class ReportAbuse extends AppCompatActivity {

    private DatabaseReference messagesRef;
    private EditText reportMessageEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_abuse);

        // Initialize the Firebase database reference
        messagesRef = FirebaseDatabase.getInstance().getReference().child("messages");

        reportMessageEditText = findViewById(R.id.editTextReportMessage);
        Button sendReportButton = findViewById(R.id.buttonSendReport);

        sendReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendReport();
            }
        });
    }

    private void sendReport() {
        String reportMessage = reportMessageEditText.getText().toString().trim();

        if (!reportMessage.isEmpty()) {
            // Generate a unique key for the report message
            String reportId = messagesRef.child("report_abuse").push().getKey();

            // Create a Messages object for the report message
            Messages report = new Messages(reportId, reportMessage, "Anonymous", System.currentTimeMillis(), "report_abuse");

            // Save the report message to the database
            messagesRef.child("report_abuse").child(reportId).setValue(report);

            // Clear the input field
            reportMessageEditText.setText("");

            Toast.makeText(this, "Report sent successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Please enter a report message", Toast.LENGTH_SHORT).show();
        }
    }

}
