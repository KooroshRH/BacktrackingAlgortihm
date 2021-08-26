# BacktrackingAlgortihm

## Overview

In this project, we want to solve a customized sudoku game by traversing our search tree recursively.  
We call this algorithm **Backtracking** and also we using **MRV** and **Degree** methods as heuristics.

## Customized Sudoku

In this game, we have a board that we want to fill with the correct number and also **Colors**.  
Despite the original sudoku here we have a color besides the number for each block that we want to put them on.  
Now let's review sudoku original rules:
- We must use all numbers in each row
- A number must be unique in each **row**
- A number must be unique in each **column**

But as mentioned earlier we added color to each block.  
So we have new rules now:
- A block must have a different color from its adjacent blocks
- Our colors are prioritized, so if the color of a specific block has a higher priority than each adjacent block, the number of that block must be higher than that adjacent block

And we want to solve each sample of this game with a backtracking algorithm.

## How our code works
We have a **Card** class that we store number, color, a list of possible colors, and a list of possible numbers for that card in this class.  
And also in a class named **State**, we store a two-dimensional array of cards that represents each of the possible states.  
When making each state, after selecting one of the possible numbers and colors, we amend possible numbers and colors of other blocks using **Forward checking**.  
And when we reach a state that is recognized as a possible target, we finish our algorithm and print that state.

## Sample
As input in the first line, we get our colors count and then our board size. (our board is square)  
After that in the second line, we get colors in prioritized order which the first one has the highest priority.  
Then at each row, we get the cards. Each card is represented with two characters, the first is number and then color. (if the number is unknown we using * and if the color is unknown we using #)  

### Input
```
5 3
r g b y p
1# *b *#
*# 3r *#
*g 1# *#
```

### Output
```
::::::::::::::::::
1p|2b|3g|
2y|3r|1p|
3g|1p|2y|
::::::::::::::::::
```
