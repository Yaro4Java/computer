package com.company.repository;

import com.company.entity.Operator;

public interface OperatorRepository {

    void addOperator(Operator operator);

    Operator getOperatorBySymbol(String Symbol);

    String getStringOfAllOperators();

}
