package com.example.newsapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.newsapplication.models.Members;

public class BecomeMember extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextLocation;
    private EditText editTextDOB;
    private EditText editTextPhoneNumber;
    private EditText editTextEmail;
    private EditText editTextEducationLevel;
    private Button buttonSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_become_member);

        // Initialize UI elements
        editTextName = findViewById(R.id.editTextName);
        editTextLocation = findViewById(R.id.editTextLocation);
        editTextDOB = findViewById(R.id.editTextDOB);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextEducationLevel = findViewById(R.id.editTextEducationLevel);
        buttonSubmit = findViewById(R.id.buttonSubmit);

        // Set click listener for the submit button
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve the user input data from the EditText fields
                String name = editTextName.getText().toString();
                String location = editTextLocation.getText().toString();
                String dob = editTextDOB.getText().toString();
                String phoneNumber = editTextPhoneNumber.getText().toString();
                String email = editTextEmail.getText().toString();
                String educationLevel = editTextEducationLevel.getText().toString();

                // Perform validation checks
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(location) || TextUtils.isEmpty(dob) ||
                        TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(email) || TextUtils.isEmpty(educationLevel)) {
                    // Display an error message or toast indicating that all fields must be filled
                    Toast.makeText(BecomeMember.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                } else {
                    // All fields are filled, proceed to save the application and redirect to the signup page
                    Members application = new Members(name, location, dob, phoneNumber, email, educationLevel);
                    application.saveApplicationToDb(getApplicationContext(), editTextName, editTextLocation, editTextDOB,
                            editTextPhoneNumber, editTextEmail, editTextEducationLevel);

                    // Redirect to the signup page
                    Intent intent = new Intent(BecomeMember.this, Register.class);
                    startActivity(intent);
                    finish(); // Close the current activity
                }
            }
        });

    }


}
