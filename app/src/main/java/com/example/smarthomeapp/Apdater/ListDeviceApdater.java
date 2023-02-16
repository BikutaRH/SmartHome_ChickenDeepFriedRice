package com.example.smarthomeapp.Apdater;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthomeapp.LivingRoom;
import com.example.smarthomeapp.Module.DeviceModel;
import com.example.smarthomeapp.R;
import com.example.smarthomeapp.evenbus.MyUpdateCartEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.eventbus.EventBus;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ListDeviceApdater extends RecyclerView.Adapter<ListDeviceApdater.MyViewHolder> {
    private FirebaseAuth mAuth;
    public ListDeviceApdater(Context context, ArrayList<DeviceModel> list) {
        this.context = context;
        this.list = list;
    }

    Context context;
    ArrayList<DeviceModel> list;

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView nameDevice, pin ;
        Switch adjust_width;
        ImageView btn_clear;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nameDevice = itemView.findViewById(R.id.nameDevice);
            adjust_width = itemView.findViewById(R.id.adjust_width);
            btn_clear = itemView.findViewById(R.id.btn_clear);
            pin = itemView.findViewById(R.id.pin);

        }
    }

    @NonNull
    @Override
    public ListDeviceApdater.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.load_item_device,parent,false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ListDeviceApdater.MyViewHolder holder, int position) {
        DeviceModel deviceModel = list.get(position);

        holder.adjust_width.setChecked(list.get(position).isStatus());
        holder.nameDevice.setText(list.get(position).getName());
        holder.pin.setText("PIN" + list.get(position).getId());
        holder.adjust_width.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton,
                                                 boolean b) {
                        deviceModel.setStatus(compoundButton.isChecked());
                        updateFirebase(deviceModel);
                    }
                });

        holder.btn_clear.setOnClickListener(v->{
            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle("Xóa thiết bị")
                    .setMessage("Bạn có muốn xóa thiết bị này?")
                    .setNegativeButton("HỦY BỎ", (dialogInterface, which) -> dialogInterface.dismiss())
                    .setPositiveButton("ĐỒNG Ý", (dialogInterface1, which1) -> {

                        //Temp Remove
                        notifyItemRemoved(position);

                        deleteFromFirebase(list.get(position));
                        dialogInterface1.dismiss();
                    }).create();
            dialog.show();
        });
    }

    private void deleteFromFirebase(DeviceModel deviceModel) {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseUser.getUid()).child("device").child(deviceModel.getId())
                .removeValue();
              //  .addOnSuccessListener(aVoid -> EventBus.getDefault().postSticky(new MyUpdateCartEvent()));
    }


    private void updateFirebase(DeviceModel deviceModel) {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseUser.getUid()).child("device").child(deviceModel.getId())
                .setValue(deviceModel);
//                .addOnSuccessListener(aVoid -> EventBus.getDefault().postSticky(new MyUpdateCartEvent()));
    }


    @Override
    public int getItemCount() {
        return list.size();
    }
}

