import java.util.LinkedList;

public class Othello {
	/**
	 * Main entry point for the Othello game search.
	 *
	 * Current behavior:
	 * - Uses Alpha-Beta pruning with a fixed search depth.
	 * - Prints the best move found at that depth.
	 *
	 * Assignment requirement:
	 * - Replace fixed-depth search with Iterative Deepening Search (IDS)
	 * that respects a time limit (provided as an argument).
	 *
	 * Usage:
	 * java Othello <position_string> <time_limit_seconds>
	 *
	 * Args:
	 * arg0: Position string (length 65, board representation).
	 * arg1: Time limit in seconds for IDS (float or int).
	 *
	 *
	 * Author: Henrik Björklund
	 */
	public static void main(String[] args) {

		String posString;
		OthelloPosition pos;
		OthelloAlgorithm algorithm;
		OthelloAction move = null;

		if (args.length > 0) {
			posString = args[0];
		} else {
			posString = "WEEEEEEEEEEEEEEEEEEEEEEEEEEEOXEEEEEEXOEEEEEEEEEEEEEEEEEEEEEEEEEEE";
		}
		// System.out.println(posString);
		pos = new OthelloPosition(posString);
		// pos.illustrate(); //Only for debugging. The test script has it's own print
		// method

		long startTime = System.currentTimeMillis();
		// Which evaluator (heuristics) should be used
		algorithm = new AlphaBeta(new HeuristicEvaluator());

		Long timeLimitMili = Long.parseLong(args[1]) * 1000L;
		if (timeLimitMili <= 0) {
			System.out.print("Invalid time limit");
			return;
		}

		LinkedList<OthelloAction> legalMoves = pos.getMoves();
		if (legalMoves.isEmpty()) {
			new OthelloAction(0, 0, true).print();
			return;
		}

		for (int depth = 1; System.currentTimeMillis() - startTime < timeLimitMili - 10; depth++) {
			algorithm.setSearchDepth(depth);
			OthelloAction candidate = algorithm.evaluate(pos, startTime, timeLimitMili);

			if (System.currentTimeMillis() - startTime < timeLimitMili - 100 && candidate != null) {
				move = candidate;
			} else {
				break;
			}
		}

		move.print();
	}
}