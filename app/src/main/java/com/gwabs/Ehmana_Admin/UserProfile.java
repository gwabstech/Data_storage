package com.gwabs.Ehmana_admin;

import static android.app.Activity.RESULT_OK;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;


public class UserProfile extends Fragment {

    FirebaseUser firebaseUser ;
    private FirebaseAuth mAuth;
    ImageView profilePicture;


    public UserProfile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        profilePicture = view.findViewById(R.id.imgProfileImage);
        Button btnSetProfileImage = view.findViewById(R.id.btnSetProfilPic);
        Button btnChangePassword = view.findViewById(R.id.btnChangePassword);
        TextView txtFreeStorage = view.findViewById(R.id.txtFreeStorage);
        TextView txtTotalStorage = view.findViewById(R.id.txtTotalStorageVlue);
        TextView email = view.findViewById(R.id.txtProfileEmail);


        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        email.setText(firebaseUser.getEmail());
        txtTotalStorage.setText("1GB");
        txtFreeStorage.setText("1GB");
        loadImage(firebaseUser.getPhotoUrl(),profilePicture);
        btnSetProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    selectImage();
                    loadImage(firebaseUser.getPhotoUrl(),profilePicture);
                }catch (Exception e){
                    Toast.makeText(requireContext(), "Unexpected error try again", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });

        return  view;
    }


    public void selectImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent,101);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == RESULT_OK
                && data != null && data.getData() !=null){

            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse(data.getData().toString()))
                    .build();

            firebaseUser.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                            }
                        }
                    });

        }
    }


    public void loadImage(Uri url, View vi){
        ImageView imageView = (ImageView)vi.findViewById(R.id.imgProfileImage);
        Glide.with(requireContext()).load(url).into(imageView);
    }

    public void resetPassword(){

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Change Password");
        final View customLayout
                = getLayoutInflater()
                .inflate(
                        R.layout.resetpasword,
                        null);
        builder.setView(customLayout);
        builder.setCancelable(true);
        EditText NewPassword = customLayout.findViewById(R.id.NewPassword);
        builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (TextUtils.isEmpty(NewPassword.getText().toString())){
                    Toast.makeText(requireContext(),"Enter new Password",Toast.LENGTH_SHORT).show();
                }else if (NewPassword.getText().toString().length()<6){
                    Toast.makeText(requireContext(), "Password should be at least 6 characters", Toast.LENGTH_SHORT).show();
                }
                else {
                    String newPassword = NewPassword.getText().toString();
                    firebaseUser.updatePassword(newPassword)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(requireContext(), "Password reset successful", Toast.LENGTH_SHORT).show();
                                    }else {
                                        Toast.makeText(requireContext(), "Unexpected error try again later", Toast.LENGTH_SHORT).show();
                                        Log.i("re",task.getException().toString());
                                    }
                                }
                            });
                }

            }

        });


        AlertDialog dialog
                = builder.create();
        dialog.show();

    }

}