package com.example.hackathon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.model.value.FieldValue;
import com.google.zxing.Result;
import com.google.zxing.client.android.Intents;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class Scanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    ZXingScannerView scannerView;
    FirebaseFirestore db;
    DocumentReference documentReference;
    String item;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        db= FirebaseFirestore.getInstance();
        scannerView= new ZXingScannerView(this);
        setContentView(scannerView);
    }

    @Override
    public void handleResult(Result result) {
        item=result.getText();
        modifyItem(item);
        Toast.makeText(this, result.getText(), Toast.LENGTH_SHORT).show();
        onBackPressed();
    }
    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    public void modifyItem(String item){
        documentReference= db.collection("Item").document(item);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        Toast.makeText(Scanner.this, "Item exists", Toast.LENGTH_SHORT).show();
                        int newQuant=Integer.parseInt(documentSnapshot.get("Quantity").toString())-1;
                        if(newQuant>=0)
                        {
                        documentReference.update("Quantity",newQuant);
                        }
                        else{
                            documentReference.update("Quantity",0);
                            Toast.makeText(Scanner.this, "Item not available", Toast.LENGTH_SHORT).show();
                        }


                    }
                }
            }
        });
    }
}