package com.example.sipmobileapp.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sipmobileapp.R;
import com.example.sipmobileapp.databinding.ServerDataItemBinding;
import com.example.sipmobileapp.model.ServerData;
import com.example.sipmobileapp.viewmodel.LoginViewModel;

import java.util.List;

public class ServerDataAdapter extends RecyclerView.Adapter<ServerDataAdapter.ServerDataHolder> {
    private final LoginViewModel viewModel;
    private final List<ServerData> serverDataList;

    public ServerDataAdapter(LoginViewModel viewModel, List<ServerData> serverDataList) {
        this.viewModel = viewModel;
        this.serverDataList = serverDataList;
    }

    @NonNull
    @Override
    public ServerDataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ServerDataHolder(DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.server_data_item,
                parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull ServerDataHolder holder, int position) {
        ServerData serverData = serverDataList.get(position);
        holder.bindServerData(serverData);
        holder.binding.imgViewEdit.setOnClickListener(view -> viewModel.getEditClicked().setValue(serverData));
        holder.binding.imgViewDelete.setOnClickListener(view -> viewModel.getDeleteClicked().setValue(serverData.getCenterName()));
    }

    @Override
    public int getItemCount() {
        return serverDataList != null ? serverDataList.size() : 0;
    }

    public class ServerDataHolder extends RecyclerView.ViewHolder {
        private final ServerDataItemBinding binding;

        public ServerDataHolder(ServerDataItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindServerData(ServerData serverData) {
            binding.txtCenterName.setText(serverData.getCenterName());
            binding.txtIpAddress.setText(serverData.getIp());
        }
    }
}
