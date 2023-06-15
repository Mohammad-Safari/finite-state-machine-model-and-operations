package ir.ac.aut.ce;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author M.Safari
 * @since 2023.06
 */
public class NondeterministicFiniteAutomata implements Automaton<String, Character> {
    private final Map<String, Map<String, Set<String>>> transitions;
    private final Set<String> acceptStates;
    private final Set<Character> alphabet;
    private final String startState;
    private Set<String> possibleStates;
    private String state;

    public static final Character LAMBDA = 'λ';

    /**
     * states are inferred from transition key set and accepting states
     * 
     * @param alphabet
     * @param transition
     * @param acceptingStates
     */
    public NondeterministicFiniteAutomata(Set<Character> alphabet,
            Map<String, Map<String, Set<String>>> transitions,
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

    public Set<String> lambdaClosure(String state) {
        var closure = new HashSet<String>();
        lambdaClosureHelper(state, closure);
        return closure;
    }

    private void lambdaClosureHelper(String state, Set<String> closure) {
        closure.add(state);
        if (transitions.containsKey(state)) {
            var nextStates = transitions.get(state).getOrDefault(String.valueOf(LAMBDA), Collections.emptySet());
            nextStates.stream()
                    .filter(nextState -> !closure.contains(nextState))
                    .forEach(nextState -> lambdaClosureHelper(nextState, closure));
        }
    }

    private Set<String> processInputHelper(Iterable<Character> input, Set<String> possibleStates) {
        // Get the epsilon closure of each possible state
        Set<String> expandedStates = possibleStates.stream()
                .flatMap(state -> lambdaClosure(state).stream())
                .collect(Collectors.toSet());

        if (!input.iterator().hasNext()) {
            // Base case: no more input symbols
            return expandedStates;
        } else {
            // Recursive case: process next input symbol
            var symbol = String.valueOf(input.iterator().next());
            return expandedStates.stream()
                    // get all transitions from this state
                    .map(state -> transitions.get(state).entrySet().stream()
                            // which are allowed to use by this symbol
                            .filter(nondeterministicTransition -> nondeterministicTransition.getKey().equals(symbol))
                            // convert all of them in to a unit set of next state
                            .map(t -> t.getValue()).flatMap(Set::stream)
                            .collect(Collectors.toSet()))
                    // finally pass all of possible next state for next round of processing
                    .map(allowedStates -> processInputHelper(input, allowedStates)).flatMap(Set::stream)
                    // collect final states that consumed whole input into a unit set and go back
                    .collect(Collectors.toSet());
        }
    }

    /**
     * unlike DFA, NFA {@link#processInput} method changes {@link#state} only after
     * whole input gets consumed and includes states in {@link#possibleStates} which
     * has consumed input completely due to its undeterministic characteristic which
     * can produce any state at each branch of partially input sequence.
     */
    @Override
    public boolean processInput(Iterable<Character> input) {
        possibleStates = processInputHelper(input, possibleStates);
        if (possibleStates.size() == 0) {
            return false;
        }
        if (possibleStates.size() == 1) {
            state = possibleStates.iterator().next();
        }
        return true;
    }

    @Override
    public boolean isAcceptingState() {
        return acceptStates.contains(state);

    }

    public boolean isAcceptingState(String state) {
        return lambdaClosure(state).stream().anyMatch(acceptStates::contains);

    }

    public boolean canBeAcceptingState() {
        return possibleStates.stream().anyMatch(state -> acceptStates.contains(state));

    }

    @Override
    public void reset() {
        this.state = startState;
        this.possibleStates = Set.of(startState);
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
    public Set<Entry<String, Map<String, Set<String>>>> getTransitions() {
        return transitions.entrySet();
    }
}
