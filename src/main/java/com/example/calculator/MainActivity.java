package com.example.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static com.example.calculator.ButtonState.*;

public class MainActivity extends AppCompatActivity {

    TextView formulaText, answerText;
    Button zeroButton, oneButton, twoButton, threeButton, fourButton, fiveButton, sixButton,
            sevenButton, eightButton, nineButton, decimalButton, plusButton, minusButton,
            mutiplyButton, divideButton, equalsButton, percentButton, radicalButton, deleteButton,
            clearButton, fxButton, tranButton;

    Calculate calculate = new Calculate();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar myToolbar = findViewById(R.id.my_toolbar);
//        setSupportActionBar(myToolbar);
        initAllComponents();
    }

    private void initAllComponents() {
        fxButton = findViewById(R.id.fx_button);
        setOnClickListener(fxButton, FX);
        tranButton = findViewById(R.id.tran_button);
        setOnClickListener(tranButton, TRAN);
        formulaText = findViewById(R.id.formula_text);
        answerText = findViewById(R.id.answer_text);
        decimalButton = findViewById(R.id.decimal_button);
        setOnClickListener(decimalButton, DECIMAL);
        zeroButton = findViewById(R.id.zero_button);
        setOnClickListener(zeroButton, 0);
        oneButton = findViewById(R.id.one_button);
        setOnClickListener(oneButton, 1);
        twoButton = findViewById(R.id.two_button);
        setOnClickListener(twoButton, 2);
        threeButton = findViewById(R.id.three_button);
        setOnClickListener(threeButton, 3);
        fourButton = findViewById(R.id.four_button);
        setOnClickListener(fourButton, 4);
        fiveButton = findViewById(R.id.five_button);
        setOnClickListener(fiveButton, 5);
        sixButton = findViewById(R.id.six_button);
        setOnClickListener(sixButton, 6);
        sevenButton = findViewById(R.id.seven_button);
        setOnClickListener(sevenButton, 7);
        eightButton = findViewById(R.id.eight_button);
        setOnClickListener(eightButton, 8);
        nineButton = findViewById(R.id.nine_button);
        setOnClickListener(nineButton, 9);
        plusButton = findViewById(R.id.plus_button);
        setOnClickListener(plusButton, PLUS);
        minusButton = findViewById(R.id.minus_button);
        setOnClickListener(minusButton, MINUS);
        mutiplyButton = findViewById(R.id.multiply_button);
        setOnClickListener(mutiplyButton, MUTIPLY);
        divideButton = findViewById(R.id.divide_button);
        setOnClickListener(divideButton, DIVIDE);
        equalsButton = findViewById(R.id.equals_button);
        setOnClickListener(equalsButton, EQUALS);
        zeroButton = findViewById(R.id.zero_button);
        setOnClickListener(zeroButton, 0);
        percentButton = findViewById(R.id.percent_button);
        setOnClickListener(percentButton, PERCENT);
        radicalButton = findViewById(R.id.radical_button);
        setOnClickListener(radicalButton, RADICAL);
        deleteButton = findViewById(R.id.delete_button);
        setOnClickListener(deleteButton, DELETE);
        clearButton = findViewById(R.id.clear_button);
        setOnClickListener(clearButton, CLEAR);
    }

    /**
     * 为每个按钮绑定对应的事件
     *
     * @param button 需要绑定事件的按钮
     * @param num 按钮的ID，对于数字按钮ID即为按钮代表的数字，
     *            对于其他按钮，使用ButtonState中对应定义的常量作为ID
     */
    private void setOnClickListener(final Button button, final int num) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer n = num;
                switch (n) {
                    case FX:
                        Intent intent1 = new Intent(MainActivity.this, FunctionActivity.class);
                        startActivity(intent1);
                        break;
                    case TRAN:
                        Intent intent2 = new Intent(MainActivity.this, TranActivity.class);
                        startActivity(intent2);
                        break;
                    case CLEAR:
                        calculate.clear();
                        break;
                    case DELETE:
                        calculate.delete();
                        break;
                    case EQUALS:
                        calculate.equals();
                        break;
                    case RADICAL:
                        calculate.radical();
                        break;
                    case PERCENT:
                        calculate.percent();
                        break;
                    case DECIMAL:
                        calculate.decimal();
                        break;
                    case PLUS:
                        calculate.arithmetic(PLUS);
                        break;
                    case MINUS:
                        calculate.arithmetic(MINUS);
                        break;
                    case MUTIPLY:
                        calculate.arithmetic(MUTIPLY);
                        break;
                    case DIVIDE:
                        calculate.arithmetic(DIVIDE);
                        break;
                    default:
                        calculate.appendNum(n);
                }
                answerText.setText(calculate.getAnswer());
                formulaText.setText(calculate
                        .toString()
                        .replace('/', '÷')
                        .replace('*', '×'));
            }
        });
    }


}
