package com.company.entity;

import java.math.BigDecimal;
import java.util.function.BiFunction;

public class Operator {

    private String symbol;
    private int priority;
    private BiFunction<BigDecimal, BigDecimal, BigDecimal> operation;

    public Operator(String symbol,
                    int priority,
                    BiFunction<BigDecimal, BigDecimal, BigDecimal> operation) {

        this.symbol = symbol;
        this.priority = priority;
        this.operation = operation;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getPriority() {
        return priority;
    }

    public BigDecimal executeOperation(BigDecimal a, BigDecimal b){

        BigDecimal result;

        try{
            result = operation.apply(a, b);
        } catch (ArithmeticException exception){
            throw new UnsupportedOperationException("Запрещённая операция!", exception);
        }

        return result;
    }

    @Override
    public String toString() {
        return "Operator{" +
                "symbol='" + symbol + '\'' +
                ", priority=" + priority +
                '}';
    }
}
