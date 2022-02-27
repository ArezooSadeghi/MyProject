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
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.util.List;

public class ServerDataAdapter extends RecyclerView.Adapter<ServerDataAdapter.ServerDataHolder> {
    private Context context;
    private final LoginViewModel viewModel;
    private final List<ServerData> serverDataList;

    public ServerDataAdapter(LoginViewModel viewModel, List<ServerData> serverDataList) {
        this.viewModel = viewModel;
        this.serverDataList = serverDataList;
    }

    @NonNull
    @Override
    public ServerDataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
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
        holder.binding.ivMore.setOnClickListener(view -> {
            PowerMenu powerMenu = new PowerMenu.Builder(context)
                    .addItem(new PowerMenuItem(context.getString(R.string.edit_item_title)))
                    .addItem(new PowerMenuItem(context.getString(R.string.delete_item_title)))
                    .build();

            powerMenu.setOnMenuItemClickListener((i, item) -> {
                if (i == 0)
                    viewModel.getEditClicked().setValue(serverData);
                else if (i == 1)
                    viewModel.getDeleteClicked().setValue(serverData.getCenterName());
                powerMenu.dismiss();
            });
            powerMenu.showAsDropDown(view);
        });
    }

    @Override
    public int getItemCount() {
        return serverDataList != null ? serverDataList.size() : 0;
    }

    public class ServerDataHolder extends RecyclerView.ViewHolder {
        private final ServerDataAdapterItemBinding binding;

        public ServerDataHolder(ServerDataAdapterItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindServerData(ServerData serverData) {
            binding.tvCenterName.setText(serverData.getCenterName());
            binding.tvIp.setText(serverData.getIp());
        }
    }
}
