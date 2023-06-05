package ir.ac.aut.ce;

import java.util.Set;

/**
 * @author M.Safari
 * @since 2023.06
 */
public interface Automaton<S, A> {
    /**
     * 
     * @return the current state of the automaton
     */
    public S getState();

    /**
     * This method will set the current state of the automaton to the specified
     * state.
     * 
     * @param state
     */
    public void setState(S state);

    /**
     * This method will take an input and process it according to the transition
     * function of the automaton.
     * 
     * @param input
     * @return weather input could have been processed completely
     */
    public boolean processInput(Iterable<A> input);

    /**
     * 
     * @return whether the current state of the automaton is an accepting state
     */
    public boolean isAcceptingState();

    /**
     * This method will reset the automaton to its initial state.
     */
    public void reset();

    /**
     * 
     * @return the alphabet of the automaton
     */
    public Set<A> getAlphabet();

    /**
     * 
     * @return the set of states of the automaton
     */
    public Set<S> getStates();

    /**
     * 
     * @return the set of transitions of the automaton
     */
    public Set getTransitions();
}
