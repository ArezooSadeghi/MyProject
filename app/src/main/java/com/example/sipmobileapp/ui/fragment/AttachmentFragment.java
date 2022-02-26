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
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.example.sipmobileapp.R;
import com.example.sipmobileapp.adapter.AttachAdapter;
import com.example.sipmobileapp.databinding.FragmentAttachmentBinding;
import com.example.sipmobileapp.model.AttachResult;
import com.example.sipmobileapp.model.ServerData;
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
import java.util.ArrayList;
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
    private List<Uri> uris;
    private GridLayoutManager gridLayoutManager;
    private ActivityResultLauncher activityResultLauncherCameraPermission, activityResultLauncherTakePhoto, activityResultLauncherPickPhoto;
    private int numberOfRotate, sickID, index;

    private static final int SPAN_COUNT = 3;
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
        AttachmentFragmentArgs args = AttachmentFragmentArgs.fromBundle(getArguments());
        sickID = args.getSickID();
        matrix = new Matrix();
        uris = new ArrayList<>();

        activityResultLauncherCameraPermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), (ActivityResultCallback<Boolean>) granted -> {
            if (granted)
                openCamera();
            else
                handleError(getString(R.string.camera_permission_denied));
        });

        activityResultLauncherTakePhoto = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                if (photoFile.length() != 0) {
                    try {
                        photoUri = FileProvider.getUriForFile(getContext(), AUTHORITY, photoFile);
                        uris.add(photoUri);
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uris.get(0));

                        if (bitmap.getWidth() > bitmap.getHeight()) {
                            matrix.postRotate(90);
                            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                        }

                        binding.ivEmpty.setVisibility(View.GONE);
                        binding.recyclerView.setVisibility(View.GONE);
                        binding.ivPhoto.setVisibility(View.VISIBLE);
                        Glide.with(getContext()).load(bitmap).into(binding.ivPhoto);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
                getActivity().revokeUriPermission(photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
        });

        activityResultLauncherPickPhoto = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                if (result.getData().getClipData() != null) {
                    int count = result.getData().getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        uris.add(result.getData().getClipData().getItemAt(i).getUri());
                    }
                    binding.ivEmpty.setVisibility(View.GONE);
                    binding.ivPhoto.setVisibility(View.GONE);
                    binding.recyclerView.setVisibility(View.VISIBLE);
                    setupAdapter(uris);
                    startAttach();
                }
            }
        });
    }

    private void initViews() {
        gridLayoutManager = new GridLayoutManager(getContext(), SPAN_COUNT);
        binding.recyclerView.setLayoutManager(gridLayoutManager);
    }

    private void setupAdapter(List<Uri> uris) {
        AttachAdapter adapter = new AttachAdapter(uris);
        binding.recyclerView.setAdapter(adapter);
    }

    private boolean hasCameraPermission() {
        return (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestCameraPermission() {
        activityResultLauncherCameraPermission.launch(Manifest.permission.CAMERA);
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
                activityResultLauncherTakePhoto.launch(starter);
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
            starter.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            starter.setAction(Intent.ACTION_GET_CONTENT);
            activityResultLauncherPickPhoto.launch(starter);
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
            try {
                if (uris.size() == 0)
                    handleError(getString(R.string.select_file));
                else
                    startAttach();
            } catch (Exception e) {
                handleError(e.getMessage());
            }
        });
    }

    private void setupObserver() {
        viewModel.getAttachResultSingleLiveEvent().observe(getViewLifecycleOwner(), attachResult -> {
            if (attachResult != null) {
                if (attachResult.getErrorCode().equals("0")) {
                    AttachAdapter.AttachHolder attachHolder = (AttachAdapter.AttachHolder) binding.recyclerView.findViewHolderForAdapterPosition(index);
                    if (attachHolder != null) {
                        ImageView imageView = attachHolder.itemView.findViewById(R.id.iv_done);
                        ProgressBar progressBar = attachHolder.itemView.findViewById(R.id.progress_bar_loading);
                        imageView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                    binding.progressBarLoading.setVisibility(View.GONE);
                    new Thread(() -> {
                        try {
                            write(attachResult.getAttachs()[0].getAttachID());
                        } catch (IOException e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }).start();

                } else {
                    index++;
                    startAttach();
                }
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
            uris = new ArrayList<>();
            index = 0;
            binding.ivPhoto.setImageResource(0);
            binding.edTxtDescription.setText("");
            binding.ivEmpty.setVisibility(View.VISIBLE);
            binding.ivPhoto.setVisibility(View.GONE);
            binding.recyclerView.setVisibility(View.GONE);
            binding.ivSend.setEnabled(true);
            binding.ivCamera.setEnabled(true);
            binding.edTxtDescription.setEnabled(true);
            binding.ivRotate.setEnabled(true);
            binding.ivAttach.setEnabled(true);
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

        viewModel.getNewDoneWrite().observe(getViewLifecycleOwner(), doneWrite -> {
            try {
                index++;
                if (index >= uris.size())
                    showSuccessDialog(getString(R.string.success_attach));
                else
                    startAttach();
            } catch (Exception e) {
                handleError(e.getMessage());
            }
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
        viewModel.getNewDoneWrite().postValue(true);
    }

    private boolean externalMemoryAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    private void startAttach() {
        if (uris.size() != 0 && index < uris.size()) {
            try {

                if (photoUri != null)
                    binding.progressBarLoading.setVisibility(View.VISIBLE);

                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uris.get(index));
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
            } catch (IOException e) {
                handleError(e.getMessage());
            }
        } else {
            binding.ivSend.setEnabled(true);
            binding.ivCamera.setEnabled(true);
            binding.edTxtDescription.setEnabled(true);
            binding.ivRotate.setEnabled(true);
            binding.ivAttach.setEnabled(true);
        }
    }
}