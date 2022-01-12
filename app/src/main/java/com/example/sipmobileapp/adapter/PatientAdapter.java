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
    private List<PatientResult.PatientInfo> patientList;

    public PatientAdapter(PatientViewModel viewModel, List<PatientResult.PatientInfo> patientList) {
        this.viewModel = viewModel;
        this.patientList = patientList;
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
        PatientResult.PatientInfo patient = patientList.get(position);
        holder.bindPatient(patient);

        holder.binding.ivMore.setOnClickListener(view -> {
            PowerMenu powerMenu = new PowerMenu.Builder(holder.binding.getRoot().getContext())
                    .addItem(new PowerMenuItem("مستندات"))
                    .setSize(400, 200)
                    .build();

            powerMenu.setOnMenuItemClickListener((i, item) -> {
                if (i == 0) {
                    viewModel.getAttachmentsItemClicked().setValue(patient.getSickID());
                    powerMenu.dismiss();
                }
            });
            powerMenu.showAsDropDown(view);
        });
    }

    @Override
    public int getItemCount() {
        return patientList != null ? patientList.size() : 0;
    }

    public class PatientHolder extends RecyclerView.ViewHolder {
        private final PatientItemBinding binding;

        public PatientHolder(PatientItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindPatient(PatientResult.PatientInfo patient) {
            binding.txtPatientID.setText(String.valueOf(patient.getSickID()));
            binding.txtDate.setText(patient.getDate());
            binding.txtPatientName.setText(Converter.letterConverter(patient.getPatientName()));
            binding.txtServices.setText(Converter.letterConverter(patient.getServices()));
        }
    }
}
