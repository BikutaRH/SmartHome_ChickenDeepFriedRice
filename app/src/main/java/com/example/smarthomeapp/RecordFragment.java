package com.example.smarthomeapp;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smarthomeapp.Module.DeviceModel;
import com.example.smarthomeapp.databinding.FragmentHomeBinding;
import com.example.smarthomeapp.databinding.FragmentRecordBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RecordFragment extends Fragment {

    private FragmentRecordBinding binding;
    protected static final int RESULT_SPEECH = 1;
    private  List<DeviceModel> deviceModels;
    private FirebaseDatabase firebaseDatabase;
    FirebaseAuth mAuth;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRecordBinding.inflate(inflater,container,false);


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        binding.btnSpeak.setOnClickListener(view1 -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi-VN");
            try {
                startActivityForResult(intent, RESULT_SPEECH);
                binding.tvText.setText("");
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getContext(), "Thiết bị của bạn không hỗ trợ voice! ", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });


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
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        return binding.getRoot();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        switch (requestCode){
            case RESULT_SPEECH:
                if(resultCode == RESULT_OK && data != null){
                    String dataVoice = new String(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));
                    if (deviceModels.size() != 0) {
                     for (int i = 0; i < deviceModels.size(); i++) {
                            //+ deviceModels.get(i).getName()
                            if(dataVoice.contentEquals("Bật "+ deviceModels.get(i).getName())||dataVoice.contentEquals("bật "+ deviceModels.get(i).getName())){
                                deviceModels.get(i).status = true;
                                firebaseDatabase.getInstance().getReference("Users")
                                        .child(firebaseUser.getUid()).child("device")
                                        .child(deviceModels.get(i).getId()).setValue(deviceModels.get(i));
                                Toast.makeText(requireContext(), "đã bật "+ deviceModels.get(i).getName(), Toast.LENGTH_LONG).show();
                            }
                            else if(dataVoice.contentEquals("Tắt "+ deviceModels.get(i).getName())||dataVoice.contentEquals("tắt "+ deviceModels.get(i).getName())){
                                deviceModels.get(i).status = false;
                                firebaseDatabase.getInstance().getReference("Users")
                                        .child(firebaseUser.getUid()).child("device")
                                        .child(deviceModels.get(i).getId()).setValue(deviceModels.get(i));
                                Toast.makeText(requireContext(), "đã tắt "+ deviceModels.get(i).getName(), Toast.LENGTH_LONG).show();
                            }
                            else
                                Toast.makeText(requireContext(), "Không tìm thấy thiết bị!!!", Toast.LENGTH_LONG).show();
                        }
                    }
                    else
                        Toast.makeText(requireContext(), "Không tìm thấy thiết bị!!!", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

}