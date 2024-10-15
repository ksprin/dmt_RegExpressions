import java.util.*;

public class Main{

    /* A quicker way to print things out */
    public static void p(String toPrint){
        System.out.println(toPrint);
    }

    public static void main(String[] args){

        /* Reads in the input and calls appropriate methods. */
        /* You DO NOT need to change anything here, but should read it over. */

        p("Enter Regular Expression:");

        Scanner in = new Scanner(System.in);
        String regEx = in.next();

        p("The expression you entered is: " + regEx);

        /* Build the NFA from the regular expression */
        NFA nfa = buildNFA(regEx);

        /* You can uncomment this line if you want to see the */
        /* machine your buildNFA method produced */
        //p("Machine: " + nfa);

        /* Read in the number of strings */
        int n = in.nextInt();

        for(int i=0; i<n; i++){
            String input = in.next();

            /* See if the NFA accepts it! */
            if(nfa.acceptsString(input)) p("YES");
            else p("NO");
        }
    }


    /*
     * buildNFA: Given a regular expression as a string, build the NFA object that
     * represents a machine that would accept that regular expression.
     * Psuedo-code is provided for your convenience
     */
    public static NFA buildNFA(String exp){

        /* TODO: IMPLEMENT THIS METHOD */
        /* --------------------------------------------- */

        /* Case 1 - Base Case: exp is empty string, nothing to do */
        if (exp.isEmpty()){
            return null;
        }

        /* Case 2 - Look for U operator (will never be inside parens so don't need to worry about that) */
        boolean hasU = exp.contains("U");

		//If exp contains "U" operators
		if(hasU){
            //Split exp into all the segments between the Us (e.g., aaUddUda => [aa,dd,da])
            String[] splitU = exp.split("U");

            //create result array for the NFA of the size of the amount of segments
            NFA[] results = new NFA[splitU.length];

            //Recursively call buildNFA on each individual segment (e.g., aa)
            for(int i = 0; i < splitU.length; i++){
                results[i] = buildNFA(splitU[i]);
            }
            //Call the union() method on the NFA objects returns to patch them together.
            //create start
            if (results.length < 1){
                return null;
            }
            NFA unioned = results[0];
            //start at index 1 since index 0 is already in unioned
            for(int i = 1; i < results.length; i++){
                unioned.union(results[i]);
            }

            //return the unioned NFA
            return unioned;
        }

        //create char array of string
        char[] expChars = exp.toCharArray();

        if (expChars.length < 1){
            return null;
        }
        /* Case 3 - First character of exp is 'a' or 'd' */
		//If first character is 'a' or 'd'
        if (expChars[0] == 'a' || expChars[0] == 'd'){
            //Create an NFA object that has start state and single 'a' / 'd'
            NFA nfa = new NFA();

            //get/create needed states
            int startState = nfa.getStartState();
            int finalState = nfa.addState();

            //add transition
            nfa.addTransition(startState, expChars[0], finalState);

            //set final state
            nfa.addFinalState(finalState);

            if (expChars.length >= 2){
                //If the character after the 'a' or 'd' is the * operator
                if(expChars[1] == '*'){
                    //call star() on the nfa you just built
                    nfa.star();

                    //call buildNFA on the rest of the string (index 2 to end)
                    String substring = exp.substring(2);
                    NFA substringNFA = buildNFA(substring);

                    //concatenate() with the NFA for rest of the expression (after the star)
                    nfa.concatenate(substringNFA);
                }
                //Else if the character after 'a' or 'd' is not the * operator
                else{
                    //call buildNFA on the rest of the string (index 1 to end)
                    String substring = exp.substring(1);
                    NFA substringNFA = buildNFA(substring);

                    //just concatenate() with the NFA for rest of the expression
                    nfa.concatenate(substringNFA);
                }
            }
            //Return the NFA that was built
            return nfa;
        }

        // i am using the expChars from above
        /* Case 4 - First character is an open paren */

        //* If first character is open paren *//
        if (expChars[0] == '('){

            int countRightParen = 1;
            //* Work your way down the exp to find index of closing paren that matches this one.
            // for loop increments count when not right paren and breaks when it finds the first match
            for (int i = 1; i < expChars.length; i++){
                if (expChars[i] != ')'){
                    countRightParen++;
                }
                else {
                    break;
                }
            }

            //* Call buildNFA() on everything within the parentheses*//

            // call buildNFA on NFA in the parenthesis (index 1 to countRightParen-1)
            String substring = exp.substring(1, countRightParen);
            NFA substringNFA = buildNFA(substring);

            //* Call star() on this NFA (because right paren must have * after it)
            substringNFA.star();

            //* Concatenate with the NFA for the rest of the expression after the *

            //call buildNFA on the rest of the string (index after right parenthesis and star to end)
            String endSubstring = exp.substring(countRightParen+2);
            NFA endSubstringNFA = buildNFA(endSubstring);

            //just concatenate() with the NFA for rest of the expression
            substringNFA.concatenate(endSubstringNFA);

            return substringNFA;
        }


        /* --------------------------------------------- */

        /* Should never happen...but here so code compiles */
        return null;

    }
}
