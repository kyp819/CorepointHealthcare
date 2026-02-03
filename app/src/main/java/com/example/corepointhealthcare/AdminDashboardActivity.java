package com.example.corepointhealthcare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;

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
            // TODO: Start AssignTaskActivity
            Toast.makeText(this, "Opening Assign Task...", Toast.LENGTH_SHORT).show();
        });

        cardViewData.setOnClickListener(v -> {
            // TODO: Start ViewDataActivity
            Toast.makeText(this, "Opening Data View...", Toast.LENGTH_SHORT).show();
        });

        cardAnalytics.setOnClickListener(v -> {
            // TODO: Start AnalyticsActivity
            Toast.makeText(this, "Opening Analytics...", Toast.LENGTH_SHORT).show();
        });
    }
}
