package com.example.sipmobileapp.adapter;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

public class GalleryDiffUtil extends DiffUtil.Callback {
    private List<String> oldFiles, newFiles;

    public GalleryDiffUtil(List<String> oldFiles, List<String> newFiles) {
        this.oldFiles = oldFiles;
        this.newFiles = newFiles;
    }

    @Override
    public int getOldListSize() {
        return oldFiles != null ? oldFiles.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return newFiles != null ? newFiles.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return true;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldFiles.get(oldItemPosition).equals(newFiles.get(newItemPosition));
    }
}
