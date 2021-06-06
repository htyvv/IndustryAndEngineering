package com.example.test3;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.widget.DatePicker;

import java.util.Calendar;


public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private RegisterFragment parentFragment;

    DatePickerFragment(RegisterFragment fragment) {
        parentFragment = fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int year;
        int month;
        int day;
        Bundle bundle = getArguments();

        if (bundle != null) {
            //year = bundle.getInt("s_year");
            //month = bundle.getInt("s_month");
            //day = bundle.getInt("s_day");
        } else {

        }

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(),this,year,month,day);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        parentFragment.processDatePickerResult(year, month, day);
    }
}