package com.naufal222102523.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class CuacaMainActivity extends AppCompatActivity {
    private EditText _etKota;
    private MaterialButton _btnTampilkan, _buttonViewCityInfo;
    private RecyclerView _recycleView1;
    private CuacaRootModel _rootModel;
    private SwipeRefreshLayout _swipeRefreshLayout1;
    private TextView _totalTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cuaca_main);

        _etKota = findViewById(R.id.etKota);
        _recycleView1 = findViewById(R.id.recycleView1);
        _totalTextView = findViewById(R.id.totalTextView);

        initSwipeRefreshLayout();
        initButtonViewCityInfo();
        initTampilkanButton();

        bindRecycleView1();
    }
    private void initButtonViewCityInfo(){
        _buttonViewCityInfo = findViewById(R.id.buttonView_cityInfo);

        _buttonViewCityInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CuacaCityModel cm = _rootModel.getCityModel();
                CuacaCoordModel com = cm.getCoordModel();
                double latitude = com.getLat();
                double longitude = com.getLon();

                Bundle param = new Bundle();
                param.putDouble("lat", latitude);
                param.putDouble("lon", longitude);

                Intent intent = new Intent(CuacaMainActivity.this, GpsActivity.class);
                intent.putExtra("param", param);
                startActivity(intent);
            }
        });
    }
    private void initSwipeRefreshLayout(){
        _swipeRefreshLayout1 = findViewById(R.id.swipeRefreshLayout1);

        _swipeRefreshLayout1.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                bindRecycleView1();
                _swipeRefreshLayout1.setRefreshing(false);
            }
        });
    }

    private void bindRecycleView1(){
        String namakota = _etKota.getText().toString().trim();
        if (namakota.isEmpty()){
            Toast.makeText(CuacaMainActivity.this, "Nama kota tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            namakota = URLEncoder.encode(namakota, "utf-8");
        } catch (UnsupportedEncodingException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        _buttonViewCityInfo.setText("Memuat...");

        String url = "https://api.openweathermap.org/data/2.5/forecast?q=" + namakota + "&appid=9e011a7ab7be1fea0641b7f39b848c33";
        Log.d("*tw*", url);

        AsyncHttpClient ahc = new AsyncHttpClient();
        ahc.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Gson gson = new Gson();
                _rootModel = gson.fromJson(new String(responseBody), CuacaRootModel.class);

                initCityInfo();
                RecyclerView.LayoutManager lm = new LinearLayoutManager(CuacaMainActivity.this);
                _recycleView1.setLayoutManager(lm);

                CuacaAdapter ca = new CuacaAdapter(_rootModel);
                _recycleView1.setAdapter(ca);

                _totalTextView.setText("Total Record : " + ca.getItemCount());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initCityInfo(){
        CuacaCityModel cm = _rootModel.getCityModel();
        long sunrise = cm.getSunrise();
        long sunset = cm.getSunset();
        String cityName = cm.getName();

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String sunriseTime = sdf.format(new Date(sunrise * 1000));
        String sunsetTime = sdf.format(new Date(sunset * 1000));

        String cityInfo = "kota: " + cityName + "\n" +
                "Matahari Terbit: " + sunriseTime + "(Lokal)\n" +
                "Matahari Terbenam: " + sunsetTime + "(Lokal)";

        _buttonViewCityInfo.setText(cityInfo);
    }

    private void initTampilkanButton(){
        _btnTampilkan = findViewById(R.id.btnTampilkan);

        _btnTampilkan.setOnClickListener(V -> {
          bindRecycleView1();
        });
    }
}