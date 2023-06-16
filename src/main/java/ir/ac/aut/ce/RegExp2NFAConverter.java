package ir.ac.aut.ce;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

public class RegExp2NFAConverter {
    // parenthese open and close states as the roll of nf start and end
    static Stack<Entry<String, String>> parenthesesStack = new Stack<>();
    static Entry<String, String> prevParen;
    static Map<String, Map<String, Set<String>>> transitions = new HashMap<>();
    static Integer currentId = 0;

    static void createTransition(String fState, String tState, Character input) {
        var set = new HashSet<String>();
        set.add(tState);
        if (transitions.containsKey(fState)) {
            if (transitions.get(fState).containsKey(input.toString())) {
                transitions.get(fState).get(input.toString()).add(tState);
            } else {
                transitions.get(fState).put(input.toString(), set);
            }
        } else {
            Map<String, Set<String>> t = new HashMap<>();
            if (input != null && tState != null) {
                t.put(String.valueOf(input), set);
            }
            transitions.put(fState, t);
        }
    }

    public static NondeterministicFiniteAutomata buildNFA(Set<Character> alph, String regex) {
        // implicit parentheses
        parenthesesStack.add(Map.entry("S", "E"));
        createTransition("S", currentId.toString(),
                NondeterministicFiniteAutomata.LAMBDA);
        createTransition("E", null, null);

        regex.chars().mapToObj(c -> (char) c).forEach(c -> {
            if (alph.contains(c)) {
                var fState = String.valueOf(currentId++);
                var tState = currentId.toString();
                createTransition(fState, tState, c);
            }
            switch (c) {
                case '^': // used for * opertator
                    // creating loop without creating any state
                    currentId--;
                    // if last group was in parentheses
                    if (prevParen == null) {
                        createTransition(currentId.toString(), String.valueOf(currentId - 1),
                                NondeterministicFiniteAutomata.LAMBDA);
                        createTransition(String.valueOf(currentId - 1), currentId.toString(),
                                NondeterministicFiniteAutomata.LAMBDA);
                    } else {
                        // or was just a alphabet
                        createTransition(prevParen.getKey(), prevParen.getValue(),
                                NondeterministicFiniteAutomata.LAMBDA);
                        createTransition(prevParen.getValue(), prevParen.getKey(),
                                NondeterministicFiniteAutomata.LAMBDA);
                    }
                    break;
                case '+':
                    var parentheses = parenthesesStack.peek();
                    // lambda transition of prev member to end of parentheses
                    createTransition(String.valueOf(currentId), parentheses.getValue(),
                            NondeterministicFiniteAutomata.LAMBDA);
                    // lambda transition of curr member from start of parentheses
                    createTransition(parentheses.getKey(), String.valueOf(++currentId),
                            NondeterministicFiniteAutomata.LAMBDA);
                    break;
                case '(':
                    // open transition numbered sequentially by currentId
                    var startState = String.valueOf(currentId++);
                    createTransition(startState, currentId.toString(),
                            NondeterministicFiniteAutomata.LAMBDA);
                    // end transition no determined sequence number
                    var endState = startState + "-PClose";
                    createTransition(endState, null, null);
                    parenthesesStack.add(Map.entry(startState, endState));
                    break;
                case ')':
                    var poppedParentheses = parenthesesStack.pop();
                    // lambda transition of last member to end of parentheses(is obsolete if
                    // parenthese has only one member)
                    createTransition(currentId.toString(), poppedParentheses.getValue(),
                            NondeterministicFiniteAutomata.LAMBDA);
                    // lambda transition to next state
                    createTransition(poppedParentheses.getValue(), String.valueOf(++currentId),
                            NondeterministicFiniteAutomata.LAMBDA);
                    // keeping the whole parenthese as last group
                    prevParen = poppedParentheses;
                    break;
            }
            if (c != ')') {
                prevParen = null;
            }
        });
        createTransition(currentId.toString(), "E", NondeterministicFiniteAutomata.LAMBDA);
        return new NondeterministicFiniteAutomata(alph, transitions, "S", Set.of("E"));
    }
}
