package ir.ac.aut.ce;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import ir.ac.aut.ce.App.loadedResult;

public class AppIII {
    public static void main(String[] args) throws IOException {
        // Read regular expression from input file
        var input = new BufferedReader(new FileReader("RE_Input_3.txt"));
        var alphabet = Arrays.asList(input.readLine().split(" ")).stream().map(str -> str.charAt(str.length() - 1))
                .collect(Collectors.toSet());
        var regex = input.readLine();
        input.close();

        var nfa = RegExp2NFAConverter.buildNFA(alphabet, regex);

        // Write NFA to output file
        AppII.writeToFile("NFA_Output_3.txt",
                new loadedResult(nfa.getAlphabet(), null,
                        nfa.getTransitions().stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue)),
                        nfa.getState(), nfa.getAcceptStates()));
    }

}
