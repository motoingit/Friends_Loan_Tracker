package com.moto.loanTracker.database;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.moto.loanTracker.model.Friend;
import com.moto.loanTracker.model.Transaction;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main database class for the application.
 * Version 3: Added status column to transactions and forced migration.
 */
@Database(entities = {Friend.class, Transaction.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static final String TAG = "AppDatabase";

    public abstract FriendDao friendDao();
    public abstract TransactionDao transactionDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    
    // Executor service for background database operations to keep the UI responsive
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    // Migration from version 1 to 2: Added status column
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            try {
                database.execSQL("ALTER TABLE transactions ADD COLUMN status TEXT DEFAULT 'NOT_SETTLED'");
                Log.d(TAG, "Migration 1-2 successful");
            } catch (Exception e) {
                Log.e(TAG, "Migration 1-2 failed: " + e.getMessage());
            }
        }
    };

    // Migration from version 2 to 3: Ensuring schema consistency
    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // No structural changes, just bumping version to fix integrity hash issues
            Log.d(TAG, "Migration 2-3 applied");
        }
    };

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    try {
                        // Building the database with migration support and destructive fallback
                        INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                        AppDatabase.class, "loan_tracker_db")
                                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                                // If migration fails, recreate the database to prevent crashing
                                .fallbackToDestructiveMigration()
                                .build();
                        Log.d(TAG, "Database initialized successfully");
                    } catch (Exception e) {
                        Log.e(TAG, "Database initialization error: " + e.getMessage());
                    }
                }
            }
        }
        return INSTANCE;
    }
}
