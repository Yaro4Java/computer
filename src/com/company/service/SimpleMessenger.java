package com.company.service;

import com.company.repository.OperatorRepository;
import java.math.BigDecimal;
import java.util.Scanner;

public class SimpleMessenger implements Messenger {

    @Override
    public String requestInfixNotationString(OperatorRepository operators) {

        System.out.print("\nВведите математическое выражение, используя операторы " +
                operators.getStringOfAllOperators() +"\n-> ");

        Scanner scanner = new Scanner( System. in);

        return scanner.nextLine();
    }

    @Override
    public void printErrorMessageAboutTooLongOperator(String tooLongOperator) {
        System.out.println("Длина оператора '" + tooLongOperator +
                "' превышает 1 символ! Попробуйте ещё раз.");
    }

    @Override
    public void printResultOfCalculations(BigDecimal result) {
        System.out.println("= " + result);

    }
}
