package com.example.corepointhealthcare;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class RegisterWorkerActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword;
    private Button btnRegister;
    private RecyclerView rvWorkers;
    private WorkerAdapter adapter;
    private List<User> workerList;

    private FirebaseAuth adminAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_worker);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        rvWorkers = findViewById(R.id.rvWorkers);

        adminAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        workerList = new ArrayList<>();
        adapter = new WorkerAdapter(workerList, this::deleteWorker);
        rvWorkers.setLayoutManager(new LinearLayoutManager(this));
        rvWorkers.setAdapter(adapter);

        btnRegister.setOnClickListener(v -> registerWorker());

        fetchWorkers();
    }

    private void registerWorker() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // To register a new user without signing out the admin, we use a secondary Firebase app instance
        FirebaseOptions options = adminAuth.getApp().getOptions();
        FirebaseApp secondaryApp;
        try {
            secondaryApp = FirebaseApp.getInstance("secondary");
        } catch (IllegalStateException e) {
            secondaryApp = FirebaseApp.initializeApp(this, options, "secondary");
        }

        FirebaseAuth secondaryAuth = FirebaseAuth.getInstance(secondaryApp);

        secondaryAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid();
                    User worker = new User(uid, name, email, "worker");

                    db.collection("users").document(uid).set(worker)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Worker registered successfully", Toast.LENGTH_SHORT).show();
                                etName.setText("");
                                etEmail.setText("");
                                etPassword.setText("");
                                secondaryAuth.signOut();
                                fetchWorkers();
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, "Error saving to DB: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Auth Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void fetchWorkers() {
        db.collection("users")
                .whereEqualTo("role", "worker")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    workerList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        workerList.add(doc.toObject(User.class));
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void deleteWorker(User worker) {
        db.collection("users").document(worker.getUid()).delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Worker deleted from database", Toast.LENGTH_SHORT).show();
                    // Note: This only deletes from Firestore. Deleting from Auth requires Admin SDK or Cloud Functions.
                    fetchWorkers();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error deleting: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
