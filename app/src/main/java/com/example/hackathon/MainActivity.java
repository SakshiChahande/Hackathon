package com.example.hackathon;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    EditText phoneNumberField;      //Field to enter the number
    Button proceed;//Button which send the number to the server
    String phoneNumber;
    Intent intent;
    Button privacy;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    String vendorId;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        phoneNumberField = findViewById(R.id.phone_number);
        proceed = findViewById(R.id.insert_number);
        progressBar = findViewById(R.id.cyclicProgress);
//
        //onclick function for the proceed button for sending the verification code
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber = phoneNumberField.getText().toString().trim();
                progressBar.setVisibility(View.VISIBLE);

                //Validation for the phone number
                if (!(phoneNumber.isEmpty()) && phoneNumber.length() == 10 && (phoneNumber.substring(0,1).equals("9") || phoneNumber.substring(0,1).equals("8") || phoneNumber.substring(0,1).equals("7"))) {
                    //concatenating the country code +91 for India and sending it to the next activity
                    phoneNumber = "+91" + phoneNumber;
                    progressBar.setVisibility(View.GONE);

                    //sending the phone number of the user to the next activity so that the message containing the code can be sent
                    intent = new Intent(MainActivity.this, VerificationActivity.class);
                    intent.putExtra("phone_number", phoneNumber);
                    startActivity(intent);
                } else {
                    //The phone number is not 10 digit and doesn't start with a 9 or 8 or 8 or 7
                    Toast.makeText(MainActivity.this, "Invalid Number", Toast.LENGTH_SHORT).show();
                    phoneNumberField.setError("Invalid Number");
                    phoneNumberField.requestFocus();
                    progressBar.setVisibility(View.GONE);
                }

            }
        });

    }

    //function to check if user is logged in or not to override the login process
    @Override
    protected void onStart() {
        super.onStart();


    }
}
