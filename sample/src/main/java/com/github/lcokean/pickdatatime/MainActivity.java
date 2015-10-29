package com.github.lcokean.pickdatatime;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lcokean.library.AddressPickerDialog;
import com.github.lcokean.library.DatePickerDialog;
import com.github.lcokean.library.TimePickerDialog;


public class MainActivity extends Activity {

    private TextView mDatePick, mTime, mAddress;
    private RadioButton rbnDialogMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDatePick = (TextView) findViewById(R.id.tv_datePicker);
        mTime = (TextView) findViewById(R.id.tv_birth);
        mAddress = (TextView) findViewById(R.id.tv_address);

        rbnDialogMode = (RadioButton) findViewById(R.id.rbnDialogMode);
        rbnDialogMode.setChecked(true);

        mDatePick.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                DatePickerDialog mChangeBirthDialog = new DatePickerDialog(
                        MainActivity.this);
                // mChangeBirthDialog.setDate(2015, 03, 29);
                if (rbnDialogMode.isChecked()) {
                    mChangeBirthDialog
                            .setDialogMode(DatePickerDialog.DIALOG_MODE_BOTTOM);
                }
                mChangeBirthDialog.show();
                mChangeBirthDialog
                        .setDatePickListener(new DatePickerDialog.OnDatePickListener() {

                            @Override
                            public void onClick(String year, String month,
                                                String day) {
                                // TODO Auto-generated method stub
                                Toast.makeText(MainActivity.this,
                                        year + "-" + month + "-" + day,
                                        Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        mTime.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                TimePickerDialog mTimePickerDialog = new TimePickerDialog(
                        MainActivity.this);
                // mTimePickerDialog.setDate(2015, 03, 29);
                if (rbnDialogMode.isChecked()) {
                    mTimePickerDialog
                            .setDialogMode(TimePickerDialog.DIALOG_MODE_BOTTOM);
                }
                mTimePickerDialog.show();
                mTimePickerDialog.setTimePickListener(new TimePickerDialog.OnTimePickListener() {

                    @Override
                    public void onClick(int year, int month, int day,
                                        String hour, String minute) {
                        Toast.makeText(
                                MainActivity.this,
                                year + "-" + month + "-" + day + " " + hour
                                        + ":" + minute, Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }
        });

        mAddress.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                AddressPickerDialog mChangeAddressDialog = new AddressPickerDialog(
                        MainActivity.this);
                mChangeAddressDialog.setAddress("四川", "自贡");
                if (rbnDialogMode.isChecked()) {
                    mChangeAddressDialog
                            .setDialogMode(DatePickerDialog.DIALOG_MODE_BOTTOM);
                }
                mChangeAddressDialog.show();
                mChangeAddressDialog
                        .setAddresskListener(new AddressPickerDialog.OnAddressCListener() {

                            @Override
                            public void onClick(String province, String city) {
                                // TODO Auto-generated method stub
                                Toast.makeText(MainActivity.this,
                                        province + "-" + city,
                                        Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }
}
