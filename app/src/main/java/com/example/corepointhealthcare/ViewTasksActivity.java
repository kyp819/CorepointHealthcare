package com.example.corepointhealthcare;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewTasksActivity extends AppCompatActivity {

    private static final String TAG = "ViewTasksActivity";
    private RecyclerView rvTasks;
    private TaskAdapter adapter;
    private List<Task> taskList;
    private Map<String, String> workerNamesMap;
    private List<String> workerDisplayList;
    private List<String> workerIdList;

    private List<String> allItemsList;
    private List<String> selectedItemsList;
    private boolean[] checkedItems;

    private AutoCompleteTextView atvWorker;
    private EditText etItems, etAddress, etDispatch;
    private Button btnAssign;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tasks);

        db = FirebaseFirestore.getInstance();

        // UI Initialization
        atvWorker = findViewById(R.id.atvWorker);
        etItems = findViewById(R.id.etItems);
        etAddress = findViewById(R.id.etAddress);
        etDispatch = findViewById(R.id.etDispatch);
        btnAssign = findViewById(R.id.btnAssign);
        rvTasks = findViewById(R.id.rvTasks);

        // Data Lists
        taskList = new ArrayList<>();
        workerNamesMap = new HashMap<>();
        workerDisplayList = new ArrayList<>();
        workerIdList = new ArrayList<>();
        allItemsList = new ArrayList<>();
        selectedItemsList = new ArrayList<>();

        adapter = new TaskAdapter(taskList, workerNamesMap);
        rvTasks.setLayoutManager(new LinearLayoutManager(this));
        rvTasks.setAdapter(adapter);

        // Setup Dropdowns
        setupItemSelection();
        fetchWorkers();
        fetchItems();
        fetchTasks();

        btnAssign.setOnClickListener(v -> assignTask());
    }

    private void fetchWorkers() {
        db.collection("users").whereEqualTo("role", "worker").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    workerDisplayList.clear();
                    workerIdList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String name = doc.getString("name");
                        String uid = doc.getId();
                        workerNamesMap.put(uid, name);
                        workerDisplayList.add(name + " (" + uid.substring(0, 5) + "...)");
                        workerIdList.add(uid);
                    }
                    ArrayAdapter<String> workerAdapter = new ArrayAdapter<>(this,
                            android.R.layout.simple_dropdown_item_1line, workerDisplayList);
                    atvWorker.setAdapter(workerAdapter);
                });
    }

    private void fetchItems() {
        db.collection("items").get().addOnSuccessListener(queryDocumentSnapshots -> {
            allItemsList.clear();
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                String itemName = doc.getString("itemName");
                if (itemName != null) allItemsList.add(itemName);
            }
            checkedItems = new boolean[allItemsList.size()];
        });
    }

    private void setupItemSelection() {
        etItems.setOnClickListener(v -> {
            if (allItemsList.isEmpty()) {
                Toast.makeText(this, "No items available. Upload CSV first.", Toast.LENGTH_SHORT).show();
                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Items");
            String[] itemsArray = allItemsList.toArray(new String[0]);

            builder.setMultiChoiceItems(itemsArray, checkedItems, (dialog, which, isChecked) -> {
                if (isChecked) {
                    selectedItemsList.add(itemsArray[which]);
                } else {
                    selectedItemsList.remove(itemsArray[which]);
                }
            });

            builder.setPositiveButton("OK", (dialog, which) -> {
                etItems.setText(TextUtils.join(", ", selectedItemsList));
            });

            builder.setNegativeButton("Cancel", null);
            builder.show();
        });
    }

    private void assignTask() {
        String workerSelection = atvWorker.getText().toString();
        String address = etAddress.getText().toString().trim();
        String dispatch = etDispatch.getText().toString().trim();
        String items = etItems.getText().toString();

        if (TextUtils.isEmpty(workerSelection) || TextUtils.isEmpty(address) || 
            TextUtils.isEmpty(dispatch) || TextUtils.isEmpty(items)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int index = workerDisplayList.indexOf(workerSelection);
        if (index == -1) {
            Toast.makeText(this, "Invalid Worker", Toast.LENGTH_SHORT).show();
            return;
        }

        String workerId = workerIdList.get(index);
        String adminUid = FirebaseAuth.getInstance().getUid();

        Map<String, Object> taskMap = new HashMap<>();
        taskMap.put("assignedWorkerId", workerId);
        taskMap.put("deliveryAddress", address);
        taskMap.put("dispatchedFrom", dispatch);
        taskMap.put("itemId", items); // Storing selected items as string for now
        taskMap.put("status", "assigned");
        taskMap.put("createdAt", Timestamp.now());
        taskMap.put("createdBy", adminUid);
        taskMap.put("updatedAt", Timestamp.now());

        db.collection("deliveries").add(taskMap)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Task Assigned!", Toast.LENGTH_SHORT).show();
                    clearForm();
                    fetchTasks();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void clearForm() {
        atvWorker.setText("");
        etAddress.setText("");
        etDispatch.setText("");
        etItems.setText("");
        selectedItemsList.clear();
        if (checkedItems != null) {
            for (int i = 0; i < checkedItems.length; i++) checkedItems[i] = false;
        }
    }

    private void fetchTasks() {
        db.collection("deliveries")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    taskList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Task task = doc.toObject(Task.class);
                        task.setId(doc.getId());
                        taskList.add(task);
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}
