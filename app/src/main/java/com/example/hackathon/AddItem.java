package com.example.hackathon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddItem extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    Item item;
    String code;
    String name;
    int quant;
    Button submitbutton;
    EditText nameET, codeET,quantET;
    DocumentReference documentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        FirebaseApp.initializeApp(this);
        db=FirebaseFirestore.getInstance();


        submitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAllViews();
                getAllValues();
                documentReference= db.collection("Item").document(code);
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot documentSnapshot=task.getResult();
                            if (documentSnapshot.exists()){
                                Toast.makeText(AddItem.this, "Item exists", Toast.LENGTH_SHORT).show();
                                db.collection("Item").document(code).update("Quantity",quant)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(AddItem.this, "Quantity Updated", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(AddItem.this, QRGenerator.class);
                                                intent.putExtra("item_code",code);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(AddItem.this, "Fail", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }
                            else{
                                createItemMap();
                                db.collection("Item").document(code)
                                        .update("Name",name,"Quantity",quant, "Code",code)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(AddItem.this, "Item Added", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(AddItem.this, QRGenerator.class);
                                                intent.putExtra("item_code",code);

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(AddItem.this, "Failed, Try again", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                        else
                        {
                            Toast.makeText(AddItem.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });




    }
    private void getAllValues() {
        //extracting all the fields into appropriate string variables
        name = nameET.getText().toString().trim();
        code = codeET.getText().toString().trim();
        quant = Integer.parseInt(quantET.getText().toString().trim());

    }
    public void getAllViews() {
        //linking the widgets to the java objects
        nameET = findViewById(R.id.item_name);
        quantET = findViewById(R.id.item_quant);
        codeET= findViewById(R.id.item_code);
        submitbutton = findViewById(R.id.submit);


    }

    public void createItemMap(){
        Map<String, Object> item = new HashMap<>();
        item.put("Name", name);
        item.put("Code", code);
        item.put("Quantity", quant);

        db.collection("Item").add(item)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}
