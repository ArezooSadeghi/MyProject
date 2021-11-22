package com.example.sipmobileapp.adapter;

import android.content.Context;
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
import com.example.sipmobileapp.databinding.PhotoGalleryAdapterItemBinding;
import com.example.sipmobileapp.viewmodel.AttachmentViewModel;

import java.util.List;

public class PhotoGalleryAdapter extends RecyclerView.Adapter<PhotoGalleryAdapter.PhotoGalleryHolder> {
    private Context context;
    private AttachmentViewModel viewModel;
    private List<String> filePathList;

    public PhotoGalleryAdapter(Context context, AttachmentViewModel viewModel, List<String> filePathList) {
        this.context = context;
        this.viewModel = viewModel;
        this.filePathList = filePathList;
    }

    @NonNull
    @Override
    public PhotoGalleryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PhotoGalleryHolder(DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.photo_gallery_adapter_item,
                parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoGalleryHolder holder, int position) {
        String filePath = filePathList.get(position);
        holder.bindFilePath(filePath);
        holder.binding.getRoot().setOnClickListener(v -> viewModel.getPhotoClicked().setValue(filePath));
    }

    @Override
    public int getItemCount() {
        return filePathList != null ? filePathList.size() : 0;
    }

    public void updateFilePathList(List<String> newFilePathList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new PhotoGalleryDiffUtil(filePathList, newFilePathList));
        filePathList.clear();
        filePathList.addAll(newFilePathList);
    }

    public class PhotoGalleryHolder extends RecyclerView.ViewHolder {
        private PhotoGalleryAdapterItemBinding binding;

        public PhotoGalleryHolder(PhotoGalleryAdapterItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindFilePath(String filePath) {
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            if (bitmap != null) {
                Glide.with(context).load(bitmap).into(binding.ivPhoto);
            }
        }
    }
}
