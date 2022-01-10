package com.gwabs.Ehmana_admin;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class HomeFragment extends Fragment {



    private ArrayList<String> FileName,FileURl;
    private RecyclerView myRcycler;
    private int TotalItems;

    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser ;

    RecyclerAdapter adapter;
   // private int
    private static final int Permission_Request_code = 100;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        myRcycler =view.findViewById(R.id.RecyclerView);
        LinearLayoutManager linearLayoutManager  = new LinearLayoutManager(getContext());
        myRcycler.setLayoutManager(linearLayoutManager);
        myRcycler.setHasFixedSize(true);

        // database and auth
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        assert firebaseUser != null;
        database = FirebaseDatabase.getInstance();
        String UserId = firebaseUser.getUid().toString();
        databaseReference = database.getReference(UserId);
        getDataFromFirebase();




        return view;
    }

    public void DownloadFile(String Url,String fileName){

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(Url));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setTitle("Download");
        request.setDescription("Downloading File...");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,""+fileName);

        DownloadManager downloadManager = (DownloadManager) requireActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);



    }

    private void getDataFromFirebase(){
        clearArrays();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1:snapshot.getChildren()){
                    if (snapshot1 !=null){

                        Log.i("TAG", snapshot1.toString());
                        FileName.add(snapshot1.child("fileName").getValue().toString());
                        FileURl.add(snapshot1.child("fileURL").getValue().toString());

                    }else {
                        Log.i("dataE","Not Getting data");
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("ER1",error.getMessage());
            }
        });
        adapter = new RecyclerAdapter(requireContext(), FileName, FileURl, new titleClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {

                ImageView DeleteFile = itemView.findViewById(R.id.imgDelete);
                DeleteFile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       // Toast.makeText(requireContext(), "I was clicked", Toast.LENGTH_SHORT).show();
                        deleteFile(FileName.get(position), FileURl.get(position),position);

                    }
                });

                ImageView Downloadimg = itemView.findViewById(R.id.imgDownload);
                Downloadimg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {

                            DownloadFile(FileURl.get(position),FileName.get(position));


                        }catch (Exception e){
                            Toast.makeText(requireContext(), "Unexpected Error Please Try Again ", Toast.LENGTH_SHORT).show();
                            Log.i("Error",e.getMessage());
                        }


                    }

                });

                TextView filename = itemView.findViewById(R.id.FileName);
                filename.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(FileURl.get(position)));
                        startActivity(browserIntent);
                    }
                });
            }

        });



       // adapter.notifyDataSetChanged();
        myRcycler.setAdapter(adapter);


    }

    public void clearArrays(){
        if (FileName != null){
            FileName.clear();
        }
        if (FileURl !=null){
            FileURl.clear();
        }

        FileName = new ArrayList<>();
        FileURl = new ArrayList<>();
    }


    public void deleteFile(String fileName,String FileUrl,int Position){
        final ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle("Deleting..");
        progressDialog.show();
        Query FileNameQuery = databaseReference.orderByChild("fileName").equalTo(fileName);
        Query FileUrlQuery = databaseReference.orderByChild("fileURL").equalTo(FileUrl);
        FileNameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot Filename:snapshot.getChildren()){
                    Filename.getRef().removeValue();
                    FileName.remove(Position);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        FileUrlQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot fileUrl:snapshot.getChildren()){
                    fileUrl.getRef().removeValue();
                    FileURl.remove(Position);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        progressDialog.dismiss();
        fragmentManager = getActivity().getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.contener_fragment,new HomeFragment());
        fragmentTransaction.commit();
        
    }

}