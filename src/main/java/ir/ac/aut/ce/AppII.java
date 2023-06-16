package ir.ac.aut.ce;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import ir.ac.aut.ce.App.loadedResult;

class AppII {
    private static final String SPACE_DELIMITER = " ";

    public static void writeToFile(String filename, loadedResult result) throws IOException {

        var pw = new PrintWriter(new FileWriter(filename, Charset.forName("UTF-8")));

        // Write the alphabet
        var sb1 = new StringBuilder();
        for (var c : result.alphabet) {
            sb1.append(c).append(SPACE_DELIMITER);
        }
        pw.println(sb1.toString().trim());

        // Write the set of states
        var sb2 = new StringBuilder();
        Set<String> aggregatedStates;
        if (result.nondeterministicTransitions == null) {
            aggregatedStates = result.transitions.values().stream().flatMap(e -> e.values().stream())
                    .collect(Collectors.toSet());
            aggregatedStates.addAll(result.transitions.keySet());
        } else {
            aggregatedStates = result.nondeterministicTransitions.values().stream().flatMap(e -> e.values().stream())
                    .collect(Collectors.toSet()).stream().flatMap(Set::stream).collect(Collectors.toSet());
            aggregatedStates.addAll(result.nondeterministicTransitions.keySet());
        }
        for (var s : aggregatedStates) {
            sb2.append(s).append(SPACE_DELIMITER);
        }
        pw.println(sb2.toString().trim());

        // Write the start state
        pw.println(result.startState);

        // Write the set of accept states
        var sb3 = new StringBuilder();
        for (var s : result.acceptStates) {
            sb3.append(s).append(SPACE_DELIMITER);
        }
        pw.println(sb3.toString().trim());

        if (result.nondeterministicTransitions == null) {
            for (var entry : result.transitions.entrySet()) {
                var fromState = entry.getKey();
                var trans = entry.getValue();
                for (var t : trans.entrySet()) {
                    var inputSymbol = t.getKey();
                    var toState = t.getValue();
                    pw.println(fromState + SPACE_DELIMITER + inputSymbol + SPACE_DELIMITER + toState);
                }
            }
        } else {
            for (var entry : result.nondeterministicTransitions.entrySet()) {
                var fromState = entry.getKey();
                var trans = entry.getValue();
                for (var t : trans.entrySet()) {
                    var inputSymbol = t.getKey();
                    var toStateSet = t.getValue();
                    for (var toState : toStateSet) {
                        pw.println(fromState + SPACE_DELIMITER + inputSymbol + SPACE_DELIMITER + toState);
                    }
                }
            }
        }

        pw.close();
    }

    public static void main(String[] args) throws IOException {
        var filename = args.length > 1 ? args[1] : "NFA_Input_2.txt";
        var data = App.loadFromFile(filename);
        var nfa = new NondeterministicFiniteAutomata(data.alphabet, data.nondeterministicTransitions, data.startState,
                data.acceptStates);

        var dfa = NFA2DFAConverter.convert(nfa);
        var transitions = dfa.getTransitions().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        writeToFile("DFA_Output_2.txt",
                new loadedResult(dfa.getAlphabet(), transitions, null, dfa.getState(), dfa.getAcceptStates()));

    }
}
