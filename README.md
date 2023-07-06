# Finite State Machine Model and Operations in Java

## Brief

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
After this transformation, the final states are determined according to the presence of non-deterministic machine final states in the combination of deterministic machine states.

In the next step, transitions and trap states are added to the deterministic machine if needed, and finally the machine is built.
With the help of the writeToFile method, which is in the `AppII` class, we write the car model in the file.
Since the operator of this converter recursively (and with the help of Java Streams) is similar to DFS, 
it usually does not generate additional states (it is closer to the optimal state) because it goes to the end of the path to generate a new state, 
and if after that a If the other mode wants to follow the same path (resulting in duplicate states in the `dfaTransitions` collection), that mode will not be expanded and therefore will not be added.
But if we used the queue (similar to BFS), it will probably be less complicated in terms of conversion time.

## REGEX to NFA

In AppIII, for this problem, it is a method of creating communication between machines through lambda transmissions. We start from the final definite state and the beginning called the non-explicit parentheses of the phrase, we move forward with each character. In such a way that for each member, the corresponding transitions to the next state are created and then by reading the operators for the operation

  - \* or ^* on the last first and last state of the previous group (it can be an alphabetic character or a combination of them in parentheses) In the path of states, a loop with lambda input is created.
  - \+ A lambda transfer is made between the next group being read and the viewing mode of the beginning of the current parenthesis which is kept in the stack. Also, a transition is formed between the previously read group and the end of the current parenthesis, whose state is created only.
  - A pair of states is created for open and closed parentheses and creates a connection between the previous state and the next state with two lambda transitions.
  - Establishes the relationship of the last group in the parentheses (before the parentheses) with its observation state with the help of a lambda transition and goes to the next state with another lambda transition.
  
At the same time, in order to be aware of the state interval of the previous group, the last rows that are removed from the stack are kept in the `preParen` variable for one more state.
Finally, the last state is connected to the end state with a lambda transition, and the machine is created from the map of the transition function and the alphabet and the predetermined initial and final states.
