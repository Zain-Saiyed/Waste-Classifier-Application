package com.zain.deeplearning.recycl_ifier;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register_User extends AppCompatActivity {

    private static final String TAG = "Register_User";
    Button BTNREGHere , BTNbackReg ;
    TextView NameReg , EmailReg , PasswordReg ;
    FirebaseAuth Auth_reg;
    DatabaseReference databaseNameRegistration ;
    ProgressDialog progressDialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setLogo(R.mipmap.app_logo_front_round);
//        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.register__user);

        BTNREGHere = findViewById(R.id.btnRegisterHere) ;
        BTNbackReg = findViewById(R.id.btnbackreg);
        NameReg = findViewById(R.id.txtEnterName) ;
        EmailReg = findViewById(R.id.txtEmailReg) ;
        PasswordReg = findViewById(R.id.txtPasswordReg) ;
        databaseNameRegistration = FirebaseDatabase.getInstance().getReference("Users");
        progressDialog = new ProgressDialog( this) ;
        Auth_reg = FirebaseAuth.getInstance() ;

        BTNREGHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FunRegisterHere();
            }
        });

        BTNbackReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(Register_User.this, Login.class));
                finish();
            }
        });
    }


    private void FunRegisterHere() {

        final String email;
        email = EmailReg.getText().toString().trim().toLowerCase();
        String password = PasswordReg.getText().toString().trim();

        final String name = NameReg.getText().toString().trim();

        if(TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            EmailReg.setError("Please Enter a valid Email");
            return ;
        }

        if(TextUtils.isEmpty(password) || password.length() <4 ){
            PasswordReg.setError("Enter the Password");
        }

        if(TextUtils.isEmpty(name)){
            Toast.makeText(this,"Please Enter Name" , Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog.setMessage("Registering User ...");
        progressDialog.show();

        Auth_reg.createUserWithEmailAndPassword(email ,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "New user registration: " + task.isSuccessful());

                        if(task.isSuccessful()){
                            Toast.makeText(Register_User.this,"Registered Successfully",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(Register_User.this, Login.class));
                            Register_User.this.finish();
                        }
                        else{
                            Toast.makeText(Register_User.this,"Registration Failed!" + task.getException() ,Toast.LENGTH_LONG).show();
                        }

                        progressDialog.dismiss();
                    }
                });
    }
}
