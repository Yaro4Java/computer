package com.company.service;

import java.util.Stack;

public interface RPNBuilder {

    Stack<Object> buildRPNStackOutOfInfixNotationString(String inputString);

    String translateInfixNotationStringToRPNString(String inputString);
}
