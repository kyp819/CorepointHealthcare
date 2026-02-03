package com.example.corepointhealthcare;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WorkerAdapter extends RecyclerView.Adapter<WorkerAdapter.WorkerViewHolder> {

    private List<User> workerList;
    private OnWorkerDeleteListener deleteListener;

    public interface OnWorkerDeleteListener {
        void onDeleteClick(User worker);
    }

    public WorkerAdapter(List<User> workerList, OnWorkerDeleteListener deleteListener) {
        this.workerList = workerList;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public WorkerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_worker, parent, false);
        return new WorkerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkerViewHolder holder, int position) {
        User worker = workerList.get(position);
        holder.tvName.setText(worker.getName());
        holder.tvEmail.setText(worker.getEmail());
        holder.btnDelete.setOnClickListener(v -> deleteListener.onDeleteClick(worker));
    }

    @Override
    public int getItemCount() {
        return workerList.size();
    }

    public static class WorkerViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail;
        ImageButton btnDelete;

        public WorkerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvWorkerName);
            tvEmail = itemView.findViewById(R.id.tvWorkerEmail);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
