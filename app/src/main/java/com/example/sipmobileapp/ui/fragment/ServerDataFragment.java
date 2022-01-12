package com.example.sipmobileapp.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.sipmobileapp.R;
import com.example.sipmobileapp.adapter.ServerDataAdapter;
import com.example.sipmobileapp.databinding.FragmentServerDataBinding;
import com.example.sipmobileapp.model.ServerData;
import com.example.sipmobileapp.ui.dialog.AddEditServerDataDialogFragment;
import com.example.sipmobileapp.viewmodel.AttachmentViewModel;
import com.example.sipmobileapp.viewmodel.LoginViewModel;

import java.util.List;

public class ServerDataFragment extends Fragment {
    private FragmentServerDataBinding binding;
    private LoginViewModel viewModel;
    private AttachmentViewModel attachmentViewModel;

    public static final String TAG = ServerDataFragment.class.getSimpleName();

    public static ServerDataFragment newInstance() {
        ServerDataFragment fragment = new ServerDataFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createViewModel();
        setupObserver();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(
                getContext()),
                R.layout.fragment_server_data,
                null,
                false);

        initViews();
        handleEvents();

        return binding.getRoot();
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
        attachmentViewModel = new ViewModelProvider(requireActivity()).get(AttachmentViewModel.class);
    }

    private void initViews() {
        binding.recyclerViewServerData.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewServerData.addItemDecoration(new DividerItemDecoration(binding.recyclerViewServerData.getContext(), DividerItemDecoration.VERTICAL));
    }

    private void setupAdapter(List<ServerData> serverDataList) {
        ServerDataAdapter adapter = new ServerDataAdapter(viewModel, serverDataList);
        binding.recyclerViewServerData.setAdapter(adapter);
    }

    private void handleEvents() {
        binding.fabAdd.setOnClickListener(view -> {
            AddEditServerDataDialogFragment fragment = AddEditServerDataDialogFragment.newInstance("", "", "", true);
            fragment.show(getChildFragmentManager(), AddEditServerDataDialogFragment.TAG);
        });
    }

    private void setupObserver() {
        viewModel.getEditClicked().observe(this, serverData -> {
            AddEditServerDataDialogFragment fragment = AddEditServerDataDialogFragment.newInstance(serverData.getCenterName(), serverData.getIp(), serverData.getPort(), false);
            fragment.show(getChildFragmentManager(), AddEditServerDataDialogFragment.TAG);
        });

        viewModel.getDeleteClicked().observe(this, serverDataTwo -> {
            viewModel.delete(serverDataTwo.getCenterName());
        });

        viewModel.getServerDataListMutableLiveData().observe(this, serverDataList -> {
            setupAdapter(serverDataList);
            if (serverDataList == null || serverDataList.size() == 0) {
                NavHostFragment.findNavController(this).navigate(R.id.loginFragment);
            }
        });
    }
}