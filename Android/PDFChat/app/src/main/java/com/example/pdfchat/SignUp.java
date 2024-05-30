package com.example.pdfchat;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pdfchat.LogIn;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
    }
    @SuppressLint("NotConstructor")
    public void SignUp(View v){
        TextView email_view=findViewById(R.id.SignUpEmailAddress);
        String email=email_view.getText().toString();
        TextView pw_view=findViewById(R.id.SignUpPassword);
        String pw=pw_view.getText().toString();
        boolean isPresent=false;
        for(int i=0;i<email.length();i++) {
            if(email.charAt(i)=='@')
                isPresent=true;
        }
        if(!isPresent) {
            Toast.makeText(this,"Invalid email format.@ is missing",Toast.LENGTH_SHORT).show();
            return;
        }

        String ending=".com";
        int index=0;
        for(int i=email.length()-4;i<email.length();i++) {
            if(email.charAt(i)!=ending.charAt(index)){
                Toast.makeText(this,"Invalid email.It should end with .com",Toast.LENGTH_SHORT).show();
                return;
            }
            index++;
        }
        if(pw.length()<8) {
            Toast.makeText(this,"Password has to be atleast 8 characters",Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db=FirebaseFirestore.getInstance();
        Map<String,Object> user= new HashMap<>();
        user.put("email",email);
        user.put("password",pw);
        DocumentReference doc= db.collection("Person").document(email);
        doc.set(user);

        Intent login=new Intent(getApplicationContext(), LogIn.class);
        startActivity(login);
    }

    public void LogIn(View v){
        Intent login=new Intent(getApplicationContext(),LogIn.class);
        startActivity(login);
    }
}