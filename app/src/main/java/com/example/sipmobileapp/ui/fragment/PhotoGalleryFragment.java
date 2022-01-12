package com.example.sipmobileapp.ui.fragment;

import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.sipmobileapp.R;
import com.example.sipmobileapp.adapter.PhotoGalleryAdapter;
import com.example.sipmobileapp.databinding.FragmentPhotoGalleryBinding;
import com.example.sipmobileapp.model.AttachResult;
import com.example.sipmobileapp.model.ServerData;
import com.example.sipmobileapp.ui.dialog.ErrorDialogFragment;
import com.example.sipmobileapp.utils.SipMobileAppPreferences;
import com.example.sipmobileapp.viewmodel.AttachmentViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PhotoGalleryFragment extends Fragment {
    private FragmentPhotoGalleryBinding binding;
    private AttachmentViewModel viewModel;
    private String userLoginKey;
    private PhotoGalleryAdapter adapter;
    private List<String> filePathList;
    private List<Integer> attachIDList;
    private int sickID, index;
    private static final int SPAN_COUNT = 3;

    private static final String TAG = PhotoGalleryFragment.class.getSimpleName();

    public static PhotoGalleryFragment newInstance() {
        PhotoGalleryFragment fragment = new PhotoGalleryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createViewModel();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_photo_gallery,
                container,
                false);

        adapter = new PhotoGalleryAdapter(getContext(), viewModel, new ArrayList<>());

        initVariables();
        initViews();
        fetchPatientAttachments(sickID);
        handleEvents();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupObserver();
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(AttachmentViewModel.class);
    }

    private void initVariables() {
        String centerName = SipMobileAppPreferences.getCenterName(getContext());
        ServerData serverData = viewModel.getServerData(centerName);
        viewModel.getServiceAttachResult(serverData.getIp() + ":" + serverData.getPort());
        userLoginKey = SipMobileAppPreferences.getUserLoginKey(getContext());

        PhotoGalleryFragmentArgs args = PhotoGalleryFragmentArgs.fromBundle(getArguments());
        sickID = args.getSickID();
        if (sickID > 0)
            SipMobileAppPreferences.setSickID(getContext(), sickID);
        else
            sickID = SipMobileAppPreferences.getSickID(getContext());

        int attachID = args.getAttachID();
        if (attachID > 0) {
            if (externalMemoryAvailable()) {
                File direction = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                if (direction.exists()) {
                    File[] files = direction.listFiles();
                    for (File file : files) {
                        if (file.getName().equals(attachID + ".jpg")) {
                            file.delete();
                            break;
                        }
                    }
                }
            }
        }

        filePathList = new ArrayList<>();
        attachIDList = new ArrayList<>();
        index = 0;
    }

    private void initViews() {
        binding.recyclerViewAttachment.setLayoutManager(new GridLayoutManager(getContext(), SPAN_COUNT));
    }

    private void handleError(String msg) {
        binding.progressBarLoading.setVisibility(View.GONE);
        ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(msg);
        fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
    }

    private boolean externalMemoryAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    private void setupAdapter() {
        binding.progressBarLoading.setVisibility(View.GONE);
        binding.recyclerViewAttachment.setVisibility(View.VISIBLE);
        adapter.updateFilePathList(filePathList);
        binding.recyclerViewAttachment.setAdapter(adapter);
    }

    private void fetchPatientAttachments(int sickID) {
        String path = "/api/v1/attachs/ListBySickID/";
        viewModel.fetchPatientAttachments(path, userLoginKey, sickID);
    }

    private void fetchAttachInfo(int attachID) {
        String path = "/api/v1/attachs/Info/";
        viewModel.fetchAttachInfo(path, userLoginKey, attachID);
    }

    private String read(int attachID) {
        if (externalMemoryAvailable()) {
            File direction = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (direction.exists()) {
                File[] files = direction.listFiles();
                for (File file : files) {
                    if (file.getName().equals(attachID + ".jpg"))
                        return file.getPath();
                }
            }
        }
        return "";
    }

    private String write(AttachResult.AttachInfo attachInfo) throws IOException {
        if (externalMemoryAvailable()) {
            File direction = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (direction.exists()) {
                File file = new File(direction, attachInfo.getAttachID() + ".jpg");
                if (attachInfo.getFileBase64() != null && attachInfo.getDeleteUserID() == 0) {
                    byte[] bytes = Base64.decode(attachInfo.getFileBase64(), 0);
                    FileOutputStream fileOutputStream = new FileOutputStream(file, true);
                    fileOutputStream.write(bytes);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    return file.getPath();
                }
            }
        }
        return "";
    }

    private void showAttachments(AttachResult attachResult) {
        if (attachResult.getAttachs().length == 0) {
            binding.progressBarLoading.setVisibility(View.GONE);
        } else {
            for (AttachResult.AttachInfo attachInfo : attachResult.getAttachs()) {
                String filePath = read(attachInfo.getAttachID());
                if (!filePath.isEmpty())
                    filePathList.add(filePath);
                else
                    attachIDList.add(attachInfo.getAttachID());
            }

            if (filePathList.size() != 0)
                setupAdapter();

            if (attachIDList.size() != 0)
                fetchAttachInfo(attachIDList.get(index));
        }
    }

    private void handleEvents() {
        binding.fabAdd.setOnClickListener(view -> {
            PhotoGalleryFragmentDirections.ActionPhotoGalleryFragmentToAttachmentFragment action = PhotoGalleryFragmentDirections.actionPhotoGalleryFragmentToAttachmentFragment();
            action.setSickID(sickID);
            NavHostFragment.findNavController(this).navigate(action);
        });
    }

    private void setupObserver() {
        viewModel.getPatientAttachmentsResultSingleLiveEvent().observe(getViewLifecycleOwner(), attachResult -> {
            if (attachResult != null) {
                if (attachResult.getErrorCode().equals("0"))
                    showAttachments(attachResult);
                else
                    handleError(attachResult.getError());
            }
        });

        viewModel.getAttachInfoResultSingleLiveEvent().observe(getViewLifecycleOwner(), attachResult -> {
            if (attachResult != null) {
                if (attachResult.getErrorCode().equals("0")) {
                    if (attachResult.getAttachs().length != 0) {
                        AttachResult.AttachInfo attachInfo = attachResult.getAttachs()[0];
                        new Thread(() -> {
                            try {
                                String filePath = write(attachInfo);
                                if (!filePath.isEmpty())
                                    filePathList.add(filePath);
                                viewModel.getDoneWrite().postValue(true);
                            } catch (IOException e) {
                                Log.e(TAG, e.getMessage());
                            }
                        }).start();
                    }
                } else
                    handleError(attachResult.getError());
            }
        });

        viewModel.getDoneWrite().observe(getViewLifecycleOwner(), doneWrite -> {
            index++;
            if (index < attachIDList.size())
                fetchAttachInfo(attachIDList.get(index));
            else {
                if (filePathList.size() != 0)
                    setupAdapter();
                else
                    binding.progressBarLoading.setVisibility(View.GONE);
            }
        });

        viewModel.getPhotoClicked().observe(getViewLifecycleOwner(), filePath -> {
            File file = new File(filePath);
            String fileName = file.getName().replace(".jpg", "");
            int attachID = Integer.parseInt(fileName);
            PhotoGalleryFragmentDirections.ActionPhotoGalleryFragmentToFullScreenPhotoFragment action = PhotoGalleryFragmentDirections.actionPhotoGalleryFragmentToFullScreenPhotoFragment(filePath);
            action.setAttachID(attachID);
            NavHostFragment.findNavController(this).navigate(action);
        });

        viewModel.getNoConnectionExceptionHappenSingleLiveEvent().observe(getViewLifecycleOwner(), this::handleError);

        viewModel.getTimeoutExceptionHappenSingleLiveEvent().observe(getViewLifecycleOwner(), this::handleError);
    }
}