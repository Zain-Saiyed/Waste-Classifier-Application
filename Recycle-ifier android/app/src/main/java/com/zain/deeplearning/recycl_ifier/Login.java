package com.zain.deeplearning.recycl_ifier;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class Login extends AppCompatActivity {

    TextView welcome_banner,signup_banner;
    Button buttonLogin, buttonRegister;
    EditText user_email, user_pass;
    Switch bool_shared_pref;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        welcome_banner = findViewById(R.id.tv_Welcomeback);
        signup_banner = findViewById(R.id.tv_signIn);
        buttonLogin = findViewById(R.id.btnLogin) ;
        buttonRegister = findViewById(R.id.btnRegisterHere) ;
        user_email = findViewById(R.id.txtEmail) ;
        user_pass = findViewById(R.id.txtPassword );
        bool_shared_pref = findViewById(R.id.switch_shared_pref);

        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        welcome_banner.setLetterSpacing((float) 0.28);
        signup_banner.setLetterSpacing((float) 0.15);



        if(shared_preferences.getLoginStatus(Login.this))
        {
            System.out.println(""+shared_preferences.getEmail(Login.this));
            startActivity(new Intent(Login.this, MainActivity.class));
//          progressDialog.dismiss();
            finish();
        }
        else{
            buttonLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FunLoginUser();
                }
            });
            buttonRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Login.this, Register_User.class));
                }
            });
        }
    }

    private void FunLoginUser() {
        String Email = user_email.getText().toString().trim().toLowerCase();
        String Password = user_pass.getText().toString().trim().toLowerCase();

        if (TextUtils.isEmpty(Email) || !Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            System.out.println(user_email.getText());
            user_email.setError("Enter a valid Email Address");
            Toast.makeText(Login.this,"Please enter a valid Email Address" , Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(Password) || Password.length()<4 ) {
            user_pass.setError("Enter a valid Password");
            Toast.makeText(Login.this,"Password length should be greater than 4" , Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Please Wait ....");
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(Email , Password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            // Save shared preferences

                            if ( bool_shared_pref.isChecked() ) {
                                shared_preferences.saveEmail(Email, Login.this);
                                shared_preferences.loggedIn(Login.this);
                            }

                            Toast.makeText(Login.this,"Authentication Successful!" , Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Login.this, MainActivity.class));
                            progressDialog.dismiss();
                            finish();
                        }
                        else{
                            Toast.makeText(Login.this,"Login Unsuccessful...Please Try again!" , Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }
}
