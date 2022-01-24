package com.example.sipmobileapp.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sipmobileapp.R;
import com.example.sipmobileapp.databinding.AttachItemBinding;

import java.util.List;

public class AttachAdapter extends RecyclerView.Adapter<AttachAdapter.AttachHolder> {
    private final List<Uri> uris;

    public AttachAdapter(List<Uri> uris) {
        this.uris = uris;
    }

    @NonNull
    @Override
    public AttachHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AttachHolder(DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.attach_item,
                parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull AttachHolder holder, int position) {
        Uri uri = uris.get(position);
        holder.bind(uri);
    }

    @Override
    public int getItemCount() {
        return uris != null ? uris.size() : 0;
    }

    public class AttachHolder extends RecyclerView.ViewHolder {
        private final AttachItemBinding binding;

        public AttachHolder(AttachItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Uri uri) {
            Glide.with(binding.getRoot().getContext()).load(uri).into(binding.ivAttach);
        }
    }
}
