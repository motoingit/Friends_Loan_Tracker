package com.moto.loanTracker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.moto.loanTracker.adapter.TransactionAdapter;
import com.moto.loanTracker.model.Friend;
import com.moto.loanTracker.model.Transaction;
import com.moto.loanTracker.viewmodel.FriendViewModel;
import com.moto.loanTracker.viewmodel.TransactionViewModel;

import java.util.Locale;

/**
 * Activity showing detailed information and transaction history for a specific friend.
 */
public class FriendDetailActivity extends AppCompatActivity {
    private static final String TAG = "FriendDetailActivity";
    public static final String EXTRA_FRIEND_ID = "com.moto.loanTracker.EXTRA_FRIEND_ID";

    private int friendId;
    private Friend currentFriend;
    private FriendViewModel friendViewModel;
    private TransactionViewModel transactionViewModel;
    
    private ShapeableImageView imageProfile;
    private TextView textBalance;
    private CollapsingToolbarLayout collapsingToolbar;
    private RecyclerView recyclerView;
    private TransactionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_friend_detail);

            // Retrieve friend ID from intent
            friendId = getIntent().getIntExtra(EXTRA_FRIEND_ID, -1);
            if (friendId == -1) {
                finish();
                return;
            }

            // Setup Toolbar and ActionBar
            Toolbar toolbar = findViewById(R.id.detailToolbar);
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            // Initialize UI components
            collapsingToolbar = findViewById(R.id.collapsingToolbar);
            imageProfile = findViewById(R.id.imageDetailProfile);
            textBalance = findViewById(R.id.textDetailBalance);
            recyclerView = findViewById(R.id.recyclerViewTransactions);
            FloatingActionButton fab = findViewById(R.id.fabAddTransaction);

            // Setup RecyclerView for transactions
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new TransactionAdapter();
            recyclerView.setAdapter(adapter);

            // Initialize ViewModels
            friendViewModel = new ViewModelProvider(this).get(FriendViewModel.class);
            transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

            // Observe friend details
            friendViewModel.getFriendById(friendId).observe(this, friend -> {
                if (friend != null) {
                    currentFriend = friend;
                    collapsingToolbar.setTitle(friend.getName());
                    textBalance.setText(String.format(Locale.getDefault(), "Balance: %s %.2f", 
                            friend.getBalance() >= 0 ? "+" : "", friend.getBalance()));
                    
                    if (friend.getProfileImageUri() != null && !friend.getProfileImageUri().isEmpty()) {
                        imageProfile.setImageURI(Uri.parse(friend.getProfileImageUri()));
                    }
                }
            });

            // Observe transaction list
            transactionViewModel.getTransactionsForFriend(friendId).observe(this, transactions -> {
                adapter.submitList(transactions);
            });

            // Long click to delete transaction
            adapter.setOnItemLongClickListener(this::showDeleteTransactionDialog);

            // Handle add transaction FAB click
            fab.setOnClickListener(v -> {
                Intent intent = new Intent(FriendDetailActivity.this, AddTransactionActivity.class);
                intent.putExtra(EXTRA_FRIEND_ID, friendId);
                startActivity(intent);
            });

        } catch (Exception e) {
            Log.e(TAG, "Critical error: " + e.getMessage());
            finish();
        }
    }

    private void showDeleteTransactionDialog(Transaction transaction) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Transaction")
                .setMessage("Are you sure you want to delete this transaction?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    transactionViewModel.delete(transaction);
                    Toast.makeText(this, "Transaction deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.friend_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_edit_friend) {
            if (currentFriend != null) {
                // Launch AddFriendActivity in edit mode (we need to update that activity next)
                Intent intent = new Intent(this, AddFriendActivity.class);
                intent.putExtra("EXTRA_EDIT_FRIEND_ID", currentFriend.getId());
                startActivity(intent);
            }
            return true;
        } else if (id == R.id.action_delete_friend) {
            if (currentFriend != null) {
                showDeleteFriendDialog(currentFriend);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteFriendDialog(Friend friend) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Person")
                .setMessage("Are you sure you want to delete " + friend.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    friendViewModel.delete(friend);
                    Toast.makeText(this, "Person deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
