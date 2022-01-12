package com.example.sipmobileapp.ui.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.example.sipmobileapp.R;
import com.example.sipmobileapp.databinding.FragmentPhotoBinding;
import com.example.sipmobileapp.model.ServerData;
import com.example.sipmobileapp.ui.dialog.ErrorDialogFragment;
import com.example.sipmobileapp.ui.dialog.QuestionDialogFragment;
import com.example.sipmobileapp.ui.dialog.SuccessDialogFragment;
import com.example.sipmobileapp.utils.SipMobileAppPreferences;
import com.example.sipmobileapp.viewmodel.AttachmentViewModel;

public class PhotoFragment extends Fragment {
    private FragmentPhotoBinding binding;
    private AttachmentViewModel viewModel;
    private String userLoginKey, filePath;
    private int attachID;

    public static PhotoFragment newInstance() {
        PhotoFragment fragment = new PhotoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createViewModel();
        initVariables();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_photo,
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

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(AttachmentViewModel.class);
    }

    private void initVariables() {
        String centerName = SipMobileAppPreferences.getCenterName(getContext());
        ServerData serverData = viewModel.getServerData(centerName);
        viewModel.getServiceAttachResult(serverData.getIp() + ":" + serverData.getPort());
        userLoginKey = SipMobileAppPreferences.getUserLoginKey(getContext());
        PhotoFragmentArgs args = PhotoFragmentArgs.fromBundle(getArguments());
        attachID = args.getAttachID();
        filePath = args.getFilePath();
    }

    private void initViews() {
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        if (bitmap != null)
            binding.ivPhoto.setImage(ImageSource.bitmap(bitmap));
    }

    private void handleError(String msg) {
        ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(msg);
        fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
    }

    private void showSuccessDialog(String msg) {
        SuccessDialogFragment fragment = SuccessDialogFragment.newInstance(msg);
        fragment.show(getParentFragmentManager(), SuccessDialogFragment.TAG);
    }

    private void deleteAttach(int attachID) {
        String path = "/api/v1/attachs/Delete/";
        viewModel.deleteAttach(path, userLoginKey, attachID);
    }

    private void handleEvents() {
        binding.ivDelete.setOnClickListener(v -> {
            QuestionDialogFragment fragment = QuestionDialogFragment.newInstance(getString(R.string.delete_attach_question));
            fragment.show(getParentFragmentManager(), QuestionDialogFragment.TAG);
        });

        binding.ivClose.setOnClickListener(view -> {
            NavDirections action = PhotoFragmentDirections.actionFullScreenPhotoFragmentToPhotoGalleryFragment();
            NavHostFragment.findNavController(this).navigate(action);
        });
    }

    private void setupObserver() {
        viewModel.getDeleteAttachResultSingleLiveEvent().observe(getViewLifecycleOwner(), attachResult -> {
            binding.progressBarLoading.setVisibility(View.GONE);
            if (attachResult != null) {
                if (attachResult.getErrorCode().equals("0")) {
                    if (attachResult.getAttachs().length != 0)
                        attachID = attachResult.getAttachs()[0].getAttachID();
                    showSuccessDialog(getString(R.string.success_delete_attach));
                } else
                    handleError(attachResult.getError());
            }
        });

        viewModel.getYesDeleteClicked().observe(getViewLifecycleOwner(), yesDeleteClicked -> {
            binding.progressBarLoading.setVisibility(View.VISIBLE);
            deleteAttach(attachID);
        });

        viewModel.getSuccessDialogDismissed().observe(getViewLifecycleOwner(), closeClicked -> {
            PhotoFragmentDirections.ActionFullScreenPhotoFragmentToPhotoGalleryFragment action = PhotoFragmentDirections.actionFullScreenPhotoFragmentToPhotoGalleryFragment();
            action.setAttachID(attachID);
            NavHostFragment.findNavController(PhotoFragment.this).navigate(action);
        });
    }
}