package com.example.sipmobileapp.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.sipmobileapp.R;
import com.example.sipmobileapp.databinding.FragmentAttachmentBinding;
import com.example.sipmobileapp.event.RefreshEvent;
import com.example.sipmobileapp.model.AttachResult;
import com.example.sipmobileapp.model.ServerData;
import com.example.sipmobileapp.ui.dialog.AttachAgainDialogFragment;
import com.example.sipmobileapp.ui.dialog.ErrorDialogFragment;
import com.example.sipmobileapp.ui.dialog.SuccessAttachDialogFragment;
import com.example.sipmobileapp.utils.SipMobileAppPreferences;
import com.example.sipmobileapp.viewmodel.AttachmentViewModel;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class AttachmentFragment extends Fragment {
    private FragmentAttachmentBinding binding;
    private AttachmentViewModel viewModel;
    private ServerData serverData;
    private String userLoginKey;
    private Uri photoUri;
    private File photoFile;
    private Bitmap bitmap;
    private Matrix matrix;
    private int numberOfRotate, sickID;

    private static final int REQUEST_CODE_TAKE_PHOTO = 0;
    private static final int REQUEST_CODE_PICK_PHOTO = 1;
    private static final int REQUEST_CODE_CAMERA_PERMISSION = 2;

    private static final String ARGS_SICK_ID = "sickID";
    private static final String AUTHORITY = "com.example.sipmobileapp.fileProvider";
    public static final String TAG = AttachmentFragment.class.getSimpleName();

    public static AttachmentFragment newInstance(int sickID) {
        AttachmentFragment fragment = new AttachmentFragment();
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
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_attachment,
                container,
                false);

        handleEvents();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupObserver();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_TAKE_PHOTO:
                    if (photoFile.length() != 0) {
                        try {
                            photoUri = FileProvider.getUriForFile(getContext(), AUTHORITY, photoFile);
                            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), photoUri);

                            if (bitmap.getWidth() > bitmap.getHeight()) {
                                matrix.postRotate(90);
                                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                            }

                            binding.ivNoPhoto.setVisibility(View.GONE);
                            binding.ivPhoto.setVisibility(View.VISIBLE);
                            Glide.with(getContext()).load(bitmap).into(binding.ivPhoto);
                        } catch (IOException e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                    getActivity().revokeUriPermission(photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    break;
                case REQUEST_CODE_PICK_PHOTO:
                    photoUri = data.getData();
                    if (photoUri != null) {
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), photoUri);
                            binding.ivNoPhoto.setVisibility(View.GONE);
                            binding.ivPhoto.setVisibility(View.VISIBLE);
                            Glide.with(getContext()).load(bitmap).into(binding.ivPhoto);
                        } catch (IOException e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                    break;
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length == 0) {
            return;
        }
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            handleError(getString(R.string.camera_permission_denied));
        }
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(AttachmentViewModel.class);
    }

    private void initVariables() {
        String centerName = SipMobileAppPreferences.getCenterName(getContext());
        serverData = viewModel.getServerData(centerName);
        userLoginKey = SipMobileAppPreferences.getUserLoginKey(getContext());
        sickID = getArguments().getInt(ARGS_SICK_ID);
        matrix = new Matrix();
    }

    private void requestCameraPermission() {
        requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA_PERMISSION);
    }

    private void openCamera() {
        Intent starter = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (starter.resolveActivity(getActivity().getPackageManager()) != null) {
            File dir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Attachments");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String name = "img_" + new Date().getTime() + ".jpg";
            photoFile = new File(dir, name);
            Uri uri = FileProvider.getUriForFile(getContext(), AUTHORITY, photoFile);
            List<ResolveInfo> activities = getActivity().getPackageManager().queryIntentActivities(starter, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo activity : activities) {
                getActivity().grantUriPermission(activity.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
            starter.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(starter, REQUEST_CODE_TAKE_PHOTO);
        }
    }

    private void handleError(String message) {
        ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
        fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
    }

    private void showSuccessDialog(String message) {
        SuccessAttachDialogFragment fragment = SuccessAttachDialogFragment.newInstance(message);
        fragment.show(getParentFragmentManager(), SuccessAttachDialogFragment.TAG);
    }

    private void attach(AttachResult.AttachParameter attachParameter) {
        viewModel.getServiceAttachResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/attachs/Add/";
        viewModel.attach(path, userLoginKey, attachParameter);
    }

    private boolean hasCameraPermission() {
        return (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        System.gc();
        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
    }

    private void setupObserver() {
        viewModel.getAttachResultSingleLiveEvent().observe(getViewLifecycleOwner(), attachResult -> {
            binding.progressBarLoading.setVisibility(View.GONE);
            binding.ivSend.setEnabled(true);
            binding.ivCamera.setEnabled(true);
            binding.edTxtDescription.setEnabled(true);
            binding.ivRotate.setEnabled(true);
            binding.ivAttach.setEnabled(true);

            if (attachResult != null) {
                if (attachResult.getErrorCode().equals("0")) {
                    if (attachResult.getAttachs().length != 0) {
                        int attachID = attachResult.getAttachs()[0].getAttachID();
                        EventBus.getDefault().postSticky(new RefreshEvent(attachID));
                    }
                    showSuccessDialog(getString(R.string.success_attach));
                } else {
                    handleError(attachResult.getError());
                }
            }
        });

        viewModel.getNoConnectionExceptionHappenSingleLiveEvent().observe(getViewLifecycleOwner(), message -> {
            binding.progressBarLoading.setVisibility(View.GONE);
            binding.ivSend.setEnabled(true);
            binding.ivCamera.setEnabled(true);
            binding.edTxtDescription.setEnabled(true);
            binding.ivRotate.setEnabled(true);
            binding.ivAttach.setEnabled(true);
            handleError(message);
        });

        viewModel.getTimeoutExceptionHappenSingleLiveEvent().observe(getViewLifecycleOwner(), message -> {
            binding.progressBarLoading.setVisibility(View.GONE);
            binding.ivSend.setEnabled(true);
            binding.ivCamera.setEnabled(true);
            binding.edTxtDescription.setEnabled(true);
            binding.ivRotate.setEnabled(true);
            binding.ivAttach.setEnabled(true);
            handleError(message);
        });

        viewModel.getShowAttachAgainDialog().observe(getViewLifecycleOwner(), showAttachAgainDialog -> {
            AttachAgainDialogFragment fragment = AttachAgainDialogFragment.newInstance(getString(R.string.attach_again_question));
            fragment.show(getParentFragmentManager(), AttachAgainDialogFragment.TAG);
        });

        viewModel.getNoAttachAgain().observe(getViewLifecycleOwner(), noAttachAgain -> getActivity().finish());

        viewModel.getYesAttachAgain().observe(getViewLifecycleOwner(), yesAttachAgain -> {
            photoUri = null;
            binding.ivPhoto.setImageResource(0);
            binding.edTxtDescription.setText("");
            binding.ivNoPhoto.setVisibility(View.VISIBLE);
            binding.ivPhoto.setVisibility(View.GONE);
        });
    }

    private void handleEvents() {
        binding.ivAttach.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, getString(R.string.chooser_title)), REQUEST_CODE_PICK_PHOTO);
        });

        binding.ivRotate.setOnClickListener(view -> {
            if (photoUri != null) {
                switch (numberOfRotate) {
                    case 0:
                        matrix.postRotate(90);
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                        Glide.with(getContext()).load(bitmap).into(binding.ivPhoto);
                        numberOfRotate++;
                        break;
                    case 1:
                        matrix.postRotate(180);
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                        Glide.with(getContext()).load(bitmap).into(binding.ivPhoto);
                        numberOfRotate++;
                        break;
                    case 2:
                        matrix.postRotate(270);
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                        Glide.with(getContext()).load(bitmap).into(binding.ivPhoto);
                        numberOfRotate = 0;
                        break;
                }
            } else {
                handleError(getString(R.string.select_file));
            }
        });

        binding.ivCamera.setOnClickListener(view -> {
            if (hasCameraPermission()) {
                openCamera();
            } else {
                requestCameraPermission();
            }
        });

        binding.ivSend.setOnClickListener(view -> {
            if (photoUri != null) {
                binding.progressBarLoading.setVisibility(View.VISIBLE);
                binding.ivSend.setEnabled(false);
                binding.ivRotate.setEnabled(false);
                binding.ivAttach.setEnabled(false);
                binding.ivCamera.setEnabled(false);

                AttachResult.AttachParameter attachParameter = new AttachResult().new AttachParameter();
                String image = convertBitmapToBase64(bitmap);
                attachParameter.setImage(image);
                attachParameter.setSickID(sickID);
                String description = binding.edTxtDescription.getText().toString();
                attachParameter.setDescription(description);
                attachParameter.setAttachTypeID(1);
                attachParameter.setImageTypeID(2);

                new Thread(() -> attach(attachParameter)).start();
            } else {
                handleError(getString(R.string.select_file));
            }
        });
    }
}