package com.gwabs.Ehmana_admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static android.app.Activity.RESULT_OK;


public class AddFileFg extends Fragment {

    // declaring ui components
   EditText fileName;
   Button btnUpload;
   private FirebaseAuth mAuth;
//  DatabaseReference databaseReference;
// Write a message to the database
  //  FirebaseDatabase database = FirebaseDatabase.getInstance();
  //  DatabaseReference databaseReference = database.getReference("message");

   // decliring databse and ui elements
    FirebaseDatabase database;
    DatabaseReference databaseReference;
  StorageReference storageReference;
    FirebaseUser firebaseUser ;



    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    public AddFileFg() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_file_fg, container, false);
        fileName = view.findViewById(R.id.edtFileName);
        btnUpload = view.findViewById(R.id.uploadbtn);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        assert firebaseUser != null;
        database = FirebaseDatabase.getInstance();
        String UserId = firebaseUser.getUid().toString();
        databaseReference = database.getReference(UserId);
     //   databaseReference = FirebaseDatabase.getInstance().getReference("Upload");
        storageReference  = FirebaseStorage.getInstance().getReference();
        FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;


        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(fileName.getText().toString())){
                    fileName.setError("Enter SongName");
                }else {
                    selectFile();
                }


            }
        });
        return view;
    }

    private void selectFile() {

/*
        Intent intent = new Intent();

        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_MIME_TYPES,"")


 */

        Intent audio = new Intent();
        audio.setType("*/*");
        audio.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(audio, "Select Audio"), 100);


    }


    private void UploadFile(Uri data){


            final ProgressDialog progressDialog = new ProgressDialog(requireContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference reference = storageReference.child(firebaseUser.getEmail()+"/"+System.currentTimeMillis());
            reference.putFile(data).
                    addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isComplete());
                            Uri uri = uriTask.getResult();

                            assert uri != null;
                            fileModel fileModel = new fileModel(fileName.getText().toString(),uri.toString());
                            databaseReference.child(Objects.requireNonNull(databaseReference.push().getKey())).setValue(fileModel);
                            Toast.makeText(requireContext(),"file uploaded succefuly",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            fragmentManager = getActivity().getSupportFragmentManager();
                            fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.contener_fragment,new HomeFragment());
                            fragmentTransaction.commit();
                        }
                    }).
                    addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Toast.makeText(requireContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();

                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull @NotNull UploadTask.TaskSnapshot snapshot) {

                    double progress = (100.0*snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                    progressDialog.setMessage("Uploaded: "+(int)progress+"%");

                }
            });



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK
                && data != null && data.getData() !=null){
            UploadFile(data.getData());
        }
    }


}