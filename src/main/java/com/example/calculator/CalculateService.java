package com.example.calculator;

import java.util.Stack;

/**
 * 辅助 Calculate 类执行算式的具体计算
 */
class CalculateService {
    private Stack<Double> optr, opnd;
    private String arithmetic;
    private double result = 0;
    private int[][] operatorRelationship = {
            {'+', 3, 2},
            {'-', 3, 2},
            {'*', 5, 4},
            {'/', 5, 4},
            {'(', 1, 6},
            {')', 6, 1},
            {'=', 0, 0}};

    /**
     * 使用算式初始化
     *
     * @param arithmetic 待计算的算式
     */
    CalculateService(String arithmetic) {
        this.arithmetic = arithmetic;
        optr = new Stack<>();
        opnd = new Stack<>();
        optr.push((double) '=');
    }

    public void setArithmetic(String arithmetic) {
        this.arithmetic = arithmetic + '=';
    }


    /**
     * 计算算式
     *
     * @return 浮点格式的计算结果
     */
    Double getResult() {
        if (arithmetic.equals("=")) {
            return 0.0;
        }
        // countDecimalPlaces：记录小数点的位数，数值代表读取的数字需要缩小的倍数
        int countDecimalPlaces = 1;
        // passDecimalPoint：记录是否读取到小数点
        boolean passDecimalPoint = false;
        double temp = 0;

        // 遍历计算式
        for (int i = 0; i < arithmetic.length(); i++) {
            // 处理char，将char格式的数字转换为字面值
            if (isNum(arithmetic.charAt(i))) {
                do {
                    // 若已读取过小数点，则每读取一位新的连续的数字，缩小的倍数扩大10倍
                    if (passDecimalPoint)
                        countDecimalPlaces *= 10;
                    // 若读取到小数点，将passDecimalPoint置为true,增加循环变量，同时结束当次循环
                    if (arithmetic.charAt(i) == '.') {
                        passDecimalPoint = true;
                        i++;
                        continue;
                    }
                    // 每读取到新的一位数字，先将存储运算数的值扩大十倍，再加上读取到的数字的字面值
                    // 以12为例，先读取到'1'，转换为字面值1存入temp，再读取到'2'，将temp扩大十倍后加上'2'的字面值2，即得到12
                    temp = temp * 10 + ASCIINumToInt(arithmetic.charAt(i));
                    i++;
                }
                // 执行char转换int直到读取完一整个数字
                while (isNum(arithmetic.charAt(i)));
                // 将temp缩小countDecimalPlaces倍，若无小数点，此处该值应为1，有一位小数点应为10，有n位则缩小10^n倍
                temp /= countDecimalPlaces;
                // 重置两个与记录小数点有关的值
                countDecimalPlaces = 1;
                passDecimalPoint = false;
                // 将获得的操作数压入操作数栈
                opnd.push(temp);
                // 重置中间变量temp
                temp = 0;
                // 在do-while循环中多自增了一次i，此处进行修正
                i--;
            }
            // 处理读取到的操作符Theta2，将其与操作符栈顶元素Theta1进行对比
            else {
                // 如果Theta1与Theta2均为'#'且优先级相等，则此时操作数栈顶元素即为计算式计算结果
                if (judgeOperatorRelationship((int) Math.round(optr.peek()), arithmetic.charAt(i)) == 0
                        && arithmetic.charAt(i) == '=') {
                    result = opnd.peek();
                }
                // 如果Theta1优先级小于Theta2，将Theta2压入操作符栈
                else if (judgeOperatorRelationship((int) Math.round(optr.peek()), arithmetic.charAt(i)) == 2) {
                    optr.push((double) arithmetic.charAt(i));
                }
                // 如果Theta1优先级大于Theta2，进行运算操作，并将i自减从当前处继续判断读取到的运算式，而非跳转至下一个位置
                else if (judgeOperatorRelationship((int) Math.round(optr.peek()), arithmetic.charAt(i)) == 1) {
                    calculateArithmetic();
                    i--;
                }
                // 如果Theta1优先级等于Theta2，推出栈顶元素
                else if (judgeOperatorRelationship((int) Math.round(optr.peek()), arithmetic.charAt(i)) == 0) {
                    optr.pop();
                }
            }
        }
        return result;
    }


    /**
     * 从操作符栈取出操作符，从操作数栈取出两个操作数进行计算，结果压入操作数栈
     */
    private void calculateArithmetic() {
        double num1, num2, optr1 = optr.peek();
        optr.pop();
        num2 = opnd.peek();
        opnd.pop();
        num1 = opnd.peek();
        opnd.pop();
        double temp;
        if (optr1 == '+') {
            temp = num1 + num2;
        } else if (optr1 == '-') {
            temp = num1 - num2;
        } else if (optr1 == '*') {
            temp = num1 * num2;
        } else {
            temp = num1 / num2;
        }
        opnd.push(temp);
    }

    /**
     * 判断操作符的关系
     *
     * @param operatorOne 栈端操作符
     * @param operatorTwo 扫描端操作符
     * @return 返回操作符优先级，0表示优先级相等，1表示operatorOne优先级大于operatorTwo，
     * 2表示operatorOne优先级小于operatorTwo
     */
    private int judgeOperatorRelationship(int operatorOne, int operatorTwo) {
        if (getOperatorRelationship(operatorOne, 1) ==
                getOperatorRelationship(operatorTwo, 2)) {
            return 0;
        } else if (getOperatorRelationship(operatorOne, 1) >
                getOperatorRelationship(operatorTwo, 2)) {
            return 1;
        } else {
            return 2;
        }
    }

    /**
     * 获取操作符的优先关系值
     *
     * @param operator 操作符
     * @param order    操作符位置，1为栈端，2为扫描端
     * @return 操作符优先关系值
     */
    private int getOperatorRelationship(int operator, int order) {
        for (int[] i : operatorRelationship) {
            for (int j : i) {
                if (j == operator) {
                    return i[order];
                }
            }
        }
        return -1;
    }

    /**
     * 检查字符是否是数字
     *
     * @param c 待检查的字符
     * @return 待检查的字符是否是数字
     */
    private boolean isNum(char c) {
        return c <= 57 && c >= 48 || c == 46;
    }


    /**
     * 将字符转换为数字
     *
     * @param c 待转换的字符
     * @return 该字符对应的数字
     */
    private int ASCIINumToInt(char c) {
        return (int) c - 48;
    }
}
