package com.company.service;

import com.company.exception.UnrecognizedSymbolException;
import com.company.repository.SimpleOperatorRepository;

public class SimpleInputValidator implements InputValidator {

    private SimpleOperatorRepository operators;

    public SimpleInputValidator(SimpleOperatorRepository operators) {
        this.operators = operators;
    }

    @Override
    public boolean validateInputString(String inputString) {

        SimpleMessenger messenger = new SimpleMessenger();

        // Removing spaces
        inputString = inputString.replaceAll("\\s", "");

        String inputStringWithoutNumbersAndParentheses = inputString.replaceAll("[\\d.()]", "");

        for(int i = 0; i < inputStringWithoutNumbersAndParentheses.length(); i++){

            String oneCharacterAsString = inputStringWithoutNumbersAndParentheses.substring(i, i + 1);

            if(operators.getOperatorBySymbol(oneCharacterAsString) == null){

                throw new UnrecognizedSymbolException("Недопустимый символ в строке выражения: '"
                        + oneCharacterAsString + "'!");
            }
        }

        String[] splitParsedString = inputString.split("[\\d.()]+");

        for (String pieceOfParsedString : splitParsedString) {

            if(pieceOfParsedString.length() > 1){

                messenger.printErrorMessageAboutTooLongOperator(pieceOfParsedString);

                return false;
            }
        }

        return true;
    }
}
