package com.naufal222102523.login;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class ForexAdapter extends RecyclerView.Adapter<ForexViewHolder> {
    private List<ForexModel> _forexModelList;

    public ForexAdapter(List<ForexModel> forexModelList) {
        this._forexModelList = forexModelList;
    }

    @NonNull
    @Override
    public ForexViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_forex, parent, false);
        return new ForexViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForexViewHolder holder, int position) {
        ForexModel item = _forexModelList.get(position);

        holder.kodeTextView.setText(item.code);
        holder.namaTextView.setText(item.name);
        holder.kursTextView.setText(String.format(Locale.US, "%,.2f", item.rate));
    }

    @Override
    public int getItemCount() {
        return _forexModelList.size();
    }
}
