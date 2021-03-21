package com.example.videomeetingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.example.videomeetingapp.R;
import com.example.videomeetingapp.adapter.UsersAdapter;
import com.example.videomeetingapp.models.User;
import com.example.videomeetingapp.utilities.Constants;
import com.example.videomeetingapp.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;
    String keyUser;
    private List<User> users;
    private UsersAdapter usersAdapter;
    private TextView textErrorMessage;
    private ProgressBar usersProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        preferenceManager = new PreferenceManager(getApplicationContext());
        keyUser = preferenceManager.getString(Constants.KEY_USER_ID);
        TextView textView = findViewById(R.id.textTilte);
        textView.setText(String.format("%s %s", preferenceManager.getString(Constants.KEY_FIRST_NAME),
                preferenceManager.getString(Constants.KEY_LAST_NAME)));
        findViewById(R.id.textSignOut).setOnClickListener(v -> {
            signOut();
        });
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                sendFCMTokenToDatabase(task.getResult().getToken());
            }
        });
        RecyclerView userRecyclerView = findViewById(R.id.usersRecyclerView);
        textErrorMessage = findViewById(R.id.textErrorMessage);
        usersProgressBar = findViewById(R.id.mainProgressBar);
        users = new ArrayList<>();
        usersAdapter = new UsersAdapter(users);
        userRecyclerView.setAdapter(usersAdapter);
        getUsers();
    }

    private void getUsers() {
        usersProgressBar.setVisibility(View.VISIBLE);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTIONS_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    usersProgressBar.setVisibility(View.GONE);
                    String myUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            if (myUserId.equals(documentSnapshot.getId())) {
                                continue;
                            }
                            User user = new User();
                            user.firstName = documentSnapshot.getString(Constants.KEY_FIRST_NAME);
                            user.lastName = documentSnapshot.getString(Constants.KEY_LAST_NAME);
                            user.email = documentSnapshot.getString(Constants.KEY_EMAIL);
                            user.token = documentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                            users.add(user);
                        }
                        if (users.size() > 0) {
                            usersAdapter.notifyDataSetChanged();
                        } else {
                            textErrorMessage.setText(String.format("%s", "No users available"));
                            textErrorMessage.setVisibility(View.VISIBLE);
                        }
                    } else {
                        textErrorMessage.setText(String.format("%s", "No users available"));
                        textErrorMessage.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void sendFCMTokenToDatabase(String token) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTIONS_USERS)
                .document(keyUser);
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
                .addOnFailureListener(e -> {
            Toast.makeText(getApplicationContext(), "Unable to send token: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void signOut() {
        Toast.makeText(getApplicationContext(), "Signing out...", Toast.LENGTH_SHORT).show();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTIONS_USERS)
                .document(keyUser);
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates)
                .addOnSuccessListener(aVoid -> {
                    preferenceManager.clearPreferences();
                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                    finish();
                }).addOnFailureListener(e ->
                Toast.makeText(getApplicationContext(), "Unable to sign out", Toast.LENGTH_SHORT).show());
    }
}