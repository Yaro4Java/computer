package com.company.repository;

import com.company.entity.Operator;
import java.util.HashSet;
import java.util.Set;

public class SimpleOperatorRepository implements OperatorRepository {

    private Set<Operator> operators = new HashSet<>();

    @Override
    public void addOperator(Operator operator) {
        operators.add(operator);
    }

    @Override
    public Operator getOperatorBySymbol(String symbol) {

        Operator result = null;

        for (Operator operator : operators) {
            if(operator.getSymbol().equals(symbol)){
                result = operator;
            }
        }

        return result;
    }

    @Override
    public String getStringOfAllOperators() {

        String stringOfOperators = "{";

        for (Operator operator : operators) {
            stringOfOperators += "'" + operator.getSymbol() + "', ";
        }

        return stringOfOperators.substring(0, stringOfOperators.length() - 2) + "}";
    }
}
