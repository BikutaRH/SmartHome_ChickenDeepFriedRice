package com.example.smarthomeapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.smarthomeapp.Module.DeviceModel;
import com.example.smarthomeapp.Module.ResDHT11;
import com.example.smarthomeapp.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseDTH11, dataUser;
    private List<DeviceModel> deviceModels;
    FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater,container,false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();


        deviceModels = new ArrayList<>();
        firebaseDatabase.getInstance().getReference("Users")
                .child(firebaseUser.getUid()).child("device")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for(DataSnapshot drinkSnapshot:snapshot.getChildren()){
                                DeviceModel deviceModel = drinkSnapshot.getValue(DeviceModel.class);
                                deviceModels.add(deviceModel);

                            }
                            loadCountStatusOn(deviceModels);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        databaseDTH11  = FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseUser.getUid()).child("DHT11");
        // DHT11
        databaseDTH11.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    ResDHT11 resDHT11 = snapshot.getValue(ResDHT11.class);

                    binding.doAm.setText(Float.toString(resDHT11.getDoam()) + " %");
                    binding.nhietDo.setText("H: "+ Float.toString(resDHT11.getNhietdo())+" - L: 10 C");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //name user
        dataUser = FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseUser.getUid());
        dataUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User userModel = snapshot.getValue(User.class);
                    binding.nameUser.setText("Chào " + userModel.getUsername());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        binding.livingroom.setOnClickListener(view -> {
            Intent intent = new Intent(requireContext(), LivingRoom.class);
            startActivity(intent);
        });

        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    private void loadCountStatusOn(List<DeviceModel> deviceModels) {
        int statusSum =0;
        for(DeviceModel deviceModel: deviceModels)
        {
            if (deviceModel.isStatus()==true)
                statusSum ++;
        }
       binding.statusDevice.setText(statusSum + " đang hoạt động");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}