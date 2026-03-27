package com.moto.loanTracker.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.moto.loanTracker.model.Friend;

import java.util.List;

@Dao
public interface FriendDao {
    @Insert
    long insert(Friend friend);

    @Update
    void update(Friend friend);

    @Delete
    void delete(Friend friend);

    @Query("SELECT * FROM friends ORDER BY name ASC")
    LiveData<List<Friend>> getAllFriends();

    @Query("SELECT * FROM friends WHERE id = :friendId")
    LiveData<Friend> getFriendById(int friendId);
    
    /**
     * Updates the balance for a specific friend.
     * Uses COALESCE to handle cases with no transactions, preventing NULL pointer errors.
     */
    @Query("UPDATE friends SET balance = (SELECT COALESCE(SUM(CASE WHEN type = 'CASH_IN' THEN amount ELSE -amount END), 0.0) FROM transactions WHERE friendId = :friendId) WHERE id = :friendId")
    void updateBalance(int friendId);
}
