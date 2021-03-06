package com.example.sipmobileapp.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sipmobileapp.R;
import com.example.sipmobileapp.databinding.GalleryItemBinding;
import com.example.sipmobileapp.viewmodel.AttachmentViewModel;

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryHolder> {
    private final AttachmentViewModel viewModel;
    private final List<String> files;

    public GalleryAdapter(AttachmentViewModel viewModel, List<String> files) {
        this.viewModel = viewModel;
        this.files = files;
    }

    @NonNull
    @Override
    public GalleryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GalleryHolder(DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.gallery_item,
                parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryHolder holder, int position) {
        String file = files.get(position);
        holder.bindFile(file);
        holder.binding.getRoot().setOnClickListener(v -> viewModel.getPhotoClicked().setValue(file));
    }

    @Override
    public int getItemCount() {
        return files != null ? files.size() : 0;
    }

    public void updateFileList(List<String> newFileList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new GalleryDiffUtil(files, newFileList));
        diffResult.dispatchUpdatesTo(this);
        files.clear();
        files.addAll(newFileList);
    }

    public class GalleryHolder extends RecyclerView.ViewHolder {
        private final GalleryItemBinding binding;

        public GalleryHolder(GalleryItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindFile(String file) {
            Bitmap bitmap = BitmapFactory.decodeFile(file);
            if (bitmap != null) {
                Glide.with(binding.getRoot().getContext()).load(bitmap).into(binding.ivAttach);
            }
        }
    }
}
