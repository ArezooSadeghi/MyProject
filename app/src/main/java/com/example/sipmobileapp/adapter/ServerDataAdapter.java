package com.example.sipmobileapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sipmobileapp.R;
import com.example.sipmobileapp.databinding.ServerDataAdapterItemBinding;
import com.example.sipmobileapp.model.ServerData;
import com.example.sipmobileapp.viewmodel.LoginViewModel;

import java.util.List;

public class ServerDataAdapter extends RecyclerView.Adapter<ServerDataAdapter.ServerDataHolder> {
    private Context context;
    private List<ServerData> serverDataList;
    private LoginViewModel viewModel;

    public ServerDataAdapter(Context context, List<ServerData> serverDataList, LoginViewModel viewModel) {
        this.context = context;
        this.serverDataList = serverDataList;
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public ServerDataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ServerDataHolder(DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.server_data_adapter_item,
                parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull ServerDataHolder holder, int position) {
        ServerData serverData = serverDataList.get(position);
        holder.bindServerData(serverData);

        holder.binding.imgViewEdit.setOnClickListener(view -> viewModel.getEditClicked().setValue(serverData));
        holder.binding.imgViewDelete.setOnClickListener(view -> viewModel.getDeleteClicked().setValue(serverData));
    }

    @Override
    public int getItemCount() {
        return serverDataList == null ? 0 : serverDataList.size();
    }

    public class ServerDataHolder extends RecyclerView.ViewHolder {
        private ServerDataAdapterItemBinding binding;

        public ServerDataHolder(ServerDataAdapterItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindServerData(ServerData serverData) {
            binding.txtCenterName.setText(serverData.getCenterName());
            binding.txtIpAddress.setText(serverData.getIp());
        }
    }
}
