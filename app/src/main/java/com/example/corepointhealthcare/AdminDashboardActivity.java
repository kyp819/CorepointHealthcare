package com.example.corepointhealthcare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Initialize UI Elements
        ImageButton btnLogout = findViewById(R.id.btnLogout);
        MaterialCardView cardRegisterWorker = findViewById(R.id.cardRegisterWorker);
        MaterialCardView cardAssignTask = findViewById(R.id.cardAssignTask);
        MaterialCardView cardViewData = findViewById(R.id.cardViewData);
        MaterialCardView cardAnalytics = findViewById(R.id.cardAnalytics);
        MaterialCardView cardUploadCsv = findViewById(R.id.cardUploadCsv);

        // Logout logic
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(AdminDashboardActivity.this, LoginActivity.class));
            finish();
        });

        // Dashboard Card Click Listeners
        cardRegisterWorker.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, RegisterWorkerActivity.class));
        });

        cardAssignTask.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, ViewTasksActivity.class));
        });

        cardUploadCsv.setOnClickListener(v -> uploadItemsFromCsv());

        cardViewData.setOnClickListener(v -> {
            // TODO: Start ViewDataActivity
            Toast.makeText(this, "Opening Data View...", Toast.LENGTH_SHORT).show();
        });

        cardAnalytics.setOnClickListener(v -> {
            // TODO: Start AnalyticsActivity
            Toast.makeText(this, "Opening Analytics...", Toast.LENGTH_SHORT).show();
        });
    }

    private void uploadItemsFromCsv() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        try {
            InputStream is = getAssets().open("items.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line;
            boolean isFirstLine = true;

            WriteBatch batch = db.batch();
            int batchCount = 0;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) { // skip header
                    isFirstLine = false;
                    continue;
                }

                // Split by comma: itemId,itemName
                String[] parts = line.split(",", 2);
                if (parts.length < 2) continue;

                String itemId = parts[0].trim();
                String itemName = parts[1].trim();

                if (itemId.isEmpty()) continue;

                DocumentReference ref = db.collection("items").document(itemId);
                Map<String, Object> data = new HashMap<>();
                data.put("itemName", itemName);

                batch.set(ref, data);
                batchCount++;

                // Firestore batch limit is 500 writes
                if (batchCount == 450) {
                    batch.commit();
                    batch = db.batch();
                    batchCount = 0;
                }
            }

            // Commit remaining
            batch.commit()
                    .addOnSuccessListener(unused ->
                            Toast.makeText(this, "✅ Items uploaded successfully!", Toast.LENGTH_LONG).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "❌ Upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show());

            reader.close();

        } catch (Exception e) {
            Toast.makeText(this, "❌ Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
