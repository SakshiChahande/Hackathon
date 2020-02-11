package com.example.hackathon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class VerificationActivity extends AppCompatActivity {

    Pinview pin ;
    Button confirm;
    Button resendCode;
    //    EditText receivedCode;
    String verificationCode;
    String phoneNumber;
    PhoneAuthCredential credential;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    FirebaseFirestore db;
    Boolean user_registered = false;
    Boolean user_exists_flag = false;
    Customer customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        FirebaseApp.initializeApp(this);

        db = FirebaseFirestore.getInstance();

        Log.e("Hi","kj");
        mAuth = FirebaseAuth.getInstance();
        pin= (Pinview) findViewById(R.id.otp);

//        receivedCode = findViewById(R.id.otp);
        confirm = findViewById(R.id.confirm);
        resendCode = findViewById(R.id.resend_code);
        progressBar = findViewById(R.id.cyclicProgress);

        //Obtaining the phone number from the previous activity
        phoneNumber = getIntent().getStringExtra("phone_number");

        sendVerificationCode();

        //checking the received code manually
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code  = pin.getValue();
                if (code.isEmpty() || code.length() < 6 || code.length() > 6) {
                    Toast.makeText(VerificationActivity.this, "Code length should be 6", Toast.LENGTH_SHORT).show();


                }
                else {
                    progressBar.setVisibility(View.VISIBLE);
                    verifyCode(code);

                }

            }
        });

        resendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerificationCode();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        db.collection("Customer").document(phoneNumber).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot ds = task.getResult();
                            if (ds.exists()) {
                                user_exists_flag = true;
                                customer = ds.toObject(Customer.class);
                            } else {
                                user_exists_flag = false;
                            }
                        }
                    }
                });
    }

    //the function to verify the code with the user input code
    private void verifyCode (String code) {
        credential = PhoneAuthProvider.getCredential(verificationCode, code);
        signInWithCredential(credential);
    }

    //this checks signs in the user with the credential received according to the user type and the code
    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            checkStatus();
                            Toast.makeText(VerificationActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //  Toast.makeText(VerificationActivity.this, "Invalid Code", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //Sending the Message to the user with the code
    private void sendVerificationCode() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        //Automatic scanning of the code and inserting it going to the next activity
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            //Toast.makeText(VerificationActivity.this, "Message could not be sent", Toast.LENGTH_SHORT).show();
            if (code != null) {
                // Toast.makeText(VerificationActivity.this, code, Toast.LENGTH_SHORT).show();
//                receivedCode.setText(code);
                verifyCode(code);
            }

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(VerificationActivity.this, "Invalid Code", Toast.LENGTH_LONG).show();

        }

        @Override
        //Code is taken for manual entry verification
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationCode = s;
            Toast.makeText(VerificationActivity.this, "Verification Code Sent", Toast.LENGTH_SHORT).show();
        }
    };

    private void continuation() {
        progressBar.setVisibility(View.GONE);
        if (!user_exists_flag) {
            Map<String, Object> customer = new HashMap<>();
            customer.put("phoneNumber", phoneNumber);
            customer.put("fullName", null);

            customer.put("address", null);
            customer.put("uid", null);
            customer.put("registrationDate", null);
            customer.put("email",null);

            db.collection("Customer").document(phoneNumber)
                    .set(customer)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
        }

        Intent intent = new Intent(VerificationActivity.this, RegistrationActivity.class);
        //Stack is cleared so that the user can directly exit the app on "BACK" button press and not go to the login page again
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("phone_number", phoneNumber);
        startActivity(intent);
    }


    private void checkStatus() {
        db.collection("Customer").document(phoneNumber).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            user_exists_flag = true;
                            Customer customer = documentSnapshot.toObject(Customer.class);
                            if (customer.getFullName() != null) {
                                Log.e("AAAname","a"+customer.getFullName());

                                user_registered = true;
                            }
                        }
                        checkFlow();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Toast.makeText(VerificationActivity.this, "Network Connection Lost", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkFlow() {
        if (user_exists_flag) {
            if (user_registered) {

                Intent intent = new Intent(VerificationActivity.this, AddToLocation.class);
                //Stack is cleared so that the user can directly exit the app on "BACK" button press and not go to the login page again
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("phone_number", phoneNumber);
                startActivity(intent);

            } else {
                Intent intent = new Intent(VerificationActivity.this, RegistrationActivity.class);
                //Stack is cleared so that the user can directly exit the app on "BACK" button press and not go to the login page again
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("phone_number", phoneNumber);
                startActivity(intent);
            }
        } else {
            continuation();
        }
    }
}
