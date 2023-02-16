package com.example.smarthomeapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.smarthomeapp.Module.DeviceModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddDevice extends AppCompatActivity implements View.OnClickListener {

    Button addWidgetSwitch;
    Button confirmAdd;
    Button[] dButton = new Button[9];
    int[] buttonIds = {R.id.D0, R.id.D1, R.id.D2, R.id.D3, R.id.D4, R.id.D5, R.id.D6, R.id.D7, R.id.D8};
    EditText widgetNameAdd;
    FirebaseAuth mAuth;

    String widgetName_s="";
    String espPin_s="";
    String widgetType;
    private List<DeviceModel> deviceModels;

    FirebaseDatabase rootNode;
    DatabaseReference reference;
    int checkbutton = 0, flatButton = 100, checkSwitch = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);

        deviceModels = new ArrayList<>();
        addWidgetSwitch = findViewById(R.id.addWidgeSwitch);
        widgetNameAdd = findViewById(R.id.widgetNameAdd);
        confirmAdd = findViewById(R.id.confirmAdd);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        for(int i=0;i<9;i++) {
            dButton[i] = findViewById(buttonIds[i]);
        }

        //espPin = findViewById(R.id.espPin);
        addWidgetSwitch.setBackgroundColor(Color.GRAY);
        addWidgetSwitch.setOnClickListener(this);
        confirmAdd.setOnClickListener(this);
        for(int i=0;i<9;i++) {
            dButton[i].setOnClickListener(this);
            dButton[i].setBackgroundColor(Color.GRAY);
        }

        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("Users")
                .child(firebaseUser.getUid()).child("device");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
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
    }
    @SuppressLint("ResourceType")
    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.confirmAdd){
            widgetName_s = widgetNameAdd.getText().toString();
            widgetNameAdd.setText("");//clear the field when button pushed'
            if(validInput())
                gobackMainMenu(view);
        }

        if((view.getId() == R.id.addWidgeSwitch) && checkSwitch == 0){
            widgetType = "button";
            addWidgetSwitch.setBackgroundColor(Color.GREEN);
            checkSwitch = 1;
        }
        else if((view.getId() == R.id.addWidgeSwitch) && checkSwitch == 1){
            checkSwitch =  0;
            addWidgetSwitch.setBackgroundColor(Color.GRAY);
            widgetType = "";
        }

        for(int i=0; i<9;i++){
            if((view.getId() == buttonIds[i]) && checkbutton == 0){
                flatButton = i;
                checkbutton = 1;
                dButton[i].setBackgroundColor(Color.GREEN);
                espPin_s = ""+i;
            }
            else if((view.getId() == buttonIds[i]) && checkbutton == 1 && flatButton == i)
            {
                flatButton = 100;
                checkbutton = 0;
                dButton[i].setBackgroundColor(Color.GRAY);
                espPin_s = "";
            }
            if(view.getId() == buttonIds[i] && flatButton != i)
            {
                return;
            }
        }

    }
    private boolean validInput(){
        for(int i=0; i < deviceModels.size(); i++){

            if(deviceModels.get(i).getName().equals(widgetName_s)){
                Toast.makeText(this, "Tên đã tồn tại, chọn lại!!!", Toast.LENGTH_SHORT).show();
                return false;
            }
            if(deviceModels.get(i).getId().equals(espPin_s)){
                Toast.makeText(this, "Chân đã chọn rồi, chọn lại!!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if(widgetName_s.isEmpty()){
            Toast.makeText(this, "Tên không được bỏ trống!!", Toast.LENGTH_SHORT).show();
            return false;}
        else if(widgetType.isEmpty()){
                Toast.makeText(this, "Vui lòng chọn kiểu thiết bị!!", Toast.LENGTH_SHORT).show();
                return false;
        }else if(espPin_s.isEmpty()){
            Toast.makeText(this, "Hãy chọn 1 chân !", Toast.LENGTH_SHORT).show();
            return false;
        }else return true;
    }

    public void gobackMainMenu(View view){

        DeviceModel deviceModel =new DeviceModel( widgetName_s, espPin_s ,widgetType, true);
        reference.child(espPin_s).setValue(deviceModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // hide virtual keyboard
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                //--------------push data to MainMenu acctivity via username------------
                Intent gtoLivingroom =new Intent(AddDevice.this, LivingRoom.class);
                //intent.putExtra("username",MainActivity.user_username_gadget);
                gtoLivingroom.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(gtoLivingroom);
                //--------------end push data to MainMenu acctivity via username------------
            }
        });


    }
}