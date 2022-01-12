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
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.sipmobileapp.R;
import com.example.sipmobileapp.databinding.FragmentAttachmentBinding;
import com.example.sipmobileapp.model.AttachResult;
import com.example.sipmobileapp.model.ServerDataTwo;
import com.example.sipmobileapp.ui.dialog.AttachAgainDialogFragment;
import com.example.sipmobileapp.ui.dialog.ErrorDialogFragment;
import com.example.sipmobileapp.ui.dialog.SuccessAttachDialogFragment;
import com.example.sipmobileapp.utils.ScaleBitmap;
import com.example.sipmobileapp.utils.SipMobileAppPreferences;
import com.example.sipmobileapp.viewmodel.AttachmentViewModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class AttachmentFragment extends Fragment {
    private FragmentAttachmentBinding binding;
    private AttachmentViewModel viewModel;
    private String userLoginKey;
    private Uri photoUri;
    private File photoFile;
    private Bitmap bitmap;
    private Matrix matrix;
    private String image;
    private int numberOfRotate, sickID;

    private static final int REQUEST_CODE_TAKE_PHOTO = 0;
    private static final int REQUEST_CODE_PICK_PHOTO = 1;
    private static final int REQUEST_CODE_CAMERA_PERMISSION = 2;

    private static final String TAG = AttachmentFragment.class.getSimpleName();
    private static final String AUTHORITY = "com.example.sipmobileapp.fileProvider";

    public static AttachmentFragment newInstance() {
        AttachmentFragment fragment = new AttachmentFragment();
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

                            binding.ivEmpty.setVisibility(View.GONE);
                            binding.ivPhoto.setVisibility(View.VISIBLE);
                            Glide.with(getContext()).load(bitmap).into(binding.ivPhoto);
                        } catch (IOException e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                    getActivity().revokeUriPermission(photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    break;
                case REQUEST_CODE_PICK_PHOTO:
                    assert data != null;
                    photoUri = data.getData();
                    if (photoUri != null) {
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), photoUri);
                            binding.ivEmpty.setVisibility(View.GONE);
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
        if (grantResults.length == 0)
            return;
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            openCamera();
        else
            handleError(getString(R.string.camera_permission_denied));
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(AttachmentViewModel.class);
    }

    private void initVariables() {
        String centerName = SipMobileAppPreferences.getCenterName(getContext());
        ServerDataTwo serverData = viewModel.getServerData(centerName);
        viewModel.getServiceAttachResult(serverData.getIp() + ":" + serverData.getPort());
        userLoginKey = SipMobileAppPreferences.getUserLoginKey(getContext());
        AttachmentFragmentArgs args = AttachmentFragmentArgs.fromBundle(getArguments());
        sickID = args.getSickID();
        matrix = new Matrix();
    }

    private boolean hasCameraPermission() {
        return (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestCameraPermission() {
        requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA_PERMISSION);
    }

    private void openCamera() {
        Intent starter = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (starter.resolveActivity(getActivity().getPackageManager()) != null) {
            File direction = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (direction.exists()) {
                String name = "img_" + new Date().getTime() + ".jpg";
                photoFile = new File(direction, name);
                Uri uri = FileProvider.getUriForFile(requireContext(), AUTHORITY, photoFile);
                List<ResolveInfo> activities = requireActivity().getPackageManager().queryIntentActivities(starter, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo activity : activities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                starter.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(starter, REQUEST_CODE_TAKE_PHOTO);
            }
        }
    }

    private void handleError(String msg) {
        ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(msg);
        fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
    }

    private void showSuccessDialog(String msg) {
        SuccessAttachDialogFragment fragment = SuccessAttachDialogFragment.newInstance(msg);
        fragment.show(getParentFragmentManager(), SuccessAttachDialogFragment.TAG);
    }

    private String convertBitmapToBase64(Bitmap bitmap) {
        Bitmap scaledBitmap = ScaleBitmap.getScaledDownBitmap(bitmap, 2245);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        System.gc();
        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
    }

    private void attach(AttachResult.AttachParameter attachParameter) {
        String path = "/api/v1/attachs/Add/";
        viewModel.attach(path, userLoginKey, attachParameter);
    }

    private void handleEvents() {
        binding.ivAttach.setOnClickListener(view -> {
            Intent starter = new Intent();
            starter.setType("image/*");
            starter.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(starter, getString(R.string.chooser_title)), REQUEST_CODE_PICK_PHOTO);
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
            } else
                handleError(getString(R.string.select_file));
        });

        binding.ivCamera.setOnClickListener(view -> {
            if (hasCameraPermission())
                openCamera();
            else
                requestCameraPermission();
        });

        binding.ivSend.setOnClickListener(view -> {
            if (photoUri != null) {
                binding.progressBarLoading.setVisibility(View.VISIBLE);
                binding.ivSend.setEnabled(false);
                binding.ivRotate.setEnabled(false);
                binding.ivAttach.setEnabled(false);
                binding.ivCamera.setEnabled(false);
                AttachResult.AttachParameter attachParameter = new AttachResult().new AttachParameter();
                image = convertBitmapToBase64(bitmap);
                attachParameter.setImage(image);
                attachParameter.setSickID(sickID);
                String description = binding.edTxtDescription.getText().toString();
                attachParameter.setDescription(description);
                attachParameter.setAttachTypeID(1);
                attachParameter.setImageTypeID(2);
                new Thread(() -> attach(attachParameter)).start();
            } else
                handleError(getString(R.string.select_file));
        });
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
                    new Thread(() -> {
                        try {
                            write(attachResult.getAttachs()[0].getAttachID());
                        } catch (IOException e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }).start();
                    showSuccessDialog(getString(R.string.success_attach));
                } else
                    handleError(attachResult.getError());
            }
        });

        viewModel.getShowAttachAgainDialog().observe(getViewLifecycleOwner(), showAttachAgainDialog -> {
            AttachAgainDialogFragment fragment = AttachAgainDialogFragment.newInstance(getString(R.string.attach_again_question));
            fragment.show(getParentFragmentManager(), AttachAgainDialogFragment.TAG);
        });

        viewModel.getNoAttachAgain().observe(getViewLifecycleOwner(), noAttachAgain ->
        {
            NavDirections action = AttachmentFragmentDirections.actionAttachmentFragmentToPhotoGalleryFragment();
            NavHostFragment.findNavController(this).navigate(action);
        });

        viewModel.getYesAttachAgain().observe(getViewLifecycleOwner(), yesAttachAgain -> {
            photoUri = null;
            binding.ivPhoto.setImageResource(0);
            binding.edTxtDescription.setText("");
            binding.ivEmpty.setVisibility(View.VISIBLE);
            binding.ivPhoto.setVisibility(View.GONE);
        });

        viewModel.getNoConnectionExceptionHappenSingleLiveEvent().observe(getViewLifecycleOwner(), msg -> {
            binding.progressBarLoading.setVisibility(View.GONE);
            binding.ivSend.setEnabled(true);
            binding.ivCamera.setEnabled(true);
            binding.edTxtDescription.setEnabled(true);
            binding.ivRotate.setEnabled(true);
            binding.ivAttach.setEnabled(true);
            handleError(msg);
        });

        viewModel.getTimeoutExceptionHappenSingleLiveEvent().observe(getViewLifecycleOwner(), msg -> {
            binding.progressBarLoading.setVisibility(View.GONE);
            binding.ivSend.setEnabled(true);
            binding.ivCamera.setEnabled(true);
            binding.edTxtDescription.setEnabled(true);
            binding.ivRotate.setEnabled(true);
            binding.ivAttach.setEnabled(true);
            handleError(msg);
        });
    }

    private void write(int attachID) throws IOException {
        if (externalMemoryAvailable()) {
            File direction = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (direction.exists()) {
                File file = new File(direction, attachID + ".jpg");
                byte[] bytes = Base64.decode(image, 0);
                FileOutputStream fileOutputStream = new FileOutputStream(file, true);
                fileOutputStream.write(bytes);
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }
    }

    private boolean externalMemoryAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
}