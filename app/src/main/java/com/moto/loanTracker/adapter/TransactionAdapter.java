package com.moto.loanTracker.adapter;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.moto.loanTracker.R;
import com.moto.loanTracker.model.Transaction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Adapter for displaying transactions in a list.
 */
public class TransactionAdapter extends ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder> {
    private static final String TAG = "TransactionAdapter";

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
    private final SimpleDateFormat dueDateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    private OnItemLongClickListener longListener;

    public TransactionAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Transaction> DIFF_CALLBACK = new DiffUtil.ItemCallback<Transaction>() {
        @Override
        public boolean areItemsTheSame(@NonNull Transaction oldItem, @NonNull Transaction newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Transaction oldItem, @NonNull Transaction newItem) {
            return oldItem.getAmount() == newItem.getAmount() &&
                    oldItem.getType().equals(newItem.getType()) &&
                    oldItem.getDate() == newItem.getDate() &&
                    String.valueOf(oldItem.getDescription()).equals(String.valueOf(newItem.getDescription())) &&
                    String.valueOf(oldItem.getStatus()).equals(String.valueOf(newItem.getStatus()));
        }
    };

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction current = getItem(position);
        if (current == null) return;
        
        boolean isCashIn = "CASH_IN".equals(current.getType());
        String sign = isCashIn ? "+" : "-";
        
        holder.textAmount.setText(String.format(Locale.getDefault(), "%s $%.2f", sign, current.getAmount()));
        holder.textAmount.setTextColor(isCashIn ? Color.parseColor("#2E7D32") : Color.RED);
        
        holder.textType.setText(isCashIn ? "(Cash In)" : "(Cash Out)");
        holder.textDate.setText(dateFormat.format(new Date(current.getDate())));
        holder.textDescription.setText(current.getDescription());

        if (current.getDueDate() > 0) {
            holder.textDueDate.setVisibility(View.VISIBLE);
            holder.textDueDate.setText("Due: " + dueDateFormat.format(new Date(current.getDueDate())));
        } else {
            holder.textDueDate.setVisibility(View.GONE);
        }

        if (holder.textStatus != null) {
            String status = current.getStatus();
            if (status != null && !status.isEmpty()) {
                holder.textStatus.setVisibility(View.VISIBLE);
                holder.textStatus.setText("Status: " + status.replace("_", " "));
            } else {
                holder.textStatus.setVisibility(View.GONE);
            }
        }
    }

    class TransactionViewHolder extends RecyclerView.ViewHolder {
        private TextView textAmount, textType, textDate, textDescription, textDueDate, textStatus;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            textAmount = itemView.findViewById(R.id.textTransAmount);
            textType = itemView.findViewById(R.id.textTransType);
            textDate = itemView.findViewById(R.id.textTransDate);
            textDescription = itemView.findViewById(R.id.textTransDescription);
            textDueDate = itemView.findViewById(R.id.textDueDate);
            textStatus = itemView.findViewById(R.id.textStatus);

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (longListener != null && position != RecyclerView.NO_POSITION) {
                    longListener.onItemLongClick(getItem(position));
                    return true;
                }
                return false;
            });
        }
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(Transaction transaction);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longListener = listener;
    }
}
