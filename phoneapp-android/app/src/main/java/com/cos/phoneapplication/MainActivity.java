package com.cos.phoneapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity2";
    private Toolbar toolbarMain;
    private RecyclerView rvPhone;
    private PhoneAdapter phoneAdapter;
    private FloatingActionButton fabAddUser;
    private List<Phone> phones;
    private LinearLayout phoneItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        phones = new ArrayList<>();

        toolbarMain = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbarMain);

        rvPhone = findViewById(R.id.rv_phone);


        fabAddUser = findViewById(R.id.fab_addUser);
        fabAddUser.setOnClickListener(v -> {
            View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.adduser_item, null);

            EditText etName = dialogView.findViewById(R.id.et_add_username);
            EditText etPhone = dialogView.findViewById(R.id.et_add_userphone);

            AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
            dlg.setTitle("연락처 등록");
            dlg.setView(dialogView);
            dlg.setPositiveButton("등록", (dialog, which) -> {
                PhoneService phoneService = PhoneService.retrofit.create(PhoneService.class);
                Phone phoneEntity = new Phone(null, etName.getText().toString(), etPhone.getText().toString());
                Call<CMRespDto<Phone>> call = phoneService.save(phoneEntity);

                call.enqueue(new Callback<CMRespDto<Phone>>() {
                    @Override
                    public void onResponse(Call<CMRespDto<Phone>> call, Response<CMRespDto<Phone>> response) {
                        CMRespDto<Phone> cmRespDto = response.body();
                        Phone phone = cmRespDto.getData();
                        if (phone != null) {
                            // List에 추가
                            phones.add(phone);
                            // 어댑터에 알려주기 notify
                            phoneAdapter.notifyDataSetChanged();
                            initData();
                            rvPhone.setAdapter(phoneAdapter);
                        }
                    }

                    @Override
                    public void onFailure(Call<CMRespDto<Phone>> call, Throwable t) {
                        Log.d(TAG, "onFailure: 연락처 추가 실패");
                    }
                });
            });
            dlg.setNegativeButton("닫기", null);
            dlg.show();
        });
    }

    public void initData() {
        // 레트로핏(전체가져오기)
        PhoneService phoneService = PhoneService.retrofit.create(PhoneService.class);
        Call<CMRespDto<List<Phone>>> call = phoneService.findAll();
        call.enqueue(new Callback<CMRespDto<List<Phone>>>() {
            @Override
            public void onResponse(Call<CMRespDto<List<Phone>>> call, Response<CMRespDto<List<Phone>>> response) {
                CMRespDto<List<Phone>> cmRespDto = response.body();
                Log.d(TAG, "onResponse: cmRespDto : " + cmRespDto);
                List<Phone> phones = cmRespDto.getData();
                // 어댑터에게 넘기기
                Log.d(TAG, "onResponse: 응답 받은 데이터 : " + phones);
                LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL, false);
                rvPhone.setLayoutManager(layoutManager);

                MainActivity mainActivity = null;
                phoneAdapter = new PhoneAdapter(phones, mainActivity);
                phoneAdapter.notifyDataSetChanged();
                rvPhone.setAdapter(phoneAdapter);
            }

            @Override
            public void onFailure(Call<CMRespDto<List<Phone>>> call, Throwable t) {
                Log.d(TAG, "onFailure: findAll() 실패");
            }
        });
    }




}




