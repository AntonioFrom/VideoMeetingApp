package com.example.videomeetingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.videomeetingapp.R;
import com.example.videomeetingapp.utilities.Constants;
import com.example.videomeetingapp.utilities.PreferenceManager;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignInActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private MaterialButton buttonSignIn;
    private ProgressBar signInProgressBar;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        findViewById(R.id.textSignUp).setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
            startActivity(intent);
        });
        preferenceManager = new PreferenceManager(getApplicationContext());
        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)){
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        buttonSignIn = findViewById(R.id.btnSignIn);
        signInProgressBar = findViewById(R.id.signInProgressBar);

        buttonSignIn.setOnClickListener(v -> {
            if (inputEmail.getText().toString().trim().isEmpty()) {
                Toast.makeText(getApplicationContext(), "Enter email", Toast.LENGTH_SHORT).show();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(inputEmail.getText().toString()).matches()) {
                Toast.makeText(getApplicationContext(), "Enter valid email", Toast.LENGTH_SHORT).show();
            } else if (inputPassword.getText().toString().trim().isEmpty()) {
                Toast.makeText(getApplicationContext(), "Enter password", Toast.LENGTH_SHORT).show();
            } else {
                signIn();
            }
        });

//        FirebaseFirestore database = FirebaseFirestore.getInstance();
//        HashMap<String, Object> user = new HashMap<>();
//        user.put("first_name", "John");
//        user.put("last_name", "Johnes");
//        user.put("email", "john.johnes@example.com");
//        database.collection("users")
//                .add(user)
//                .addOnSuccessListener(documentReference ->
//                        Toast.makeText(SignInActivity.this, "user inserted", Toast.LENGTH_SHORT).show())
//                .addOnFailureListener(e ->
//                        Toast.makeText(SignInActivity.this, "error adding user", Toast.LENGTH_SHORT).show());
    }

    private void signIn() {
        buttonSignIn.setVisibility(View.INVISIBLE);
        signInProgressBar.setVisibility(View.VISIBLE);

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTIONS_USERS)
                .whereEqualTo(Constants.KEY_EMAIL,inputEmail.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD,inputPassword.getText().toString())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()&&task.getResult()!=null&&task.getResult().getDocuments().size() >0){
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true);
                        preferenceManager.putString(Constants.KEY_FIRST_NAME,documentSnapshot.getString(Constants.KEY_FIRST_NAME));
                        preferenceManager.putString(Constants.KEY_USER_ID,documentSnapshot.getId());
                        preferenceManager.putString(Constants.KEY_LAST_NAME,documentSnapshot.getString(Constants.KEY_LAST_NAME));
                        preferenceManager.putString(Constants.KEY_EMAIL,documentSnapshot.getString(Constants.KEY_EMAIL));
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        buttonSignIn.setVisibility(View.VISIBLE);
                        signInProgressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "Unable to sign in ", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}