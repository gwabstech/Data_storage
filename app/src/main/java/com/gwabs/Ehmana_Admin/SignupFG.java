package com.gwabs.Ehmana_admin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
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


public class SignupFG extends Fragment implements View.OnClickListener {

    private FirebaseAuth mAuth;

    private  EditText edtEmail,edtPassword;

    public SignupFG() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_signup_f_g, container, false);


        Button btnsignup = view.findViewById(R.id.btnSignup);
        TextView TXTtoLogin = view.findViewById(R.id.Txt_tooLogin);


        edtEmail = view.findViewById(R.id.EdtEmailLSU);
        edtPassword = view.findViewById(R.id.EdtPasswordSu);

        btnsignup.setOnClickListener(SignupFG.this);
        TXTtoLogin.setOnClickListener(SignupFG.this);


        return view;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View buttons) {
        switch (buttons.getId()){
            case R.id.btnSignup:
                if (TextUtils.isEmpty(edtEmail.getText().toString()) || TextUtils.isEmpty(edtPassword.getText().toString())){

                    Toast.makeText(getContext(),"please enter your email and password" ,Toast.LENGTH_SHORT).show();
               
                }else if (!Patterns.EMAIL_ADDRESS.matcher(edtEmail.getText().toString()).matches()){

                    Toast.makeText(requireContext(), "Invalid Email", Toast.LENGTH_SHORT).show();
               
                } else if (TextUtils.isEmpty(edtEmail.getText().toString())&& TextUtils.isEmpty(edtPassword.getText().toString())){
                   
                    Toast.makeText(getContext(),"Enter your Email and password to signU",Toast.LENGTH_SHORT).show();
                    
                }else if (edtPassword.getText().length() <6){
                    Toast.makeText(requireContext(), " Password should be at least 6 characters", Toast.LENGTH_SHORT).show();
                } 
                else{
                    String emai,password;
                    emai= edtEmail.getText().toString();
                    password = edtPassword.getText().toString();
                    createUser(emai,password);
                }

                break;
            case R.id.Txt_tooLogin:
                LoginFG loginFg = new LoginFG();
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.contener_fragment,loginFg)
                        .commit();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + buttons.getId());
        }

    }

    private void createUser(String Email,String Password){
        mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(Email,Password)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Tag", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            assert user != null;
                            Toast.makeText(getContext(), user.getEmail(),Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getContext(),HomeActivity.class);
                            startActivity(intent);


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Tag", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getContext(),"Failed to register "+task.getException(),Toast.LENGTH_SHORT).show();
                           // updateUI(null);
                        }
                    }
                });

    }


}