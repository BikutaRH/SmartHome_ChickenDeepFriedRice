package com.example.smarthomeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DataToEsp extends AppCompatActivity {

    private EditText reg_pass,reg_name;
    DhcpInfo d;
    WifiManager wifii;
    private FirebaseAuth firebaseAuth;
    private TextView btnGotoHome;
    private String userid;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_to_esp);
        AndroidNetworking.initialize(getApplicationContext());

        firebaseAuth = FirebaseAuth.getInstance();

        // get ip default gateway
        wifii= (WifiManager) getSystemService(Context.WIFI_SERVICE);
        d=wifii.getDhcpInfo();
        userid = firebaseAuth.getUid();

        String ipAddress = "192.168.43.1";

        int address = d.gateway;
        ipAddress = ((address & 0xFF)
                + "." + ((address >> 8) & 0xFF)
                + "." + ((address >> 16) & 0xFF)
                + "." + ((address >> 24) & 0xFF));

        btnGotoHome=(TextView)this.findViewById(R.id.log_gotoHome);
        reg_pass=(EditText) this.findViewById(R.id.reg_pass);
        reg_name=(EditText) this.findViewById(R.id.reg_name);

//        Button btnGet = findViewById(R.id.btn_get);
//        String finalIpAddress = ipAddress;
//        btnGet.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), finalIpAddress, Toast.LENGTH_SHORT).show();
//
//            }
//        });

        btnGotoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentHome = new Intent(DataToEsp.this, MainActivity.class);

                intentHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentHome);
                finish();
            }
        });
        Button btnPost = findViewById(R.id.btn_post);
        String finalIpAddress = ipAddress;
        btnPost.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                {
                    String name = reg_name.getText().toString().trim();
                    String pass  = reg_pass.getText().toString().trim();


                    if (name.isEmpty())
                    {
                        reg_name.setError("Yêu cầu nhập tên Wifi");
                        reg_name.requestFocus();
                        return;
                    }
                    if (pass.isEmpty())
                    {
                        reg_pass.setError("Yêu cầu mật khẩu Wifi");
                        reg_pass.requestFocus();
                        return;
                    }
                    AndroidNetworking.post("http://192.168.4.1:80/post")
                            .addQueryParameter("ssid", name)
                            .addQueryParameter("pass", pass)
                            .addQueryParameter("userid", userid)
                            .build()
                            .getAsString(new StringRequestListener() {
                                @Override
                                public void onResponse(String response) {
                                    Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onError(ANError anError) {
                                    Toast.makeText(getApplicationContext(), anError.getErrorBody(), Toast.LENGTH_SHORT).show();
                                }
                            });

                }//end else firebaseAuth
            }
        });
    }

}


