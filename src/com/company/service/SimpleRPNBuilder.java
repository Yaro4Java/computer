package com.company.service;

import com.company.repository.SimpleOperatorRepository;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A tool to convert infix notation of arithmetic expression to postfix reverse polish notation (RPN).
 *
 * <p> A pair of words here about the contents of the class
 *
 *
 */
public class SimpleRPNBuilder implements RPNBuilder {

    /**
     * Keys to be used in actionsMap to get proper action while forming an RPN stack.
     *
     * <p> Each key is a list of two String elements: 1st element represents top of the temporary stack
     * and the 2nd element represents top of the input stack.
     */
    // --------------- CASE 1. WHEN TOP OF TEMPORARY STACK IS EMPTY ---------------

    private static final List<String> WHEN_TEMPORARY_STACK_EMPTY_AND_INPUT_STACK_NUMBER =
            Collections.unmodifiableList(Arrays.asList("EMPTY", "NUMBER"));

    private static final List<String> WHEN_TEMPORARY_STACK_EMPTY_AND_INPUT_STACK_OPEN_PARENTHESIS =
            Collections.unmodifiableList(Arrays.asList("EMPTY", "("));

    private static final List<String> WHEN_TEMPORARY_STACK_EMPTY_AND_INPUT_STACK_CLOSED_PARENTHESIS =
            Collections.unmodifiableList(Arrays.asList("EMPTY", ")"));

    private static final List<String> WHEN_TEMPORARY_STACK_EMPTY_AND_INPUT_STACK_OPERATOR =
            Collections.unmodifiableList(Arrays.asList("EMPTY", "OPERATOR"));


    // --------------- CASE 2. WHEN TOP OF TEMPORARY STACK IS OPEN PARENTHESIS ---------------

    private static final List<String> WHEN_TEMPORARY_STACK_OPEN_PARENTHESIS_AND_INPUT_STACK_EMPTY =
            Collections.unmodifiableList(Arrays.asList("(", "EMPTY"));

    private static final List<String> WHEN_TEMPORARY_STACK_OPEN_PARENTHESIS_AND_INPUT_STACK_NUMBER =
            Collections.unmodifiableList(Arrays.asList("(", "NUMBER"));

    private static final List<String> WHEN_TEMPORARY_STACK_OPEN_PARENTHESIS_AND_INPUT_STACK_OPEN_PARENTHESIS =
            Collections.unmodifiableList(Arrays.asList("(", "("));

    private static final List<String> WHEN_TEMPORARY_STACK_OPEN_PARENTHESIS_AND_INPUT_STACK_CLOSED_PARENTHESIS =
            Collections.unmodifiableList(Arrays.asList("(", ")"));

    private static final List<String> WHEN_TEMPORARY_STACK_OPEN_PARENTHESIS_AND_INPUT_STACK_OPERATOR =
            Collections.unmodifiableList(Arrays.asList("(", "OPERATOR"));


    // --------------- CASE 3. WHEN TOP OF TEMPORARY STACK IS OPERATOR ---------------

    private static final List<String> WHEN_TEMPORARY_STACK_OPERATOR_AND_INPUT_STACK_EMPTY =
            Collections.unmodifiableList(Arrays.asList("OPERATOR", "EMPTY"));

    private static final List<String> WHEN_TEMPORARY_STACK_OPERATOR_AND_INPUT_STACK_NUMBER =
            Collections.unmodifiableList(Arrays.asList("OPERATOR", "NUMBER"));

    private static final List<String> WHEN_TEMPORARY_STACK_OPERATOR_AND_INPUT_STACK_OPEN_PARENTHESIS =
            Collections.unmodifiableList(Arrays.asList("OPERATOR", "("));

    private static final List<String> WHEN_TEMPORARY_STACK_OPERATOR_AND_INPUT_STACK_CLOSED_PARENTHESIS =
            Collections.unmodifiableList(Arrays.asList("OPERATOR", ")"));

    private static final List<String> WHEN_TEMPORARY_STACK_OPERATOR_AND_INPUT_STACK_OPERATOR =
            Collections.unmodifiableList(Arrays.asList("OPERATOR", "OPERATOR"));

    // ------------------------------- END OF KEYS -------------------------------


    // -------- IDs of actions to execute while forming an RPN stack --------
    private static final int MOVE_TOP_ELEMENT_FROM_INPUT_STACK_TO_TEMPORARY_STACK = 1;
    private static final int MOVE_TOP_ELEMENT_FROM_TEMPORARY_STACK_TO_OUTPUT_STACK = 2;
    private static final int MOVE_TOP_ELEMENT_FROM_INPUT_STACK_TO_OUTPUT_STACK = 3;
    private static final int REMOVE_TOP_ELEMENTS_FROM_TEMPORARY_STACK_AND_FROM_INPUT_STACK = 4;
    private static final int DO_ACTION_ACCORDING_TO_PRIORITY_OF_OPERATORS = 5;
    private static final int THROW_NON_CONSISTENT_ARITHMETIC_EXPRESSION_EXCEPTION = 6;
    // ----------------------------------------------------------------------

