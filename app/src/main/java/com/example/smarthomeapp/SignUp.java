package com.example.smarthomeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity implements View.OnClickListener  {
    private FirebaseAuth mAuth;
    private EditText reg_email,reg_pass,reg_name,reg_phone;
    private ImageView reg_back;
    private TextView ok_register,reg_login_ima;
    private ProgressBar progressBar;
    private LoadingDialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        loadingDialog = new LoadingDialog(this);
        mAuth = FirebaseAuth.getInstance();

        reg_back=(ImageView) this.findViewById(R.id.reg_back);
        reg_back.setOnClickListener(this);

        ok_register=(TextView) this.findViewById(R.id.ok_register);
        ok_register.setOnClickListener(this);

        reg_login_ima=(TextView) this.findViewById(R.id.reg_login_ima);
        reg_login_ima.setOnClickListener(this);

        progressBar=(ProgressBar) this.findViewById(R.id.progressBar);


        reg_phone=(EditText) this.findViewById(R.id.reg_phone);
        reg_email=(EditText) this.findViewById(R.id.reg_email);
        reg_pass=(EditText) this.findViewById(R.id.reg_pass);
        reg_name=(EditText) this.findViewById(R.id.reg_name);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case  R.id.reg_back  :

                onBackPressed();  // function call back prevous Intent

            case R.id.reg_login_ima :
                Intent intent2 = new Intent(SignUp.this, Login.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent2);
                finish();

                break;
            case R.id.ok_register:
                registerUser();
                break;
        }
    }

    private void registerUser() {

        String name = reg_name.getText().toString().trim();
        String phone = reg_phone.getText().toString().trim();
        String email = reg_email.getText().toString().trim();
        String pass  = reg_pass.getText().toString().trim();


        if (name.isEmpty())
        {
            reg_name.setError("Y??u c???u t??n!");
            reg_name.requestFocus();
            return;
        }
        if (phone.isEmpty())
        {
            reg_phone.setError("Y??u c???u s??? ??i???n tho???i!");
            reg_phone.requestFocus();
            return;
        }
        if (email.isEmpty())
        {
            reg_email.setError("Y??u c???u Email!");
            reg_email.requestFocus();
            return;
        }
        if (pass.isEmpty())
        {
            reg_pass.setError("Y??u c???u m???t kh???u!");
            reg_pass.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            reg_email.setError("Y??u c???u Email h???p l???!");
            reg_email.requestFocus();
            return;
        }
        if (pass.length() < 6)
        {
            reg_pass.setError("T???i thi???u 6 k?? t???!");
            reg_pass.requestFocus();
            return;
        }

        else
        {
            loadingDialog.startLoading();
            mAuth.createUserWithEmailAndPassword(email,pass)
                    .addOnCompleteListener(SignUp.this,new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                User user = new User(name,phone,email,pass);
                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(firebaseUser.getUid())
                                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                            loadingDialog.stopLoading();
                                            firebaseUser.sendEmailVerification();

                                            Toast.makeText(SignUp.this,"T??i kho???n ???? ????ng k?? th??nh c??ng, vui l??ng x??c nh???n Email!",Toast.LENGTH_LONG).show();

                                            Intent intent = new Intent(SignUp.this, ConfirmSyncEsp.class);

                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                    | Intent.FLAG_ACTIVITY_NEW_TASK);

                                            startActivity(intent);
                                            finish();
                                        }
                                        else
                                        {
                                            loadingDialog.stopLoading();
                                            Toast.makeText(SignUp.this,"????ng k?? t??i kho???n th???t b???i!",Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                            }
                            else
                            {
                                loadingDialog.stopLoading();
                                Toast.makeText(SignUp.this,"????ng k?? t??i kho???n th???t b???i !",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }

    }
}