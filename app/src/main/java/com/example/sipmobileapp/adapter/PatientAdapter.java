package com.example.sipmobileapp.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sipmobileapp.R;
import com.example.sipmobileapp.databinding.PatientItemBinding;
import com.example.sipmobileapp.model.PatientResult;
import com.example.sipmobileapp.utils.Converter;
import com.example.sipmobileapp.viewmodel.PatientViewModel;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.util.List;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.PatientHolder> {
    private final PatientViewModel viewModel;
    private final List<PatientResult.PatientInfo> patients;

    public PatientAdapter(PatientViewModel viewModel, List<PatientResult.PatientInfo> patients) {
        this.viewModel = viewModel;
        this.patients = patients;
    }

    @NonNull
    @Override
    public PatientHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PatientHolder(DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.patient_item,
                parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull PatientHolder holder, int position) {
        PatientResult.PatientInfo info = patients.get(position);
        holder.bindPatient(info);

        holder.binding.ivMore.setOnClickListener(view -> {
            PowerMenu powerMenu = new PowerMenu.Builder(holder.binding.getRoot().getContext())
                    .addItem(new PowerMenuItem("مستندات"))
                    .setSize(400, 200)
                    .build();

            powerMenu.setOnMenuItemClickListener((i, item) -> {
                if (i == 0) {
                    viewModel.getAttachmentsItemClicked().setValue(info.getSickID());
                    powerMenu.dismiss();
                }
            });
            powerMenu.showAsDropDown(view);
        });

        holder.binding.getRoot().setOnClickListener(view -> viewModel.getItemClicked().setValue(info.getSickID()));
    }

    @Override
    public int getItemCount() {
        return patients != null ? patients.size() : 0;
    }

    public class PatientHolder extends RecyclerView.ViewHolder {
        private final PatientItemBinding binding;

        public PatientHolder(PatientItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindPatient(PatientResult.PatientInfo info) {
            binding.txtPatientID.setText(String.valueOf(info.getSickID()));
            binding.txtDate.setText(info.getDate());
            binding.txtPatientName.setText(Converter.letterConverter(info.getPatientName()));
            binding.txtServices.setText(Converter.letterConverter(info.getServices()));
        }
    }
}
