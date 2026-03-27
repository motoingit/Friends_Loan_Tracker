package com.moto.loanTracker.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "transactions",
        foreignKeys = @ForeignKey(entity = Friend.class,
                parentColumns = "id",
                childColumns = "friendId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("friendId")})
public class Transaction {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int friendId;
    private double amount;
    private String type; // "CASH_IN" or "CASH_OUT"
    private long date;
    private String description;
    private long dueDate;
    private String status; // "SETTLED", "NOT_SETTLED", or "NO_WORRY"

    public Transaction(int friendId, double amount, String type, long date, String description, long dueDate, String status) {
        this.friendId = friendId;
        this.amount = amount;
        this.type = type;
        this.date = date;
        this.description = description;
        this.dueDate = dueDate;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getFriendId() { return friendId; }
    public void setFriendId(int friendId) { this.friendId = friendId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public long getDate() { return date; }
    public void setDate(long date) { this.date = date; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public long getDueDate() { return dueDate; }
    public void setDueDate(long dueDate) { this.dueDate = dueDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
