package ir.ac.aut.ce;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class NFA2DFAConverter {

    private static final String STATE_COMBINATOR_DELIMITER = ",";
    private static final String TRAP_STATE_NAME = "T";

    public static DeterministicFiniteAutomata convert(NondeterministicFiniteAutomata nfa) {
        // Create sets to represent the states of the DFA
        Set<Set<String>> dfaStates = new HashSet<>();
        var startState = nfa.lambdaClosure(nfa.getState());
        dfaStates.add(startState);

        // Create a map to represent the transitions of the DFA
        Map<String, Map<String, String>> dfaTransitions = new HashMap<>();

        // Convert each set of NFA states to a DFA state
        convertHelper(startState, nfa, dfaStates, dfaTransitions);

        var dfaAcceptStates = dfaStates.stream()
                .filter(stateSet -> stateSet.stream().anyMatch(nfa::isAcceptingState))
                .map(set -> set.stream().collect(Collectors.joining(STATE_COMBINATOR_DELIMITER)))
                .collect(Collectors.toSet());

        // check for trap states
        dfaStates.stream()
                .map(set -> set.stream().collect(Collectors.joining(STATE_COMBINATOR_DELIMITER)))
                // if any states has no transition on any member of alphabet
                .forEach(state -> nfa.getAlphabet().stream().map(String::valueOf)
                        .forEach(sym -> {
                            dfaTransitions.putIfAbsent(state, new HashMap());
                            dfaTransitions.get(state).putIfAbsent(sym, TRAP_STATE_NAME);
                        }));
        // if trap state be needed
        for (var tr : dfaTransitions.values()) {
            if (tr.containsValue(TRAP_STATE_NAME)) {
                dfaTransitions.put(TRAP_STATE_NAME, new HashMap<>());
                nfa.getAlphabet().stream().map(String::valueOf)
                        .forEach(sym -> dfaTransitions.get(TRAP_STATE_NAME).put(sym, TRAP_STATE_NAME));
                break;
            }
        }
        return new DeterministicFiniteAutomata(
                nfa.getAlphabet(),
                dfaTransitions,
                 // Start state is always the first set processed
                startState.stream().collect(Collectors.joining(STATE_COMBINATOR_DELIMITER)),
                dfaAcceptStates);
    }

    private static void convertHelper(Set<String> nfaStates, NondeterministicFiniteAutomata nfa,
            Set<Set<String>> dfaStates, Map<String, Map<String, String>> dfaTransitions) {
        // Process each symbol in the alphabet
        for (char symbol : nfa.getAlphabet()) {
            var nextStates = new HashSet<String>();

            // all states reachable from the current NFA states using the current symbol
            var transitionsMap = nfa.getTransitions().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            for (var state : nfaStates) {
                var transitions = transitionsMap.getOrDefault(state, Collections.emptyMap());
                nextStates.addAll(transitions.getOrDefault(Character.toString(symbol), Collections.emptySet()));
            }

            // Add states reachable by lambda transition
            nextStates.addAll(
                    nextStates.stream().map(nfa::lambdaClosure).flatMap(Set::stream).collect(Collectors.toSet()));

            // Add the new DFA state to the transition table of the DFA if it doesn't exist
            if (!nextStates.isEmpty()) {
                if (!dfaStates.contains(nextStates)) {
                    dfaStates.add(nextStates);
                    convertHelper(nextStates, nfa, dfaStates, dfaTransitions);
                }
                var fromState = nfaStates.stream().collect(Collectors.joining(STATE_COMBINATOR_DELIMITER));
                var toState = nextStates.stream().collect(Collectors.joining(STATE_COMBINATOR_DELIMITER));
                dfaTransitions.computeIfAbsent(fromState, k -> new HashMap<>()).put(Character.toString(symbol),
                        toState);
            }
        }
    }

}
