package com.example.calculator;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;


public class Calculate {

    private Stack<Character> formula;
    private String answer;
    private boolean canDecimal;
    private CalculateService service;
    private int prevButton;

    public Calculate() {
        formula = new Stack<>();
        answer = "";
        canDecimal = true;
        service = new CalculateService("");
        prevButton = ButtonState.NULL;
        Map<Integer, String> map = new HashMap<>();
    }

    /**
     * 重置计算器状态
     */
    public void clear() {
        formula = new Stack<>();
        answer = "";
        canDecimal = true;
        service = new CalculateService("");
        prevButton = ButtonState.NULL;
    }

    /**
     * 删除上一个输入
     */
    public void delete() {
        if (prevButton >= 0 && prevButton <= 9) {
            formula.pop();
            prevButton = formula.peek() - 48;
            updateAnswer();
        } else if (prevButton == ButtonState.DECIMAL) {
            formula.pop();
            canDecimal = true;
            prevButton = formula.peek() - 48;
        } else if (isOperator(prevButton)) {
            formula.pop();
            prevButton = formula.peek() - 48;
        }
    }

    /**
     * 计算当前算式的结果，并清空当前算式，将结果作为新的算式继续计算
     */
    public void equals() {
        formula.removeAllElements();
        for (int i = 0; i < answer.length(); i++) {
            formula.add(answer.charAt(i));
        }
    }

    /**
     * 对当前的结果做百分运算，得到的结果作为新的算式继续计算
     */
    public void percent() {
        updatePercentAnswer();
        equals();
    }

    /**
     * 对当前的结果做开根号运算，得到的结果作为新的算式继续计算
     */
    public void radical() {
        updateRadicalAnswer();
        equals();
    }

    /**
     * 执行四则运算
     *
     * @param buttonState 具体需要执行的四则运算，使用ButtonState常量传入参数。
     *                    例如执行加法：arithmetic(ButtonState.PLUS)
     */
    public void arithmetic(int buttonState) {
        if (isOperator(prevButton) || prevButton == ButtonState.DECIMAL) {
            formula.pop();
        }
        if (formula.isEmpty()) {
            formula.add('0');
        }
        char c = (char) (buttonState + 48);
        formula.add(c);
        canDecimal = true;
        prevButton = buttonState;
    }

    /**
     * 输入小数点执行的操作
     */
    public void decimal() {
        if (!canDecimal) {
            return;
        }
        if (prevButton < 0 || prevButton > 9) {
            if (prevButton == ButtonState.DECIMAL) {
                formula.pop();
            } else {
                formula.add('0');
            }
        }
        formula.add('.');
        canDecimal = false;
        prevButton = ButtonState.DECIMAL;
    }

    /**
     * 输入数字执行的操作
     *
     * @param num 输入的具体数字
     */
    public void appendNum(Integer num) {
        formula.add(num.toString().charAt(0));
        prevButton = num;
        updateAnswer();
    }

    /**
     * 获取当前算式的计算结果
     *
     * @return 当前算式的计算结果
     */
    public String getAnswer() {
        if (formula.isEmpty()) {
            return "";
        }
        if (answer.length() > 2 && answer.charAt(answer.length() - 1) == '0' && answer.charAt(answer.length() - 2) == '.') {
            return answer.substring(0, answer.length() - 2);
        } else {
            return answer;
        }
    }

    /**
     * 重新计算当前算式的计算结果
     */
    private void updateAnswer() {
        service.setArithmetic(formulaToString());
        answer = service.getResult().toString();
        if (answer.length() >= 2 && answer.charAt(answer.length() - 1) == '0' && answer.charAt(answer.length() - 2) == '.') {
            answer = answer.substring(0, answer.length() - 2);
        }
        System.out.println("Current answer is " + answer);
    }

    /**
     * 重新计算当前算式的百分计算结果
     */
    private void updatePercentAnswer() {
        service.setArithmetic(formulaToString());
        Double result = service.getResult() / 100;
        answer = result.toString();
        System.out.println("Current answer is " + answer);
    }

    /**
     * 重新计算当前算式的开根号结果
     */
    private void updateRadicalAnswer() {
        service.setArithmetic(formulaToString());
        Double result = service.getResult();
        if (result < 0) {
            result = 0.0;
        } else {
            result = Math.sqrt(result);
        }
        answer = result.toString();
        System.out.println("Current answer is " + answer);
    }

    /**
     * 将formula栈转换为CalculateService可以操作的字符串
     *
     * @return formula代表的字符串
     */
    private String formulaToString() {
        Stack<Character> temp = (Stack<Character>) formula.clone();
        if (temp.isEmpty()) {
            return "";
        }
        while (temp.peek() > '9' || temp.peek() < '0') {
            temp.pop();
        }
        StringBuilder result = new StringBuilder();
        while (!temp.isEmpty()) {
            result.append(temp.pop());
        }
        return result.reverse().toString();
    }

    /**
     * 判断操作符是否是加减乘除之一
     *
     * @param button 要判断的操作符
     */
    private boolean isOperator(int button) {
        return button == ButtonState.PLUS || button == ButtonState.MINUS
                || button == ButtonState.MUTIPLY || button == ButtonState.DIVIDE;
    }

    @Override
    public String toString() {
        Stack<Character> temp = (Stack<Character>) formula.clone();
        StringBuilder result = new StringBuilder();
        while (!temp.isEmpty()) {
            result.append(temp.pop());
        }
        return result.reverse().toString();
    }
}
