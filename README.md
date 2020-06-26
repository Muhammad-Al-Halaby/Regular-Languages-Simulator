# Regular Languages Simulator
Regular Languages Simulator such as DFAs and NFAs based on Automata Theory


## Software Description
- The project is written in Java, the front end was created using java swing library and the back end was created using graphs and data structures.

- The application consists of three main panels:
  - Drawing panel where you can draw the DFA or NFA.
  - Control panel where you can start the simulation, freeze, unfreeze, trace back, or even remove a state from the resulted states.
  - States panel where you see each state and trace its path back or freeze it.
  
 - The application has similar feautres to the popular [JFLAP](http://www.jflap.org/) with a more user-friendly interface and a slightly better performance with large simulations.
 
## User Manual
1. Double click on the drawing panel to draw a node.
1. Right click on the node to set its attributes.
1. After drawing the graph you can start the simulation by pressing "simulate" button and go step by step to see all the states with some nice features like freezing and tracing back the whole path for a set of transitions.


## Examples

### Example 1

![Image of Example 1](/images/1.png)

This simulation is for a Language which accepts all strings without consecutive 1s. Providing the test case 01010101 which has to be accepted accordingly and it is then denoted by color green, also, a side panel appears in case that you want trace all steps that has come across the whole process.

### Example 2

This simulation is for a Language which accepts all strings with at least 2 0s or exactly two 1s. Providing the appearing test case 1100010101 which in the end turns to be accepted.

![Image of Example 2](/images/2.png)


### Example 3

This image shows a not accepted language by the automaton and it is denoted by color red.

![Image of Example 3](/images/3.png)


### Example 4

This image is the same one as the first example, but here we used the fast-run feature, so you do not go into simulating every step and just get the direct answer which here is a rejected one.

![Image of Example 4](/images/4.png)
### Example 5

Language:

![Image of Example 5](/images/5.1.png)

Simulation:

![Image of Example 5](/images/5.2.png)

As seen, the test case -7349.34 is accepted and denoted by color green.
