import java.util.LinkedList;

/**
 * This is where you implement the alpha-beta algorithm.
 * See <code>OthelloAlgorithm</code> for details
 * 
 * @author Henrik Bj&ouml;rklund
 *
 */
public class AlphaBeta implements OthelloAlgorithm {
	protected int searchDepth;
	protected static final int DefaultDepth = 7;
	protected OthelloEvaluator evaluator;

	public AlphaBeta() {
		evaluator = new CountingEvaluator();
		searchDepth = DefaultDepth;
	}

	public AlphaBeta(OthelloEvaluator eval) {
		evaluator = eval;
		searchDepth = DefaultDepth;
	}

	public AlphaBeta(OthelloEvaluator eval, int depth) {
		evaluator = eval;
		searchDepth = depth;
	}

	public void setEvaluator(OthelloEvaluator eval) {
		evaluator = eval;
	}

	public void setSearchDepth(int depth) {
		searchDepth = depth;
	}

	public OthelloAction evaluate(OthelloPosition pos, long startTime, long timeLimitMili) {
		LinkedList<OthelloAction> moves = pos.getMoves();

		if (moves.isEmpty()) {
			moves.add(new OthelloAction(0, 0, true));
		}

		boolean maximizing = pos.toMove();
		int bestIndex = 0;
		int bestValue = maximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;

		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;

		for (int i = 0; i < moves.size(); i++) {
			try {
				OthelloPosition newPos = pos.makeMove(moves.get(i));
				moves.get(i).setValue(
						alphaBetaSearch(newPos, searchDepth - 1, alpha, beta, !maximizing, startTime, timeLimitMili));

				if ((moves.get(i).getValue() > bestValue && maximizing)
						|| (!maximizing && moves.get(i).getValue() < bestValue)) {
					bestValue = moves.get(i).getValue();
					bestIndex = i;
				}

				if (maximizing) {
					alpha = Math.max(alpha, bestValue);
				} else {
					beta = Math.min(beta, bestValue);
				}

			} catch (IllegalMoveException e) {
				throw new RuntimeException("Illegal move", e);
			}
		}
		return moves.get(bestIndex);
	}

	private int alphaBetaSearch(OthelloPosition pos, int depth, int alpha, int beta, boolean isMaxPlayer,
			long startTime, long timeLimitMili) {
		if (depth == 0) {
			return evaluator.evaluate(pos);
		}

		if (System.currentTimeMillis() - startTime > timeLimitMili - 100) {
			return evaluator.evaluate(pos);
		}

		LinkedList<OthelloAction> moves = pos.getMoves();

		if (isMaxPlayer) {
			int max = Integer.MIN_VALUE;
			for (OthelloAction move : moves) {
				try {
					OthelloPosition newPos = pos.makeMove(move);

					int value = alphaBetaSearch(newPos, depth - 1, alpha, beta, false, startTime, timeLimitMili);
					max = Math.max(max, value);
					alpha = Math.max(alpha, max);

					if (beta <= alpha) {
						break;
					}

				} catch (IllegalMoveException e) {
					throw new RuntimeException("Illegal move", e);
				}
			}
			return max;
		} else {
			int min = Integer.MAX_VALUE;

			for (OthelloAction move : moves) {
				try {
					OthelloPosition newPos = pos.makeMove(move);
					int value = alphaBetaSearch(newPos, depth - 1, alpha, beta, true, startTime, timeLimitMili);

					min = Math.min(min, value);
					beta = Math.min(beta, min);

					if (beta <= alpha) {
						break;
					}

				} catch (IllegalMoveException e) {
					throw new RuntimeException("Illegal move", e);
				}
			}
			return min;
		}
	}
}