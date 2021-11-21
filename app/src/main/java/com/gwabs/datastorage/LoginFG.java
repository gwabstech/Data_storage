package com.gwabs.datastorage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;


public class LoginFG extends Fragment  implements View.OnClickListener{

    private  EditText edtEmail,edtPassword;
    private FirebaseAuth mAuth;

    public LoginFG() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login_f_g, container, false);

        mAuth = FirebaseAuth.getInstance();
        Button btnlogin = view.findViewById(R.id.btnLogin);
        TextView TXTtoSignup = view.findViewById(R.id.Txt_tooSignup);

        edtEmail = view.findViewById(R.id.EdtEmailLg);
        edtPassword = view.findViewById(R.id.EdtPasswordLg);


        btnlogin.setOnClickListener(LoginFG.this);
        TXTtoSignup.setOnClickListener(LoginFG.this);


        return view;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View buttons) {
        switch (buttons.getId()){
            case R.id.btnLogin:
                if (TextUtils.isEmpty(edtEmail.getText().toString()) || TextUtils.isEmpty(edtPassword.getText().toString())){

                    Toast.makeText(getContext(),"fields cant be empty",Toast.LENGTH_SHORT).show();

                }else if (TextUtils.isEmpty(edtEmail.getText().toString()) && TextUtils.isEmpty(edtPassword.getText().toString())){
                    Toast.makeText(getContext(),"Enter your user name and password to Login",Toast.LENGTH_SHORT).show();
                }else {
                    try {
                        String Email,password;
                        Email = edtEmail.getText().toString();
                        password = edtPassword.getText().toString();

                        loginUser(Email,password);
                    }catch (Exception e){
                        e.printStackTrace();
                    }


                }

                break;
            case R.id.Txt_tooSignup:
                SignupFG signupFG = new SignupFG();
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.contener_fragment,signupFG)
                        .commit();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + buttons.getId());
        }
    }


    public void loginUser(String email,String password){

        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();
                            assert user != null;
                            Toast.makeText(requireContext(),"LogIn successful "+ user.getEmail(),Toast.LENGTH_SHORT ).show();
                            Intent i = new Intent(requireContext(),HomeActivity.class);
                            startActivity(i);
                            requireActivity().finish();
                        }else {
                            Toast.makeText(requireContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }
}