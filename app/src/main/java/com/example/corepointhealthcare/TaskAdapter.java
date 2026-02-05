package com.example.corepointhealthcare;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;
    private Map<String, String> workerNames;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());

    public TaskAdapter(List<Task> taskList, Map<String, String> workerNames) {
        this.taskList = taskList;
        this.workerNames = workerNames;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        
        // Show Task numbers starting from 1 (position + 1)
        holder.tvTaskId.setText("Task " + (position + 1));

        holder.tvItemId.setText("Item: " + task.getItemId());
        holder.tvStatus.setText("Status: " + task.getStatus());
        
        // Fetch name from cache using assignedWorkerId
        String name = workerNames.get(task.getAssignedWorkerId());
        if (name == null) name = "Unknown Worker (" + task.getAssignedWorkerId() + ")";
        holder.tvWorkerId.setText("Assigned To: " + name);

        holder.tvDeliveryAddress.setText("Delivery: " + task.getDeliveryAddress());
        holder.tvDispatchedFrom.setText("From: " + task.getDispatchedFrom());
        
        if (task.getCreatedAt() != null) {
            holder.tvCreatedAt.setText("Created: " + dateFormat.format(task.getCreatedAt().toDate()));
        }
        holder.tvCreatedBy.setText("By: " + task.getCreatedBy());
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTaskId, tvItemId, tvStatus, tvWorkerId, tvDeliveryAddress, tvDispatchedFrom, tvCreatedAt, tvCreatedBy;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTaskId = itemView.findViewById(R.id.tvTaskId);
            tvItemId = itemView.findViewById(R.id.tvItemId);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvWorkerId = itemView.findViewById(R.id.tvWorkerId);
            tvDeliveryAddress = itemView.findViewById(R.id.tvDeliveryAddress);
            tvDispatchedFrom = itemView.findViewById(R.id.tvDispatchedFrom);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
            tvCreatedBy = itemView.findViewById(R.id.tvCreatedBy);
        }
    }
}
