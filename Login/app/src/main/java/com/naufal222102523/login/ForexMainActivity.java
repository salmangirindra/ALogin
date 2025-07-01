package com.naufal222102523.login;

import android.os.Bundle;
import android.util.Log;
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

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;

public class ForexMainActivity extends AppCompatActivity {
    private SwipeRefreshLayout _swipeRefreshLayout1;
    private RecyclerView _recyclerView1;
    private TextView _timestampTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forex_main);

        initSwipeRefreshLayout();
        _recyclerView1 = findViewById(R.id.recycleView1);
        _timestampTextView = findViewById(R.id.timestampTextView);

        bindRecyclerView();
    }

    private void bindRecyclerView(){
        String ratesUrl = "https://openexchangerates.org/api/latest.json?app_id=682a27ba0b99472a8fd8389a7276c9a8";
        String currencyUrl = "https://openexchangerates.org/api/currencies.json";

        AsyncHttpClient client = new AsyncHttpClient();
        Log.d("*tw*", "accessing" + ratesUrl);

        client.get(ratesUrl, new AsyncHttpResponseHandler() {
            JSONObject ratesObj;
            long timestamp;
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject root = new JSONObject(new String(responseBody));
                    ratesObj = root.getJSONObject("rates");
                    timestamp = root.getLong("timestamp");

                    double idrRate = ratesObj.getDouble("IDR");

                    client.get(currencyUrl, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            try {
                                JSONObject currencies = new JSONObject(new String(responseBody));
                                List<ForexModel> forexList = new ArrayList<>();

                                Iterator<String> keys = ratesObj.keys();
                                while (keys.hasNext()) {
                                    String code = keys.next();
                                    if (code.equals("IDR")) continue;

                                    try {
                                        double rate = idrRate / ratesObj.getDouble(code);
                                        String name = currencies.optString(code, "Unknown");
                                        forexList.add(new ForexModel(code, name, rate));
                                    } catch (JSONException e) {
                                        Log.e("ForexError", "Gagal parsing untuk" + code, e);
                                    }
                                }
                                setTimestamp(timestamp);

                                ForexAdapter adapter = new ForexAdapter(forexList);
                                _recyclerView1.setLayoutManager(new LinearLayoutManager(ForexMainActivity.this));
                                _recyclerView1.setAdapter(adapter);

                            } catch (JSONException e) {
                                Toast.makeText(ForexMainActivity.this, "Gagal parsing mata uang", Toast.LENGTH_SHORT).show();
                            }

                            _swipeRefreshLayout1.setRefreshing(false);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Toast.makeText(ForexMainActivity.this, "Gagal ambil nama mata uang", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (JSONException e){
                    Toast.makeText(ForexMainActivity.this, "Gagal parsing kurs", Toast.LENGTH_SHORT).show();
                    _swipeRefreshLayout1.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("*tw*", error.getMessage());

                Toast.makeText(ForexMainActivity.this, "Gagal ambil kurs", Toast.LENGTH_SHORT).show();
                _swipeRefreshLayout1.setRefreshing(false);
            }
        });
    }

    private void setTimestamp(long timestamp){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("Asia/Jakarta"));
        String dateTime = format.format(new Date(timestamp * 1000));
        _timestampTextView.setText("Tanggal dan Waktu: " + dateTime);
    }

    private void initSwipeRefreshLayout(){
        _swipeRefreshLayout1 = findViewById(R.id.swipeRefreshLayout1);
        _swipeRefreshLayout1.setOnRefreshListener(this::bindRecyclerView);
    }
}