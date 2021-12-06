package com.example.sipmobileapp.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.sipmobileapp.R;
import com.example.sipmobileapp.adapter.ServerDataAdapter;
import com.example.sipmobileapp.databinding.FragmentServerDataListDialogBinding;
import com.example.sipmobileapp.model.ServerData;
import com.example.sipmobileapp.viewmodel.LoginViewModel;

import java.util.List;

public class ServerDataListDialogFragment extends DialogFragment {
    private FragmentServerDataListDialogBinding mBinding;
    private LoginViewModel viewModel;

    public static final String TAG = ServerDataListDialogFragment.class.getSimpleName();

    public static ServerDataListDialogFragment newInstance() {
        ServerDataListDialogFragment fragment = new ServerDataListDialogFragment();
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

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(
                getContext()),
                R.layout.fragment_server_data_list_dialog,
                null,
                false);

        initViews();
        setupAdapter();
        handleEvents();

        return new AlertDialog.Builder(getContext())
                .setView(mBinding.getRoot())
                .create();
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
    }

    private void initViews() {
        mBinding.recyclerViewServerData.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.recyclerViewServerData.addItemDecoration(new DividerItemDecoration(
                mBinding.recyclerViewServerData.getContext(),
                DividerItemDecoration.VERTICAL));
    }

    private void setupAdapter() {
        List<ServerData> serverDataList = viewModel.getServerDataList();
        ServerDataAdapter adapter = new ServerDataAdapter(getContext(), serverDataList, viewModel);
        mBinding.recyclerViewServerData.setAdapter(adapter);
    }

    private void handleEvents() {
        mBinding.fabAddServerData.setOnClickListener(view -> {
            AddEditServerDataDialogFragment fragment = AddEditServerDataDialogFragment.newInstance("", "", "", true);
            fragment.show(getChildFragmentManager(), AddEditServerDataDialogFragment.TAG);
        });
    }

    private void setupObserver() {
        viewModel.getInsertNotifyServerDataList().observe(this, isServerDataInsert -> setupAdapter());

        viewModel.getEditClicked().observe(this, serverData -> {
            AddEditServerDataDialogFragment fragment = AddEditServerDataDialogFragment.newInstance(serverData.getCenterName(), serverData.getIpAddress(), serverData.getPort(), false);
            fragment.show(getChildFragmentManager(), AddEditServerDataDialogFragment.TAG);
        });

        viewModel.getDeleteClicked().observe(this, serverData -> {
            viewModel.deleteServerData(serverData);
            viewModel.getDeleteNotifySpinner().setValue(true);
            setupAdapter();
            List<ServerData> serverDataList = viewModel.getServerDataList();
            if (serverDataList.size() == 0) {
                WarningDialogFragment fragment = WarningDialogFragment.newInstance(getString(R.string.required_ip));
                fragment.show(getParentFragmentManager(), WarningDialogFragment.TAG);
                dismiss();
            }
        });
    }
}