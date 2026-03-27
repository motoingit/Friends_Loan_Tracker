package com.moto.loanTracker.adapter;

import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.moto.loanTracker.R;
import com.moto.loanTracker.model.Friend;

import java.util.Locale;

public class FriendAdapter extends ListAdapter<Friend, FriendAdapter.FriendViewHolder> {

    private OnItemClickListener listener;
    private OnItemLongClickListener longListener;

    public FriendAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Friend> DIFF_CALLBACK = new DiffUtil.ItemCallback<Friend>() {
        @Override
        public boolean areItemsTheSame(@NonNull Friend oldItem, @NonNull Friend newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Friend oldItem, @NonNull Friend newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                    oldItem.getPhoneNumber().equals(newItem.getPhoneNumber()) &&
                    oldItem.getBalance() == newItem.getBalance() &&
                    String.valueOf(oldItem.getProfileImageUri()).equals(String.valueOf(newItem.getProfileImageUri()));
        }
    };

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend, parent, false);
        return new FriendViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        Friend currentFriend = getItem(position);
        holder.textName.setText(currentFriend.getName());
        holder.textPhone.setText(currentFriend.getPhoneNumber());

        double balance = currentFriend.getBalance();
        holder.textBalance.setText(String.format(Locale.getDefault(), "%s %.2f", balance >= 0 ? "+" : "", balance));
        
        if (balance > 0) {
            holder.textBalance.setTextColor(Color.parseColor("#2E7D32")); // Green
        } else if (balance < 0) {
            holder.textBalance.setTextColor(Color.RED);
        } else {
            holder.textBalance.setTextColor(Color.GRAY);
        }

        if (currentFriend.getProfileImageUri() != null && !currentFriend.getProfileImageUri().isEmpty()) {
            holder.imageProfile.setImageURI(Uri.parse(currentFriend.getProfileImageUri()));
        } else {
            holder.imageProfile.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    class FriendViewHolder extends RecyclerView.ViewHolder {
        private TextView textName;
        private TextView textPhone;
        private TextView textBalance;
        private ImageView imageProfile;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textFriendName);
            textPhone = itemView.findViewById(R.id.textPhoneNumber);
            textBalance = itemView.findViewById(R.id.textBalance);
            imageProfile = itemView.findViewById(R.id.imageFriend);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getItem(position));
                }
            });

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

    public interface OnItemClickListener {
        void onItemClick(Friend friend);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(Friend friend);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longListener = listener;
    }
}
