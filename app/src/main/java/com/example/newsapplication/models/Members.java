package com.example.newsapplication.models;

import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Members {
    private String name;
    private String location;
    private String dob;
    private String phoneNumber;
    private String email;
    private String educationLevel;

    // Constructor
    public Members(String name, String location, String dob, String phoneNumber, String email, String educationLevel) {
        this.name = name;
        this.location = location;
        this.dob = dob;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.educationLevel = educationLevel;
    }

    // Getters and Setters for the fields
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public void saveApplicationToDb(Context context, EditText editTextName, EditText editTextLocation,
                                    EditText editTextDOB, EditText editTextPhoneNumber, EditText editTextEmail,
                                    EditText editTextEducationLevel) {
        // Validate that all fields are filled
        if (name.isEmpty() || location.isEmpty() || dob.isEmpty() || phoneNumber.isEmpty() || email.isEmpty() || educationLevel.isEmpty()) {
            Toast.makeText(context, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get a reference to the Firebase Realtime Database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        // Create a new child node under the "Applications" node with a unique ID
        DatabaseReference applicationRef = databaseReference.child("Applications").push();

        // Set the application data in the child node
        applicationRef.child("name").setValue(name);
        applicationRef.child("location").setValue(location);
        applicationRef.child("dob").setValue(dob);
        applicationRef.child("phoneNumber").setValue(phoneNumber);
        applicationRef.child("email").setValue(email);
        applicationRef.child("educationLevel").setValue(educationLevel)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "Application saved to the database successfully.", Toast.LENGTH_SHORT).show();

                        // Clear the input fields
                        editTextName.setText("");
                        editTextLocation.setText("");
                        editTextDOB.setText("");
                        editTextPhoneNumber.setText("");
                        editTextEmail.setText("");
                        editTextEducationLevel.setText("");

                    } else {
                        Toast.makeText(context, "Failed to save application to the database.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}