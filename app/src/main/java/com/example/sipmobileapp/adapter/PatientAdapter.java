package com.example.sipmobileapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sipmobileapp.R;
import com.example.sipmobileapp.databinding.PatientAdapterItemBinding;
import com.example.sipmobileapp.model.PatientResult;
import com.example.sipmobileapp.utils.Converter;
import com.example.sipmobileapp.viewmodel.PatientViewModel;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.util.List;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.PatientHolder> {
    private final Context context;
    private final List<PatientResult.PatientInfo> patientInfoList;
    private final PatientViewModel viewModel;

    public PatientAdapter(Context context, List<PatientResult.PatientInfo> patientInfoList, PatientViewModel viewModel) {
        this.context = context;
        this.patientInfoList = patientInfoList;
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public PatientHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PatientHolder(DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.patient_adapter_item,
                parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull PatientHolder holder, int position) {
        PatientResult.PatientInfo patientInfo = patientInfoList.get(position);
        holder.bindPatientInfo(patientInfo);

        holder.binding.ivMore.setOnClickListener(view -> {
            PowerMenu powerMenu = new PowerMenu.Builder(context)
                    .addItem(new PowerMenuItem(context.getResources().getString(R.string.attachments_item_title)))
                    .setSize(400, 200)
                    .build();

            powerMenu.setOnMenuItemClickListener((i, item) -> {
                if (i == 0) {
                    viewModel.getAttachmentsItemClicked().setValue(patientInfo.getSickID());
                    powerMenu.dismiss();
                }
            });
            powerMenu.showAsDropDown(view);
        });
    }

    @Override
    public int getItemCount() {
        return patientInfoList == null ? 0 : patientInfoList.size();
    }

    public static class PatientHolder extends RecyclerView.ViewHolder {
        private final PatientAdapterItemBinding binding;

        public PatientHolder(PatientAdapterItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindPatientInfo(PatientResult.PatientInfo patientInfo) {
            binding.txtPatientID.setText(String.valueOf(patientInfo.getSickID()));
            binding.txtDate.setText(patientInfo.getDate());
            binding.txtPatientName.setText(Converter.letterConverter(patientInfo.getPatientName()));
            binding.txtServices.setText(Converter.letterConverter(patientInfo.getServices()));
        }
    }
}
