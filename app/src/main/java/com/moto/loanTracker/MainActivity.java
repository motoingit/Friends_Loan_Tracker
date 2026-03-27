package com.moto.loanTracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.moto.loanTracker.adapter.FriendAdapter;
import com.moto.loanTracker.model.Friend;
import com.moto.loanTracker.viewmodel.FriendViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Main activity displaying the list of friends and their balances.
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private FriendViewModel viewModel;
    private RecyclerView recyclerView;
    private FriendAdapter adapter;
    private TextView emptyView;
    private List<Friend> allFriendsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_main);

            // Initialize UI components
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            recyclerView = findViewById(R.id.recyclerViewFriends);
            emptyView = findViewById(R.id.emptyView);
            FloatingActionButton fab = findViewById(R.id.fabAddFriend);

            // Setup RecyclerView
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new FriendAdapter();
            recyclerView.setAdapter(adapter);

            // Initialize ViewModel and observe data
            viewModel = new ViewModelProvider(this).get(FriendViewModel.class);
            viewModel.getAllFriends().observe(this, friends -> {
                try {
                    allFriendsList = friends;
                    if (friends == null || friends.isEmpty()) {
                        emptyView.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        emptyView.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        adapter.submitList(friends);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error updating friend list: " + e.getMessage());
                }
            });

            // Handle friend click
            adapter.setOnItemClickListener(friend -> {
                try {
                    Intent intent = new Intent(MainActivity.this, FriendDetailActivity.class);
                    intent.putExtra(FriendDetailActivity.EXTRA_FRIEND_ID, friend.getId());
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, "Error opening FriendDetailActivity: " + e.getMessage());
                }
            });

            // Handle friend long click for deletion
            adapter.setOnItemLongClickListener(friend -> {
                showDeleteFriendDialog(friend);
            });

            // Handle add friend FAB click
            fab.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(MainActivity.this, AddFriendActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, "Error opening AddFriendActivity: " + e.getMessage());
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Critical error in onCreate: " + e.getMessage());
        }
    }

    private void showDeleteFriendDialog(Friend friend) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Person")
                .setMessage("Are you sure you want to delete " + friend.getName() + "? This will also delete all their transactions.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    viewModel.delete(friend);
                    Toast.makeText(this, "Person deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            getMenuInflater().inflate(R.menu.main_menu, menu);
            MenuItem searchItem = menu.findItem(R.id.action_search);
            SearchView searchView = (SearchView) searchItem.getActionView();
            
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    filter(newText);
                    return true;
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error creating options menu: " + e.getMessage());
        }
        return true;
    }

    private void filter(String text) {
        try {
            List<Friend> filteredList = new ArrayList<>();
            for (Friend friend : allFriendsList) {
                if (friend.getName().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(friend);
                }
            }
            adapter.submitList(filteredList);
        } catch (Exception e) {
            Log.e(TAG, "Error filtering list: " + e.getMessage());
        }
    }
}
