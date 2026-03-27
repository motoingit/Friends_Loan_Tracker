package com.moto.loanTracker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.moto.loanTracker.model.Friend;
import com.moto.loanTracker.viewmodel.FriendViewModel;

public class AddFriendActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ShapeableImageView imageProfile;
    private TextInputEditText editName, editPhone;
    private MaterialButton btnSave;
    private Uri selectedImageUri;
    private FriendViewModel viewModel;
    private int editFriendId = -1;
    private Friend existingFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        viewModel = new ViewModelProvider(this).get(FriendViewModel.class);

        imageProfile = findViewById(R.id.imageFriendProfile);
        editName = findViewById(R.id.editFriendName);
        editPhone = findViewById(R.id.editPhoneNumber);
        btnSave = findViewById(R.id.btnSaveFriend);

        // Check if we are in edit mode
        if (getIntent().hasExtra("EXTRA_EDIT_FRIEND_ID")) {
            editFriendId = getIntent().getIntExtra("EXTRA_EDIT_FRIEND_ID", -1);
            setTitle("Edit Person");
            btnSave.setText("Update");
            
            viewModel.getFriendById(editFriendId).observe(this, friend -> {
                if (friend != null && existingFriend == null) {
                    existingFriend = friend;
                    editName.setText(friend.getName());
                    editPhone.setText(friend.getPhoneNumber());
                    if (friend.getProfileImageUri() != null && !friend.getProfileImageUri().isEmpty()) {
                        selectedImageUri = Uri.parse(friend.getProfileImageUri());
                        imageProfile.setImageURI(selectedImageUri);
                    }
                }
            });
        } else {
            setTitle("Add Person");
        }

        imageProfile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        btnSave.setOnClickListener(v -> saveFriend());
    }

    private void saveFriend() {
        String name = editName.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String imageUri = selectedImageUri != null ? selectedImageUri.toString() : "";

        if (name.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show();
            return;
        }

        if (editFriendId == -1) {
            // Add new friend
            Friend friend = new Friend(name, phone, imageUri);
            viewModel.insert(friend);
            Toast.makeText(this, "Person added successfully", Toast.LENGTH_SHORT).show();
        } else {
            // Update existing friend
            if (existingFriend != null) {
                existingFriend.setName(name);
                existingFriend.setPhoneNumber(phone);
                existingFriend.setProfileImageUri(imageUri);
                viewModel.update(existingFriend);
                Toast.makeText(this, "Person updated successfully", Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            imageProfile.setImageURI(selectedImageUri);
            // Ensure permission to read the URI if needed, though simple setImageURI often works for local picks
        }
    }
}
