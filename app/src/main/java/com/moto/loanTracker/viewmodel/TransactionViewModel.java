package com.moto.loanTracker.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.moto.loanTracker.model.Transaction;
import com.moto.loanTracker.repository.LoanRepository;

import java.util.List;

public class TransactionViewModel extends AndroidViewModel {
    private LoanRepository repository;

    public TransactionViewModel(@NonNull Application application) {
        super(application);
        repository = new LoanRepository(application);
    }

    public LiveData<List<Transaction>> getTransactionsForFriend(int friendId) {
        return repository.getTransactionsForFriend(friendId);
    }

    public void insert(Transaction transaction) {
        repository.insertTransaction(transaction);
    }

    public void delete(Transaction transaction) {
        repository.deleteTransaction(transaction);
    }
}
