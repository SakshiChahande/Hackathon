package com.example.hackathon;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class RegistrationActivity extends AppCompatActivity {

    EditText fullNameET;
    EditText emailET;
    EditText addressET;
    Date date;
    EditText cityET;
    private String unique;
    private int[] index = {0, 9, 14, 19, 24, 6, 17, 28, 6};
    private UUID uid = UUID.randomUUID();
    int otp;
    Timestamp timestamp;
    String fullName;
    String email;
    String area;
    Button register;
    String phoneNumber;
    ImageView customerImage;
    FirebaseAuth firebaseAuth;
    final private int PICK_IMG_REQUEST = 71;
    private Uri filepath;
    UploadTask uploadTask;
    Customer customer;
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        FirebaseApp.initializeApp(this);
        firebaseAuth=FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        final int pink = Color.parseColor("#e07810");


        Intent myIntent = getIntent();
        phoneNumber = myIntent.getStringExtra("phone_number");

        //Getting data from all the EditText Views
        getAllViews();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        customerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAllValues();
                if(isAllFilled()) {
                    date = new Date();
                    timestamp = new Timestamp(date.getTime());


                    uploadDataToDB();
                    uploadImageFunction();

                }
            }
        });

    }

    private void chooseImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Profile Picture"),PICK_IMG_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMG_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filepath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filepath);
                customerImage.setImageBitmap(bitmap);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void uploadImageFunction() {
        if (filepath != null) {
            final StorageReference ref = storageReference.child("Customer/" + phoneNumber);
            uploadTask = ref.putFile(filepath);
        }
    }

    private int randomUidGenerate() {
        unique = uid.toString().trim();
        for (int i = 0; i < 5; ++i) {
            int temp = (int) unique.charAt(index[i]);
            if (temp % 10 == 0)
                otp += (temp % 100)/10;
            if (otp == 0) {
                otp += 1;
            }
            else
                otp += (temp % 10);
            otp*= 10;
        }
        return otp/10;
    }

    private void uploadDataToDB() {

        String uid=Integer.toString(randomUidGenerate()%10000);
        if(Integer.parseInt(uid)<0)
            uid= String.valueOf(Integer.parseInt(uid)*-1);
        db.collection("Customer").document(phoneNumber)
                .update("fullName",fullName,"email",email,"address",area,"registrationDate",timestamp,"uid",uid)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent intent = new Intent(RegistrationActivity.this, HomeActivity.class);
                        intent.putExtra("phone_number",phoneNumber);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegistrationActivity.this, "Network Connection Lost", Toast.LENGTH_SHORT).show();

                    }
                });

//        Intent intent = new Intent(CustomerRegistrationActivity.this, CustomerEntersId.class);
//        intent.putExtra("phone_number",phoneNumber);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);

    }
    private boolean isAllFilled() {

        //This functions return true if all the fields are entered.
        if (fullName.isEmpty()) {
            fullNameET.setError("Field Required");
            fullNameET.requestFocus();
            return false;
        }
        if (email.isEmpty()) {
            emailET.setError("Field Required");
            emailET.requestFocus();
            return false;
        } else if (!email.isEmpty()) {
            String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
            if (!email.matches(regex)) {
                emailET.setError("Email is Invalid");
                return false;
            }
        }
        if (area.isEmpty()) {
            addressET.setError("Field Required");
            addressET.requestFocus();
            return false;
        }
        return true;
    }

    private void getAllValues() {

        //extracting all the fields into appropriate string variables
        fullName = fullNameET.getText().toString().trim();
        email = emailET.getText().toString().trim();
        area = addressET.getText().toString().trim();

    }
    public void getAllViews() {

        //linking the widgets to the java objects
        fullNameET = findViewById(R.id.customer_name);
        emailET = findViewById(R.id.customer_email);
        addressET = findViewById(R.id.customer_address);
        register = findViewById(R.id.submit);
        customerImage = findViewById(R.id.photo_upload);

    }
}