    private SimpleOperatorRepository operatorRepository;

    private Stack<String> inputStack = new Stack<>();
    private Stack<BigDecimal> stackOfOperands = new Stack<>();
    private Stack<Object> outputStack = new Stack<>();


    private Map<List<String>, Integer> actionsMap = new HashMap<List<String>, Integer>(){
        {
            // Initializing actionsMap with keys and proper actions IDs
            // to build RPN stack ( postfix notation stack ready to use later for calculations )
            // out of input stack ( infix notation stack made from parsed initial string ).

            // --- CASE 1. WHEN TOP OF TEMPORARY STACK IS EMPTY ---

            put(WHEN_TEMPORARY_STACK_EMPTY_AND_INPUT_STACK_NUMBER,
                                MOVE_TOP_ELEMENT_FROM_INPUT_STACK_TO_OUTPUT_STACK);

            put(WHEN_TEMPORARY_STACK_EMPTY_AND_INPUT_STACK_OPEN_PARENTHESIS,
                                MOVE_TOP_ELEMENT_FROM_INPUT_STACK_TO_TEMPORARY_STACK);

            put(WHEN_TEMPORARY_STACK_EMPTY_AND_INPUT_STACK_CLOSED_PARENTHESIS,
                                THROW_NON_CONSISTENT_ARITHMETIC_EXPRESSION_EXCEPTION);

            put(WHEN_TEMPORARY_STACK_EMPTY_AND_INPUT_STACK_OPERATOR,
                                MOVE_TOP_ELEMENT_FROM_INPUT_STACK_TO_TEMPORARY_STACK);


            // --- CASE 2. WHEN TOP OF TEMPORARY STACK IS OPEN PARENTHESIS ---

            put(WHEN_TEMPORARY_STACK_OPEN_PARENTHESIS_AND_INPUT_STACK_EMPTY,
                                THROW_NON_CONSISTENT_ARITHMETIC_EXPRESSION_EXCEPTION);

            put(WHEN_TEMPORARY_STACK_OPEN_PARENTHESIS_AND_INPUT_STACK_NUMBER,
                                MOVE_TOP_ELEMENT_FROM_INPUT_STACK_TO_OUTPUT_STACK);

            put(WHEN_TEMPORARY_STACK_OPEN_PARENTHESIS_AND_INPUT_STACK_OPEN_PARENTHESIS,
                                MOVE_TOP_ELEMENT_FROM_INPUT_STACK_TO_TEMPORARY_STACK);

            put(WHEN_TEMPORARY_STACK_OPEN_PARENTHESIS_AND_INPUT_STACK_CLOSED_PARENTHESIS,
                                REMOVE_TOP_ELEMENTS_FROM_TEMPORARY_STACK_AND_FROM_INPUT_STACK);

            put(WHEN_TEMPORARY_STACK_OPEN_PARENTHESIS_AND_INPUT_STACK_OPERATOR,
                                MOVE_TOP_ELEMENT_FROM_INPUT_STACK_TO_TEMPORARY_STACK);


            // --- CASE 3. WHEN TOP OF TEMPORARY STACK IS OPERATOR ---

            put(WHEN_TEMPORARY_STACK_OPERATOR_AND_INPUT_STACK_EMPTY,
                                MOVE_TOP_ELEMENT_FROM_TEMPORARY_STACK_TO_OUTPUT_STACK);

            put(WHEN_TEMPORARY_STACK_OPERATOR_AND_INPUT_STACK_NUMBER,
                                MOVE_TOP_ELEMENT_FROM_INPUT_STACK_TO_OUTPUT_STACK);

            put(WHEN_TEMPORARY_STACK_OPERATOR_AND_INPUT_STACK_OPEN_PARENTHESIS,
                                MOVE_TOP_ELEMENT_FROM_INPUT_STACK_TO_TEMPORARY_STACK);

            put(WHEN_TEMPORARY_STACK_OPERATOR_AND_INPUT_STACK_CLOSED_PARENTHESIS,
                                MOVE_TOP_ELEMENT_FROM_TEMPORARY_STACK_TO_OUTPUT_STACK);

            put(WHEN_TEMPORARY_STACK_OPERATOR_AND_INPUT_STACK_OPERATOR,
                                DO_ACTION_ACCORDING_TO_PRIORITY_OF_OPERATORS);
        }
    };


    public SimpleRPNBuilder(SimpleOperatorRepository operatorRepository) {
        this.operatorRepository = operatorRepository;
    }

