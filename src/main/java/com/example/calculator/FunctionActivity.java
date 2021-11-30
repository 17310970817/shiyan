package com.example.calculator;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class FunctionActivity extends AppCompatActivity {

    Button sinButton, cosButton, binButton, octButton, decButton, hexButton;
    TextView sinResult, cosResult;
    EditText sinValue, cosValue, binValue, octValue, decValue, hexValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function);
        initComponents();
    }

    private void initComponents() {
        sinButton = findViewById(R.id.sin_button);
        cosButton = findViewById(R.id.cos_button);
        sinResult = findViewById(R.id.sinResult_text);
        cosResult = findViewById(R.id.cosResult_text);
        sinValue = findViewById(R.id.sinValue_text);
        cosValue = findViewById(R.id.cosValue_text);
        binButton = findViewById(R.id.bin_button);
        octButton = findViewById(R.id.oct_button);
        decButton = findViewById(R.id.dec_button);
        hexButton = findViewById(R.id.hex_button);
        binValue = findViewById(R.id.binValue_text);
        octValue = findViewById(R.id.octValue_text);
        decValue =findViewById(R.id.decValue_text);
        hexValue = findViewById(R.id.hexValue_text);

        sinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sin();
            }
        });

        cosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cos();
            }
        });

        binButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bin();
            }
        });

        octButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oct();
            }
        });

        decButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dec();
            }
        });

        hexButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hex();
            }
        });
    }

    private void sin() {
        String val = String.valueOf(sinValue.getText());
        if (val.isEmpty()) {
            return;
        }
        Double value = Double.parseDouble(String.valueOf(sinValue.getText()));
        Double result = Math.sin(value);
        sinResult.setText("=" + result.toString());
    }

    private void cos() {
        String val = String.valueOf(cosValue.getText());
        if (val.isEmpty()) {
            return;
        }
        Double value = Double.parseDouble(String.valueOf(cosValue.getText()));
        Double result = Math.cos(value);
        cosResult.setText("=" + result.toString());
    }

    private void bin() {
        String val = String.valueOf(binValue.getText());
        if (val.isEmpty()) {
            return;
        }
        Integer value;
        try {
            value = Integer.valueOf(val, 2);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "输入格式有误，请重新输入", Toast.LENGTH_LONG).show();
            return;
        }
        octValue.setText(Integer.toOctalString(value));
        hexValue.setText(Integer.toHexString(value));
        decValue.setText(Integer.valueOf(value).toString());
    }

    private void oct() {
        String val = String.valueOf(octValue.getText());
        if (val.isEmpty()) {
            return;
        }
        Integer value;
        try {
            value = Integer.valueOf(val, 8);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "输入格式有误，请重新输入", Toast.LENGTH_LONG).show();
            return;
        }
        binValue.setText(Integer.toBinaryString(value));
        hexValue.setText(Integer.toHexString(value));
        decValue.setText(Integer.valueOf(value).toString());
    }

    private void dec() {
        String val = String.valueOf(decValue.getText());
        if (val.isEmpty()) {
            return;
        }
        Integer value;
        try {
            value = Integer.valueOf(val, 10);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "输入格式有误，请重新输入", Toast.LENGTH_LONG).show();
            return;
        }
        binValue.setText(Integer.toBinaryString(value));
        hexValue.setText(Integer.toHexString(value));
        octValue.setText(Integer.toOctalString(value));
    }

    private void hex() {
        String val = String.valueOf(hexValue.getText());
        if (val.isEmpty()) {
            return;
        }
        Integer value;
        try {
            value = Integer.valueOf(val, 16);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "输入格式有误，请重新输入", Toast.LENGTH_LONG).show();
            return;
        }
        binValue.setText(Integer.toBinaryString(value));
        octValue.setText(Integer.toOctalString(value));
        decValue.setText(Integer.valueOf(value).toString());
    }

}
