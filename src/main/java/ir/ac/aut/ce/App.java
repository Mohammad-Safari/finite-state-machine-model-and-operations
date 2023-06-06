package ir.ac.aut.ce;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class App {
    static class loadedResult {
        Set<Character> alphabet;
        Map<String, Map<String, String>> transitions;
        String startState;
        Set<String> acceptStates;

        loadedResult(Set<Character> alphabet,
                Map<String, Map<String, String>> transitions,
                String startState,
                Set<String> acceptStates) {
            this.alphabet = alphabet;
            this.transitions = transitions;
            this.startState = startState;
            this.acceptStates = acceptStates;
        }
    }

    public static loadedResult loadFromFile(String filename) throws FileNotFoundException {

        var file = new File(filename);
        var scanner = new Scanner(file);
        var lineReader = (Supplier<String[]>) () -> scanner.nextLine().split(" ");

        // Read the alphabet
        var lalphabet = Arrays.asList(lineReader.get()).stream().map(str -> str.charAt(str.length() - 1))
                .collect(Collectors.toSet());

        // Read the set of states and the initial state
        var lstates = Arrays.asList(lineReader.get()).stream().collect(Collectors.toSet());

        // Read the start state
        var lstartState = scanner.nextLine();

        // Read the set of accept states
        var lacceptStates = Arrays.asList(lineReader.get()).stream().collect(Collectors.toSet());

        // Read the transition function
        var ltransitions = lstates.stream().collect(
                Collectors.toMap(Function.identity(), s -> (Map<String, String>) new HashMap<String, String>()));
        while (scanner.hasNextLine()) {
            var parts = lineReader.get();
            String fromState = parts[0], inputSymbol = parts[1], toState = parts[2];
            ltransitions.get(fromState).put(inputSymbol, toState);
        }

        scanner.close();
        return new loadedResult(lalphabet, ltransitions, lstartState, lacceptStates);
    }

    public static void main(String[] args) throws FileNotFoundException {
        var filename = args.length > 1 ? args[1] : "DFA_Input_1.txt";
        var data = loadFromFile(filename);
        var automaton = new DeterministicFiniteAutomata(data.alphabet, data.transitions, data.startState,
                data.acceptStates);

        System.out.print("Enter a string to check: ");
        var scanner = new Scanner(System.in);
        var chars = scanner.nextLine().toCharArray();
        var input = IntStream.range(0, chars.length)
                .mapToObj(i -> chars[i])
                .collect(Collectors.toList());
        scanner.close();

        automaton.processInput(input);
        if (automaton.isAcceptingState()) {
            System.out.println("Accepted!");
        } else {
            System.out.println("Rejected!");
        }

        scanner.close();
    }
}
