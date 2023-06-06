package ir.ac.aut.ce;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author M.Safari
 * @since 2023.06
 */
public class DeterministicFiniteAutomata implements Automaton<String, Character> {
    private final Map<String, Map<String, String>> transitions;
    private final Set<String> acceptStates;
    private final Set<Character> alphabet;
    private final String startState;
    private String state;

    /**
     * states are inferred from transition key set and accepting states
     * 
     * @param alphabet
     * @param transition
     * @param acceptingStates
     */
    public DeterministicFiniteAutomata(Set<Character> alphabet,
            Map<String, Map<String, String>> transitions,
            String startState, Set<String> acceptStates) {
        this.alphabet = Collections.unmodifiableSet(alphabet);
        this.transitions = Collections.unmodifiableMap(transitions);
        this.acceptStates = Collections.unmodifiableSet(acceptStates);
        this.startState = startState;
        reset();
    }

    @Override
    public String getState() {
        return state;
    }

    @Override
    public void setState(String state) {
        if (!transitions.keySet().contains(state)) {
            return;
        }
        this.state = state;
    }

    @Override
    public boolean processInput(Iterable<Character> input) {
        var inputIterator = input.iterator();
        while (inputIterator.hasNext()) {
            var inputSymbol = String.valueOf(inputIterator.next());
            if (transitions.get(state) == null || !transitions.get(state).containsKey(inputSymbol)) {
                return false;
            }
            state = transitions.get(state).get(inputSymbol);
        }
        /*
         * does not mean that input has been accepted, DFA normally returns true for
         * every input because anyway it has a transition for any input in every state
         */
        return true;
    }

    @Override
    public boolean isAcceptingState() {
        return acceptStates.contains(state);

    }

    @Override
    public void reset() {
        this.state = startState;
    }

    @Override
    public Set<Character> getAlphabet() {
        return alphabet;
    }

    @Override
    public Set<String> getStates() {
        return transitions.keySet();
    }

    @Override
    public Set<Entry<String, Map<String, String>>> getTransitions() {
        return transitions.entrySet();
    }
}
