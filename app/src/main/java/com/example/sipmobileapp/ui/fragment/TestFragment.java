package com.example.sipmobileapp.ui.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.sipmobileapp.R;
import com.example.sipmobileapp.databinding.FragmentTestBinding;
import com.example.sipmobileapp.ui.dialog.ErrorDialogFragment;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TestFragment extends Fragment {
    private FragmentTestBinding binding;
    private Socket socket;

    public static final String TAG = TestFragment.class.getSimpleName();

    public static TestFragment newInstance() {
        TestFragment fragment = new TestFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_test,
                container,
                false);

        handleEvents();

        return binding.getRoot();
    }

    private void handleError(String msg) {
        ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(msg);
        fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
    }

    private void handleEvents() {
        binding.edTxtDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                new Thread(() -> {
                    try {
                        String description = charSequence.toString();
                        String host = "";
                        int port = 0;
                        socket = new Socket(host, port);
                        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                        dataOutputStream.writeBytes(description);
                        dataOutputStream.flush();
                    } catch (IOException e) {
                        if (socket != null) {
                            try {
                                socket.close();
                                handleError(e.getMessage());
                            } catch (IOException ex) {
                                Log.e(TAG, ex.getMessage());
                            }
                        }
                    }
                }).start();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}