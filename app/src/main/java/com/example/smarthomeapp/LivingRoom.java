package com.example.smarthomeapp;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthomeapp.Apdater.ListDeviceApdater;
import com.example.smarthomeapp.Module.DeviceModel;
import com.example.smarthomeapp.Module.ResDHT11;
import com.example.smarthomeapp.databinding.ActivityLivingRoomBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LivingRoom extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DatabaseReference databaseDevice,databaseDTH11;
    FirebaseAuth mAuth;
    ListDeviceApdater listDeviceApdater;
    ArrayList<DeviceModel> list;
   // private String nameRoom;
    private ActivityLivingRoomBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLivingRoomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        binding.regBack.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        binding.addDevice.setOnClickListener(view -> {
            Intent gtoAddDevice = new Intent(this, AddDevice.class);
            startActivity(gtoAddDevice);
        });

        recyclerView  = findViewById(R.id.loadDeviceRecyclerView);

        databaseDevice = FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseUser.getUid()).child("device");

        databaseDTH11  = FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseUser.getUid()).child("DHT11");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.getLayoutManager().setMeasurementCacheEnabled(false);

        list = new ArrayList<>();

        listDeviceApdater = new ListDeviceApdater(LivingRoom.this, list);
        recyclerView.setAdapter(listDeviceApdater);

        // DHT11
        databaseDTH11.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    ResDHT11 resDHT11 = snapshot.getValue(ResDHT11.class);

                    binding.nhietDoDht11.setText(Float.toString(resDHT11.getNhietdo()) + " C");
                    binding.doAmDht11.setText(Float.toString(resDHT11.getDoam()) + " %");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //get Device
        databaseDevice.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    DeviceModel deviceModel = dataSnapshot.getValue(DeviceModel.class);
                    list.add(deviceModel);
                }
                listDeviceApdater.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

}