package com.company.service;

import com.company.repository.OperatorRepository;

import java.math.BigDecimal;

public interface Messenger {

    String requestInfixNotationString(OperatorRepository operatorRepository);

    void printErrorMessageAboutTooLongOperator(String tooLongOperator);

    void printResultOfCalculations(BigDecimal result);

}
