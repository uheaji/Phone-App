package com.cos.phoneapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhoneAdapter extends RecyclerView.Adapter<PhoneAdapter.MyViewHolder> {

    private static List<Phone> phones = new ArrayList<>();
    private  MainActivity mainActivity;

    public PhoneAdapter(List<Phone> phones, MainActivity mainActivity) {
        this.phones = phones;
    }

    public void addItem(Phone phone){
        phones.add(phone);
        notifyDataSetChanged();
    }

    public void removeItem(int position){
        phones.remove(position);
        notifyDataSetChanged();
    }

    public void updateItem(String name, String tel, int position){
        Phone phone = phones.get(position);
        phone.setName(name);
        phone.setTel(tel);
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.phone_item, parent, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Phone phone = phones.get(position);
        holder.setItem(phones.get(position));

        holder.itemView.setOnClickListener(v -> {
            View dialogView = v.inflate(v.getContext(), R.layout.updateuser_item,null);

            final EditText etName = dialogView.findViewById(R.id.et_update_username);
            final EditText etPhone = dialogView.findViewById(R.id.et_update_userphone);

            etName.setText(phone.getName());
            etPhone.setText(phone.getTel());

            AlertDialog.Builder dlg = new AlertDialog.Builder(v.getContext());
            dlg.setTitle("연락처 수정");
            dlg.setView(dialogView);
            dlg.setPositiveButton("수정", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    PhoneSaveReqDto phoneSaveReqDto = new PhoneSaveReqDto();
                    phoneSaveReqDto.setName(etName.getText().toString());
                    phoneSaveReqDto.setTel(etPhone.getText().toString());

                    PhoneService phoneService = PhoneService.retrofit.create(PhoneService.class);
                    Call<PhoneSaveReqDto> call = phoneService.update(phone.getId(),phoneSaveReqDto);

                    call.enqueue(new Callback<PhoneSaveReqDto>() {
                        @Override
                        public void onResponse(Call<PhoneSaveReqDto> call, Response<PhoneSaveReqDto> response) {
                            updateItem(phoneSaveReqDto.getName(),phoneSaveReqDto.getTel(),position);
                        }

                        @Override
                        public void onFailure(Call<PhoneSaveReqDto> call, Throwable t) {

                        }
                    });
                }
            });
            dlg.setNegativeButton("삭제", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    PhoneService phoneService = PhoneService.retrofit.create(PhoneService.class);
                    Call<CMRespDto> call = phoneService.delete(phone.getId());

                    call.enqueue(new Callback<CMRespDto>() {
                        @Override
                        public void onResponse(Call<CMRespDto> call, Response<CMRespDto> response) {
                            removeItem(position);
                            CMRespDto result = response.body();

                        }

                        @Override
                        public void onFailure(Call<CMRespDto> call, Throwable t) {

                        }
                    });
                }
            });
            dlg.show();

        });
    }

    @Override
    public int getItemCount() {
        return phones.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView name, tel;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            tel = itemView.findViewById(R.id.tel);
        }

        public void setItem(Phone phone) {
            name.setText(phone.getName());
            tel.setText(phone.getTel());

        }
    }

}
