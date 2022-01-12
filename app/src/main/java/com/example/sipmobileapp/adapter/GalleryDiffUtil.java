package com.example.sipmobileapp.adapter;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

public class GalleryDiffUtil extends DiffUtil.Callback {
    private List<String> oldFileList, newFileList;

    public GalleryDiffUtil(List<String> oldFileList, List<String> newFileList) {
        this.oldFileList = oldFileList;
        this.newFileList = newFileList;
    }

    @Override
    public int getOldListSize() {
        return oldFileList != null ? oldFileList.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return newFileList != null ? newFileList.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return true;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldFileList.get(oldItemPosition).equals(newFileList.get(newItemPosition));
    }
}
