package com.example.corepointhealthcare;

import com.google.firebase.Timestamp;

public class Task {
    private String id;
    private String assignedWorkerId;
    private Timestamp createdAt;
    private String createdBy;
    private String deliveryAddress;
    private String dispatchedFrom;
    private String itemId;
    private String status;
    private Timestamp updatedAt;

    private String name;

    public Task() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAssignedWorkerId() { return assignedWorkerId; }
    public Timestamp getCreatedAt() { return createdAt; }
    public String getCreatedBy() { return createdBy; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public String getDispatchedFrom() { return dispatchedFrom; }
    public String getItemId() { return itemId; }
    public String getStatus() { return status; }
    public Timestamp getUpdatedAt() { return updatedAt; }

    public String getName() { return name; }
}
