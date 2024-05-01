package com.standalone.cashbook.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.standalone.cashbook.controllers.SqliteHelper;
import com.standalone.cashbook.databinding.FragmentDialogBinding;
import com.standalone.cashbook.models.PayableModel;
import com.standalone.core.interfaces.DialogEventListener;
import com.standalone.core.utils.ValidationManager;


import java.util.Objects;

public class DialogFragment extends BottomSheetDialogFragment {
    public static String TAG = DialogFragment.class.getSimpleName();
    FragmentDialogBinding binding;
    ValidationManager validationManager;

    SqliteHelper sqliteHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, com.standalone.core.R.style.AppTheme_Dialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDialogBinding.inflate(getLayoutInflater());
        Objects.requireNonNull(getDialog()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        validationManager = ValidationManager.getInstance();

        sqliteHelper = new SqliteHelper(this.getContext());

        binding.btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validationManager.refresh();
                validationManager.doValidation(binding.edTitle).checkEmpty();
                validationManager.doValidation(binding.edAmount).checkEmpty();
                if (validationManager.isAllValid())
                    onSubmit();
            }
        });
    }

    private void onSubmit() {
        PayableModel model = new PayableModel();
        String title = Objects.requireNonNull(binding.edTitle.getEditText()).getText().toString().trim();
        String amount = Objects.requireNonNull(binding.edAmount.getEditText()).getText().toString().trim();

        model.setTitle(title);
        model.setAmount(Integer.parseInt(amount));

        Context context = getContext();
        if (context instanceof DialogEventListener) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("model", model);
            ((DialogEventListener) context).onDialogSubmit(getDialog(), bundle);
        }

        dismiss();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        Context context = getContext();
        if (context instanceof DialogEventListener) {
            ((DialogEventListener) context).onDialogCancel(dialog);
        }

        super.onDismiss(dialog);
    }
}
