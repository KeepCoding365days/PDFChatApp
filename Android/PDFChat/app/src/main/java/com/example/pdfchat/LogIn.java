package com.example.pdfchat;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LogIn extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
    }
    public void login(View v){
        TextView email_view=findViewById(R.id.login_email);
        if(email_view==null){

            Toast.makeText(LogIn.this,"Enter email", Toast.LENGTH_SHORT).show();
            return;

        }
        String email=email_view.getText().toString();
        if(email==null||email.length()==0) {
            Toast.makeText(LogIn.this,"Enter email", Toast.LENGTH_SHORT).show();
            return;
        }

        TextView pw_view=findViewById(R.id.loginPassword);
        String pw=pw_view.getText().toString();
        if(pw.length()==0){
            Toast.makeText(LogIn.this,"Enter password", Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        DocumentReference doc= db.collection("Person").document(email);
        doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc=task.getResult();
                    if(doc.exists()){
                        Map<String,Object> user=doc.getData();
                        String pass=user.get("password").toString();
                        Log.d(TAG,"password is:"+pass);
                        Log.d(TAG,"user enterd:"+pw);
                        if (pass.equals(pw)){
                            SharedPreferences preferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("loggedIn", "True");
                            editor.putString("role","user");
                            editor.putString("username",email);
                            editor.apply();
                            Intent idx=new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(idx);
                            Log.d(TAG,"password checked");
                        }
                        else {
                            Toast.makeText(LogIn.this,"Account not found", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "password is wrong");
                        }
                    }
                    else Toast.makeText(LogIn.this,"Account not found", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    public void signUp(View v){
        Intent profile=new Intent(getApplicationContext(),SignUp.class);
        startActivity(profile);
    }


}