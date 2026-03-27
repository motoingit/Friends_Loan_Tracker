package com.moto.loanTracker;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.moto.loanTracker.model.Transaction;
import com.moto.loanTracker.viewmodel.TransactionViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddTransactionActivity extends AppCompatActivity {

    private int friendId;
    private TextInputEditText editAmount, editDescription;
    private RadioGroup radioGroupType, radioGroupStatus;
    private MaterialButton btnSelectDueDate, btnSave;
    private TextView textSelectedDueDate;
    
    private long selectedDueDate = 0;
    private TransactionViewModel viewModel;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        friendId = getIntent().getIntExtra(FriendDetailActivity.EXTRA_FRIEND_ID, -1);
        if (friendId == -1) {
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        editAmount = findViewById(R.id.editAmount);
        editDescription = findViewById(R.id.editDescription);
        radioGroupType = findViewById(R.id.radioGroupType);
        radioGroupStatus = findViewById(R.id.radioGroupStatus);
        btnSelectDueDate = findViewById(R.id.btnSelectDueDate);
        textSelectedDueDate = findViewById(R.id.textSelectedDueDate);
        btnSave = findViewById(R.id.btnSaveTransaction);

        btnSelectDueDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                Calendar selected = Calendar.getInstance();
                selected.set(year, month, dayOfMonth);
                selectedDueDate = selected.getTimeInMillis();
                textSelectedDueDate.setText("Due Date: " + dateFormat.format(selected.getTime()));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        btnSave.setOnClickListener(v -> saveTransaction());
    }

    private void saveTransaction() {
        String amountStr = editAmount.getText().toString().trim();
        String description = editDescription.getText().toString().trim();

        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Please enter amount", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        String type = radioGroupType.getCheckedRadioButtonId() == R.id.radioCashIn ? "CASH_IN" : "CASH_OUT";
        
        String status = "NOT_SETTLED";
        int checkedStatusId = radioGroupStatus.getCheckedRadioButtonId();
        if (checkedStatusId == R.id.radioSettled) {
            status = "SETTLED";
        } else if (checkedStatusId == R.id.radioNoWorry) {
            status = "NO_WORRY";
        }

        long currentTime = System.currentTimeMillis();

        Transaction transaction = new Transaction(friendId, amount, type, currentTime, description, selectedDueDate, status);
        viewModel.insert(transaction);
        
        Toast.makeText(this, "Transaction saved", Toast.LENGTH_SHORT).show();
        finish();
    }
}
