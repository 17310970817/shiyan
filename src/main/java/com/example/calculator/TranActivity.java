package com.example.calculator;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.math.BigDecimal;

public class TranActivity extends AppCompatActivity {

    EditText lengthValue, volumeValue;
    TextView lengthResult, volumeResult;
    RadioGroup lengthValues, lengthResults, volumeValues, volumeResults;

    BigDecimal length, volume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tran);
        initComponents();
    }

    private void initComponents() {
        lengthValue = findViewById(R.id.length_value_text);
        volumeValue = findViewById(R.id.volume_value_text);

        lengthResult = findViewById(R.id.length_result_text);
        volumeResult = findViewById(R.id.volume_result_text);

        lengthValues = findViewById(R.id.length_values);
        lengthResults = findViewById(R.id.length_results);
        lengthValues.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                BigDecimal input = new BigDecimal(lengthValue.getText().toString());
                switch (checkedId) {
                    case R.id.length_value_km:
                        length = input.multiply(new BigDecimal("1000"));
                        break;
                    case R.id.length_value_m:
                        length = input;
                        break;
                    case R.id.length_value_cm:
                        length = input.multiply(new BigDecimal("0.01"));
                        break;
                    case R.id.length_value_mm:
                        length = input.multiply(new BigDecimal("0.001"));
                        break;
                }
            }
        });
        lengthResults.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String result;
                BigDecimal temp = new BigDecimal(0);
                switch (checkedId) {
                    case R.id.length_result_km:
                        temp = length.multiply(new BigDecimal("0.001"));
                        break;
                    case R.id.length_result_m:
                        temp = length;
                        break;
                    case R.id.length_result_cm:
                        temp = length.multiply(new BigDecimal("100"));
                        break;
                    case R.id.length_result_mm:
                        temp = length.multiply(new BigDecimal("1000"));
                        break;
                }
                result = temp.toString();
                lengthResult.setText(result);
            }
        });


        volumeValues = findViewById(R.id.volume_values);
        volumeResults = findViewById(R.id.volume_results);
        volumeValues.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                BigDecimal input = new BigDecimal(volumeValue.getText().toString());
                switch (checkedId) {
                    case R.id.volume_value_m2:
                        volume = input.multiply(new BigDecimal("1000"));
                        break;
                    case R.id.volume_value_dm2:
                        volume = input;
                        break;
                    case R.id.volume_value_cm2:
                        volume = input.multiply(new BigDecimal("0.001"));
                        break;
                }
            }
        });
        volumeResults.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String result;
                BigDecimal temp = new BigDecimal(0);
                switch (checkedId) {
                    case R.id.volume_result_m2:
                        temp = volume.multiply(new BigDecimal("0.001"));
                        break;
                    case R.id.volume_result_dm2:
                        temp = volume;
                        break;
                    case R.id.volume_result_cm2:
                        temp = volume.multiply(new BigDecimal("1000"));
                        break;
                }
                result = temp.toString();
                volumeResult.setText(result);
            }
        });
    }

}
