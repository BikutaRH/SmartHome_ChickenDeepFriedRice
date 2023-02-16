package com.example.smarthomeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.smarthomeapp.databinding.ActivityConfirmSyncEspBinding;
import com.example.smarthomeapp.databinding.ActivityLivingRoomBinding;
import com.example.smarthomeapp.databinding.FragmentEmailChangeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ConfirmSyncEsp extends AppCompatActivity {

    private ActivityConfirmSyncEspBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConfirmSyncEspBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.syncDataEsp.setOnClickListener(view -> {
           startActivity(new Intent(ConfirmSyncEsp.this, DataToEsp.class));
                finish();
        });

        binding.skipToLogin.setOnClickListener(view -> {
            startActivity(new Intent(ConfirmSyncEsp.this, Login.class));
            finish();
        });

    }
}