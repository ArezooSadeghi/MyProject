package com.example.sipmobileapp.adapter;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

public class PhotoGalleryDiffUtil extends DiffUtil.Callback {

    private List<String> oldFilePathList, newFilePathList;

    public PhotoGalleryDiffUtil(List<String> oldFilePathList, List<String> newFilePathList) {
        this.oldFilePathList = oldFilePathList;
        this.newFilePathList = newFilePathList;
    }

    @Override
    public int getOldListSize() {
        return oldFilePathList != null ? oldFilePathList.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return newFilePathList != null ? newFilePathList.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return true;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldFilePathList.get(oldItemPosition).equals(newFilePathList.get(newItemPosition));
    }
}
