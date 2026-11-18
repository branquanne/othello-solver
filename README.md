# Othello Solver

An Othello/Reversi player written in Java. It uses alpha-beta pruning with a
heuristic evaluator and iterative deepening to find the best move within a
time limit.

## Requirements

- Java Development Kit (JDK)
- Bash (for the helper scripts)

## Run

Compile the source files:

```bash
bash othello.sh "" "" 1
```

Run the solver with a position and time limit in seconds:

```bash
bash othello.sh "<position>" <time_limit>
```

The position is a 65-character string containing the side to move followed by
the 8x8 board. For example:

```bash
bash othello.sh WEEEEEEEEEEEEEEEEEEEEEEEEEEEOXEEEEEEXOEEEEEEEEEEEEEEEEEEEEEEEEEEE 5
```

The program prints the selected move. If there are no legal moves, it prints a
pass move.

## Benchmark

To run matches against the included naive player for time limits from 2 to 10
seconds:

```bash
bash result-script.sh
```

Results are written to `results.log` and `results.csv`.

## Project structure

- `Othello.java` - command-line entry point and iterative-deepening loop
- `AlphaBeta.java` - alpha-beta game-tree search
- `HeuristicEvaluator.java` - board evaluation function
- `OthelloPosition.java` - board representation and legal moves
- `othello.sh` - compile/run helper
- `test_code/` - test player and match runner
