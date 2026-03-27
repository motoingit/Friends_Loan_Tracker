package com.moto.loanTracker.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.moto.loanTracker.database.AppDatabase;
import com.moto.loanTracker.database.FriendDao;
import com.moto.loanTracker.database.TransactionDao;
import com.moto.loanTracker.model.Friend;
import com.moto.loanTracker.model.Transaction;

import java.util.List;

public class LoanRepository {
    private FriendDao friendDao;
    private TransactionDao transactionDao;
    private LiveData<List<Friend>> allFriends;

    public LoanRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        friendDao = db.friendDao();
        transactionDao = db.transactionDao();
        allFriends = friendDao.getAllFriends();
    }

    public LiveData<List<Friend>> getAllFriends() {
        return allFriends;
    }

    public void insertFriend(Friend friend) {
        AppDatabase.databaseWriteExecutor.execute(() -> friendDao.insert(friend));
    }

    public void updateFriend(Friend friend) {
        AppDatabase.databaseWriteExecutor.execute(() -> friendDao.update(friend));
    }

    public void deleteFriend(Friend friend) {
        AppDatabase.databaseWriteExecutor.execute(() -> friendDao.delete(friend));
    }

    public LiveData<Friend> getFriendById(int friendId) {
        return friendDao.getFriendById(friendId);
    }

    public LiveData<List<Transaction>> getTransactionsForFriend(int friendId) {
        return transactionDao.getTransactionsForFriend(friendId);
    }

    public void insertTransaction(Transaction transaction) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            transactionDao.insert(transaction);
            friendDao.updateBalance(transaction.getFriendId());
        });
    }

    public void deleteTransaction(Transaction transaction) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            transactionDao.delete(transaction);
            friendDao.updateBalance(transaction.getFriendId());
        });
    }
}
