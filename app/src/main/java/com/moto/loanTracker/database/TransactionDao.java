package com.moto.loanTracker.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.moto.loanTracker.model.Transaction;

import java.util.List;

@Dao
public interface TransactionDao {
    @Insert
    long insert(Transaction transaction);

    @Update
    void update(Transaction transaction);

    @Delete
    void delete(Transaction transaction);

    @Query("SELECT * FROM transactions WHERE friendId = :friendId ORDER BY date DESC")
    LiveData<List<Transaction>> getTransactionsForFriend(int friendId);

    @Query("SELECT * FROM transactions WHERE id = :transactionId")
    Transaction getTransactionById(int transactionId);
}
