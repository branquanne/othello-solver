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
			OthelloAction pass = new OthelloAction(0, 0, true);
			return pass;
		}

		int max_index = 0;
		int max = Integer.MIN_VALUE;

		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;

		for (int i = 0; i < moves.size(); i++) {
			try {
				OthelloPosition newPos = pos.makeMove(moves.get(i));
				moves.get(i).setValue(
						alphaBetaSearch(newPos, searchDepth - 1, alpha, beta, false, startTime, timeLimitMili));

				if (moves.get(i).getValue() > max) {
					max = moves.get(i).getValue();
					max_index = i;
				}

				alpha = Math.max(alpha, max);

			} catch (IllegalMoveException e) {
				throw new RuntimeException("Illegal move", e);
			}
		}
		return moves.get(max_index);
	}

	private int alphaBetaSearch(OthelloPosition pos, int depth, int alpha, int beta, boolean isMaxPlayer,
			long startTime, long timeLimitMili) {
		if (depth == 0) {
			return evaluator.evaluate(pos);
		}

		if (startTime > timeLimitMili - 20) {
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
					beta = Math.min(beta, value);

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