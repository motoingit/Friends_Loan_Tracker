package com.moto.loanTracker.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.moto.loanTracker.model.Friend;
import com.moto.loanTracker.repository.LoanRepository;

import java.util.List;

public class FriendViewModel extends AndroidViewModel {
    private LoanRepository repository;
    private LiveData<List<Friend>> allFriends;

    public FriendViewModel(@NonNull Application application) {
        super(application);
        repository = new LoanRepository(application);
        allFriends = repository.getAllFriends();
    }

    public LiveData<List<Friend>> getAllFriends() {
        return allFriends;
    }

    public void insert(Friend friend) {
        repository.insertFriend(friend);
    }

    public void update(Friend friend) {
        repository.updateFriend(friend);
    }

    public void delete(Friend friend) {
        repository.deleteFriend(friend);
    }

    public LiveData<Friend> getFriendById(int friendId) {
        return repository.getFriendById(friendId);
    }
}
