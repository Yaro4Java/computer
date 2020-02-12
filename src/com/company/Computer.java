package com.company;

import com.company.entity.Operator;
import com.company.repository.SimpleOperatorRepository;
import com.company.service.SimpleInputValidator;
import com.company.service.SimpleMessenger;
import com.company.service.SimpleRPNBuilder;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class Computer {

    public static void main(String[] args) {

        SimpleMessenger messenger = new SimpleMessenger();

        SimpleOperatorRepository operators = new SimpleOperatorRepository(){
            {
                addOperator(new Operator("+", 0, (x, y) -> x.add(y)));
                addOperator(new Operator("-", 0, (x, y) -> x.subtract(y)));
                addOperator(new Operator("*", 1, (x, y) -> x.multiply(y)));
                addOperator(new Operator("/", 1, (x, y) -> x.divide(y, 2, RoundingMode.HALF_UP)));
            }
        };

        SimpleInputValidator validator = new SimpleInputValidator(operators);
        SimpleRPNBuilder rpnBuilder = new SimpleRPNBuilder(operators);

        String inputString = messenger.requestInfixNotationString(operators);

        while(!validator.validateInputString(inputString)){
            inputString = messenger.requestInfixNotationString(operators);
        }

        Stack<Object> rpnStack = rpnBuilder.buildRPNStackOutOfInfixNotationString(inputString);

        Stack<Object> stackOfOperands = new Stack<>();

        while(!rpnStack.empty()){

            while(isNumber(rpnStack.peek())){

                stackOfOperands.push(rpnStack.pop());
            }

            Operator operator = operators.getOperatorBySymbol((String) rpnStack.pop());
            BigDecimal secondOperand = (BigDecimal) stackOfOperands.pop();
            BigDecimal firstOperand = (BigDecimal) stackOfOperands.pop();

            stackOfOperands.push(operator.executeOperation(firstOperand, secondOperand));
        }

        messenger.printResultOfCalculations((BigDecimal) stackOfOperands.pop());
    }

    private static boolean isNumber(Object object){
        return object.getClass().toString().equals("class java.math.BigDecimal");
    }
}
