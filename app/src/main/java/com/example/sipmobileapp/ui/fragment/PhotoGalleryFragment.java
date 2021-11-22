package com.example.sipmobileapp.ui.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.sipmobileapp.R;
import com.example.sipmobileapp.adapter.PhotoGalleryAdapter;
import com.example.sipmobileapp.databinding.FragmentPhotoGalleryBinding;
import com.example.sipmobileapp.event.DeleteEvent;
import com.example.sipmobileapp.event.RefreshEvent;
import com.example.sipmobileapp.model.AttachResult;
import com.example.sipmobileapp.model.ServerData;
import com.example.sipmobileapp.ui.activity.AttachmentContainerActivity;
import com.example.sipmobileapp.ui.activity.FullScreenPhotoContainerActivity;
import com.example.sipmobileapp.ui.dialog.ErrorDialogFragment;
import com.example.sipmobileapp.utils.SipMobileAppPreferences;
import com.example.sipmobileapp.viewmodel.AttachmentViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PhotoGalleryFragment extends Fragment {
    private FragmentPhotoGalleryBinding binding;
    private AttachmentViewModel viewModel;
    private ServerData serverData;
    private String centerName, userLoginKey;
    private PhotoGalleryAdapter adapter;
    private List<String> oldFilePathList, newFilePathList;
    private List<Integer> attachIDList;
    private int sickID, index;

    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 0;
    private static final int SPAN_COUNT = 4;

    private static final String ARGS_SICK_ID = "sickID";
    private static final String TAG = PhotoGalleryFragment.class.getSimpleName();

    public static PhotoGalleryFragment newInstance(int sickID) {
        PhotoGalleryFragment fragment = new PhotoGalleryFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_SICK_ID, sickID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createViewModel();
        initVariables();

        if (hasWriteExternalStoragePermission()) {
            fetchPatientAttachments(sickID);
        } else {
            requestWriteExternalStoragePermission();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_photo_gallery,
                container,
                false);

        initViews();
        handleEvents();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupObserver();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(sticky = true)
    public void getDeleteEvent(DeleteEvent event) {
        String filePath = "";
        File dir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Attachments");
        if (dir.exists()) {
            File[] files = dir.listFiles();
            for (File file : files) {
                if (file.getName().equals(event.getAttachID() + ".jpg")) {
                    filePath = file.getPath();
                    file.delete();
                    break;
                }
            }
        }

        for (String fPath : newFilePathList) {
            if (!filePath.isEmpty()) {
                if (fPath.equals(filePath)) {
                    newFilePathList.remove(fPath);
                    break;
                }
            }
        }

        binding.progressBarLoading.setVisibility(View.GONE);
        binding.recyclerViewAttachmentFile.setVisibility(View.VISIBLE);
        setupAdapter();
        EventBus.getDefault().removeStickyEvent(event);
    }

    @Subscribe(sticky = true)
    public void getRefreshEvent(RefreshEvent event) {
        int attachID = event.getAttachID();
        fetchAttachInfo(attachID);
        EventBus.getDefault().removeStickyEvent(event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION) {
            if (grantResults.length == 0) {
                return;
            }
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (sickID != 0) {
                    fetchPatientAttachments(sickID);
                }
            } else {
                handleError(getString(R.string.storage_access_denied_message));
            }
        }
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(AttachmentViewModel.class);
    }

    private void initVariables() {
        centerName = SipMobileAppPreferences.getCenterName(getContext());
        serverData = viewModel.getServerData(centerName);
        userLoginKey = SipMobileAppPreferences.getUserLoginKey(getContext());
        sickID = getArguments().getInt(ARGS_SICK_ID);
        oldFilePathList = new ArrayList<>();
        newFilePathList = new ArrayList<>();
        attachIDList = new ArrayList<>();
    }

    private void initViews() {
        binding.recyclerViewAttachmentFile.setLayoutManager(new GridLayoutManager(getContext(), SPAN_COUNT));
    }

    private boolean hasWriteExternalStoragePermission() {
        return (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestWriteExternalStoragePermission() {
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
    }

    private void fetchPatientAttachments(int sickID) {
        viewModel.getServicePatientResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/attachs/ListBySickID/";
        viewModel.fetchPatientAttachments(path, userLoginKey, sickID);
    }

    private void fetchAttachInfo(int attachID) {
        viewModel.getServiceAttachResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/attachs/Info/";
        viewModel.fetchAttachInfo(path, userLoginKey, attachID);
    }

    private String readFromStorage(int attachID) {
        File dir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Attachments");
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files.length != 0) {
                for (File file : files) {
                    if (file.getName().equals(attachID + ".jpg")) {
                        if (adapter == null) {
                            oldFilePathList.add(file.getPath());
                            newFilePathList.addAll(oldFilePathList);
                        } else {
                            newFilePathList.add(file.getPath());
                        }
                        return file.getPath();
                    }
                }
            }
        }
        return "";
    }

    private String writeToExternalStorage(AttachResult.AttachInfo attachInfo) throws IOException {
        File dir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Attachments");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dir, attachInfo.getAttachID() + ".jpg");
        if (attachInfo.getFileBase64() != null && attachInfo.getDeleteUserID() == 0) {
            byte[] bytes = Base64.decode(attachInfo.getFileBase64(), 0);
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            fileOutputStream.write(bytes);
            fileOutputStream.flush();
            fileOutputStream.close();
            if (adapter == null) {
                oldFilePathList.add(file.getPath());
                newFilePathList.addAll(oldFilePathList);
            } else {
                newFilePathList.add(file.getPath());
            }
            return file.getPath();
        } else {
            return "";
        }
    }

    private void setupAdapter() {
        if (adapter == null) {
            adapter = new PhotoGalleryAdapter(getContext(), viewModel, oldFilePathList);
        } else {
            adapter.updateFilePathList(newFilePathList);
        }
        binding.recyclerViewAttachmentFile.setAdapter(adapter);
    }

    private void handleError(String message) {
        ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
        fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
    }

    private void showAttachments(AttachResult attachResult) {
        if (attachResult.getAttachs().length == 0) {
            binding.progressBarLoading.setVisibility(View.GONE);
        } else {
            for (AttachResult.AttachInfo attachInfo : attachResult.getAttachs()) {
                String filePath = readFromStorage(attachInfo.getAttachID());
                if (!filePath.isEmpty()) {
                    binding.progressBarLoading.setVisibility(View.GONE);
                    binding.recyclerViewAttachmentFile.setVisibility(View.VISIBLE);
                    setupAdapter();
                } else {
                    attachIDList.add(attachInfo.getAttachID());
                }
            }

            if (attachIDList.size() != 0) {
                fetchAttachInfo(attachIDList.get(index));
            }
        }
    }

    private void handleEvents() {
        binding.fabAddNewFile.setOnClickListener(view -> {
            Intent starter = AttachmentContainerActivity.start(getContext(), sickID);
            startActivity(starter);
        });
    }

    private void setupObserver() {
        viewModel.getPatientAttachmentsResultSingleLiveEvent().observe(getViewLifecycleOwner(), attachResult -> {
            if (attachResult != null) {
                if (attachResult.getErrorCode().equals("0")) {
                    showAttachments(attachResult);
                } else {
                    binding.progressBarLoading.setVisibility(View.GONE);
                    handleError(attachResult.getError());
                }
            }
        });

        viewModel.getAttachInfoResultSingleLiveEvent().observe(getViewLifecycleOwner(), attachResult -> {
            if (attachResult != null) {
                if (attachResult.getErrorCode().equals("0")) {
                    if (attachResult.getAttachs().length != 0) {
                        AttachResult.AttachInfo attachInfo = attachResult.getAttachs()[0];
                        new Thread(() -> {
                            try {
                                String filePath = writeToExternalStorage(attachInfo);
                                viewModel.getFinishWriteToStorage().postValue(filePath);
                            } catch (IOException e) {
                                Log.e(TAG, e.getMessage());
                            }
                        }).start();
                    }
                } else {
                    binding.progressBarLoading.setVisibility(View.GONE);
                    handleError(attachResult.getError());
                }
            }
        });

        viewModel.getNoConnectionExceptionHappenSingleLiveEvent().observe(getViewLifecycleOwner(), message -> {
            binding.progressBarLoading.setVisibility(View.GONE);
            handleError(message);
        });

        viewModel.getTimeoutExceptionHappenSingleLiveEvent().observe(getViewLifecycleOwner(), message -> {
            binding.progressBarLoading.setVisibility(View.GONE);
            handleError(message);
        });

        viewModel.getFinishWriteToStorage().observe(getViewLifecycleOwner(), filePath -> {
            if (!filePath.isEmpty()) {
                binding.progressBarLoading.setVisibility(View.GONE);
                binding.recyclerViewAttachmentFile.setVisibility(View.VISIBLE);
                setupAdapter();
            }
            index++;
            if (index < attachIDList.size()) {
                fetchAttachInfo(attachIDList.get(index));
            } else {
                binding.progressBarLoading.setVisibility(View.GONE);
            }
        });

        viewModel.getPhotoClicked().observe(getViewLifecycleOwner(), filePath -> {
            File file = new File(filePath);
            String fileName = file.getName().replace(".jpg", "");
            int attachID = Integer.parseInt(fileName);
            Intent starter = FullScreenPhotoContainerActivity.start(getContext(), filePath, attachID);
            startActivity(starter);
        });
    }
}