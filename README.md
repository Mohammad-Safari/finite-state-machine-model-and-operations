# Finite State Machine Model and Operations in Java

## Breif

* DFA Acceptor
* NFA to DFA Convertor
* REGEX to NFA

## DFA Acceptor

In the `App` file, at first, for a model machine including the alphabet, transitions in two ways (due to the difference in the nature of DFA and NFA).
The initial state and the final state are designed to be stored by the `loadedResults` class inside the APP class and for generating the machine.
The process of reading and writing this model on the file is accomplished by the methods `loadFromFile` and `writeToFile`.

In the `DeterministicFiniteAutomata` class, the process of consuming a string to the machine is possible using the `processInput` method.
it takes an order of the alphabet characters and starts to change its state. If the string is consumed in its entirety, the method returns `true`
(since in deterministic machine, the string must be completely consumable it is true), otherwise `false`.
The acceptance state of the string can be checked through `isAcceptingState` of the machine.
The input processing method can be called repeatedly (the state can change by each call), and finally the machine can be reset with the reset method
to return to the original state.

## NFA to DFA Convertor

In the nondeterministic machine implemented in the `NondeterministicFiniteAutomata` class in `AppII` by entering a string,
The machine can be in several states(due to its nondeterministic characteristic), all of which can be checked with the help of `getPossibleStates`.
Unlike the deterministic machine, this machine may not process every string and may get stuck before consuming whole string.
In this case, these states will not be present in `getPossibleStates` output, and only the states happened by the consumption of the entire string
will be kept. Therefore, the input processing method in this machine can be `false`, which means that the process is totally unsuccessful and
The string cannnot be accepted in no possible path from the state machine. In addition, this machine is able to move λ-lambda(or in other notations epsilon);
as a result a new method is needed named `lambdaClosure`, which gives us all other states from current state the machine could possibly gets, without
consumption of any character of the rest of string.


This behavior is mainly considered in the NFA to DFA converter. `NFA2DFAConverter` class to convert accroding to following steps:
  * The lambda closure takes the starting state and recursively checks for each input state what states can be reached.
  * And aggregation(set) of all result states beside the lambda closure of those states, will be kept as a new state, in the possibilty space
  of all subsets of the original machine(nfa) states

## REGEX to NFA