    @Override
    public Stack<Object> buildRPNStackOutOfInfixNotationString(String infixNotationString) {

        Stack<String> temporaryStackForOperatorsAndOpenParentheses = new Stack<>();

        Stack<Object> resultStack = new Stack<>();

        String topOfTemporaryStack;
        String topOfInputStack;

        clearMainStacks();

        parseInputStringAndLoadInputStackAndStackOfOperands(infixNotationString);

        while(!(inputStack.empty() && temporaryStackForOperatorsAndOpenParentheses.empty())){

            topOfTemporaryStack = temporaryStackForOperatorsAndOpenParentheses.empty() ?
                                                    "EMPTY" :
                                                    temporaryStackForOperatorsAndOpenParentheses.peek();

            topOfInputStack = inputStack.empty() ?
                                        "EMPTY" :
                                        inputStack.peek();

            List<String> listAsKey = keyToGetAction(topOfTemporaryStack, topOfInputStack);

            int actionID = actionsMap.get(listAsKey);

            // Actions dispatcher by action ID
            switch (actionID){

            case MOVE_TOP_ELEMENT_FROM_INPUT_STACK_TO_TEMPORARY_STACK:

                temporaryStackForOperatorsAndOpenParentheses.push(inputStack.pop());
                break;

            case MOVE_TOP_ELEMENT_FROM_TEMPORARY_STACK_TO_OUTPUT_STACK:

                outputStack.push(temporaryStackForOperatorsAndOpenParentheses.pop());
                break;

            case MOVE_TOP_ELEMENT_FROM_INPUT_STACK_TO_OUTPUT_STACK:

                outputStack.push(inputStack.pop());
                break;

            case REMOVE_TOP_ELEMENTS_FROM_TEMPORARY_STACK_AND_FROM_INPUT_STACK:

                temporaryStackForOperatorsAndOpenParentheses.pop();
                inputStack.pop();
                break;

            case DO_ACTION_ACCORDING_TO_PRIORITY_OF_OPERATORS:

                if(operatorRepository.getOperatorBySymbol(topOfInputStack).getPriority() >
                        operatorRepository.getOperatorBySymbol(topOfTemporaryStack).getPriority()){

                    temporaryStackForOperatorsAndOpenParentheses.push(inputStack.pop());

                } else {

                    outputStack.push(temporaryStackForOperatorsAndOpenParentheses.pop());

                }
                break;

            case THROW_NON_CONSISTENT_ARITHMETIC_EXPRESSION_EXCEPTION:

                System.out.println("Arithmetic Exception!!!");
                break;
            }
        }

        // Pushing numbers from stack of operands and operators from output stack into result stack
        while (!outputStack.empty()){

            if(outputStack.peek().equals("N")){
                resultStack.push(stackOfOperands.pop());
                outputStack.pop();
            } else {
                resultStack.push(outputStack.pop());
            }
        }

        return resultStack;
    }


    @Override
    public String translateInfixNotationStringToRPNString(String infixNotationString) {

        Stack<Object> rpnStack = buildRPNStackOutOfInfixNotationString(infixNotationString);

       String rpnString = "";

        while (!rpnStack.empty()){
            rpnString += rpnStack.pop() + ", ";
        }

        return rpnString.substring(0, rpnString.length() - 2);
    }

    public void parseInputStringAndLoadInputStackAndStackOfOperands(String inputString){

        // Returned below parsedString has 'N' characters against numbers in inputString
        String parsedString = moveNumbersFromInputStringToStackOfOperandsAndReplaceThemWithCharacterN(inputString);

        // Pushing parsedString elements into inputStack to work with
        for(int i = parsedString.length(); i > 0; i--){
            inputStack.push(parsedString.substring(i - 1, i));
        }
    }

    public String moveNumbersFromInputStringToStackOfOperandsAndReplaceThemWithCharacterN(String inputString){

        // Remove spaces from input string
        String parsedString = inputString.replaceAll("[\\s]+", "");

        // Replacing unary minuses in infix notation parsedString with "0-"
        parsedString = parsedString.replaceAll("(?<![\\d.])-", "0-");

        Pattern numberPattern = Pattern.compile("[\\d.]+");
        Matcher numberMatcher = numberPattern.matcher(parsedString);

        while(numberMatcher.find()) {
            String element = parsedString.substring(numberMatcher.start(), numberMatcher.end());
            stackOfOperands.push(new BigDecimal(element));
        }

        return parsedString.replaceAll("[\\d.]+", "N");
    }

    //
    public List<String> keyToGetAction(String topOfTemporaryStack, String topOfInputStack){

        String temporaryStackKey;
        String inputStackKey;

        if (topOfTemporaryStack.equals("N")){
            temporaryStackKey = "NUMBER";
        } else if(topOfTemporaryStack.equals("EMPTY")){
            temporaryStackKey = "EMPTY";
        } else if(topOfTemporaryStack.matches("[^()]+")){
            temporaryStackKey = "OPERATOR";
        } else {
            temporaryStackKey = topOfTemporaryStack;
        }

        if (topOfInputStack.equals("N")){
            inputStackKey = "NUMBER";
        } else if(topOfInputStack.equals("EMPTY")){
            inputStackKey = "EMPTY";
        } else if(topOfInputStack.matches("[^()]+")){
            inputStackKey = "OPERATOR";
        } else {
            inputStackKey = topOfInputStack;
        }

        return Arrays.asList(temporaryStackKey, inputStackKey);
    }

    private void clearMainStacks(){
        inputStack.clear();
        stackOfOperands.clear();
        outputStack.clear();
    }
}
