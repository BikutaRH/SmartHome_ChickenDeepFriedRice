package com.example.smarthomeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarthomeapp.DataToEsp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity implements View.OnClickListener{

    private TextView register,login,log_forget;
    private FirebaseAuth mAuth;
    private EditText edt_email,edt_pass;
    private LoadingDialog loadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loadingDialog = new LoadingDialog(this);
        mAuth = FirebaseAuth.getInstance();

        register = (TextView) this.findViewById(R.id.log_register);
        login = (TextView) this.findViewById(R.id.log_login);
        log_forget = (TextView) this.findViewById(R.id.log_forget);
        edt_email =(EditText)this.findViewById(R.id.edt_email);
        edt_pass =(EditText)this.findViewById(R.id.edt_pass);

        register.setOnClickListener(this);
        login.setOnClickListener(this);
        log_forget.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.log_register:
                Intent intent_login = new Intent(Login.this, SignUp.class);

                intent_login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent_login);
                finish();
                break;
            case R.id.log_login:
                funcLogin();
                break;

            case R.id.log_forget:
                startActivity(new Intent(Login.this, SignUp.class));
                break;

        }
    }

    private void funcLogin() {
        String email = edt_email.getText().toString().trim();
        String pass  = edt_pass.getText().toString().trim();

        if (email.isEmpty())
        {
            edt_email.setError("Y??u c???u Email!");
            edt_email.requestFocus();
            return;
        }
        if (pass.isEmpty())
        {
            edt_pass.setError("Y??u c???u m???t kh???u!");
            edt_pass.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            edt_email.setError("Y??u c???u Email");
            edt_email.requestFocus();
            return;
        }
        if (pass.length() < 6)
        {
            edt_pass.setError("Ph???i nhi???u h??n 6 k?? t???!");
            edt_pass.requestFocus();
            return;
        }

        else
        {
            loadingDialog.startLoading();
            mAuth.signInWithEmailAndPassword(email,pass)
                    .addOnCompleteListener(Login.this,new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if(user.isEmailVerified())
                                {
                                    loadingDialog.stopLoading();
                                    Toast.makeText(Login.this,"????ng nh???p th??nh c??ng",Toast.LENGTH_LONG).show();

                                    Intent intent = new Intent(Login.this, MainActivity.class);

                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                                else
                                {
                                    loadingDialog.stopLoading();
                                    user.sendEmailVerification();
                                    Toast.makeText(Login.this,"X??c nh???n t??i kho???n Email!",Toast.LENGTH_LONG).show();
                                }

                            }
                            else
                            {
                                loadingDialog.stopLoading();
                                Toast.makeText(Login.this,"????ng nh???p th???t b???i!",Toast.LENGTH_LONG).show();
                            }
                        }
                    });

        }
    }
}