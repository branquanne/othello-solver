/**
 * Heuristic Evaluator that takes 4 different heuristics in to consideration and
 * returns a score value for the move to be made.
 *
 * The score in each method returns a value between -100 and 100 and finally
 * they are summed with multiplying constants to favour certain heuristics.
 * 
 * @author Bran Quanne & Henrik Röjvall
 */
public class HeuristicEvaluator implements OthelloEvaluator {

  /*
   * Represent positional weights for each of the board squares
   */
  private static final int[][] POSITION_WEIGHTS = { { 100, -20, 10, 5, 5, 10, -20, 100 },
      { -20, -50, -2, -2, -2, -2, -50, -20 },
      { 10, -2, 5, 1, 1, 5, -2, 10 },
      { 5, -2, 1, 0, 0, 1, -2, 5 },
      { 5, -2, 1, 0, 0, 1, -2, 5 },
      { 10, -2, 5, 1, 1, 5, -2, 10 },
      { -20, -50, -2, -2, -2, -2, -50, -20 },
      { 100, -20, 10, 5, 5, 10, -20, 100 } };

  /*
   * Returns the score value for a move, stablity is 10 times more worth than the
   * others
   */
  public int evaluate(OthelloPosition pos) {
    return evaluateDiscParity(pos) + evaluateMobility(pos) + evaluateCorners(pos) + 10 * evaluateStability(pos);
  }

  /*
   * Returns a score between -100 and 100 depending on the difference in markers
   * on the board
   */
  private int evaluateDiscParity(OthelloPosition pos) {
    OthelloPosition position = (OthelloPosition) pos;
    int blackSquares = 0;
    int whiteSquares = 0;
    for (int i = 1; i <= OthelloPosition.BOARD_SIZE; i++) {
      for (int j = 1; j <= OthelloPosition.BOARD_SIZE; j++) {
        if (position.board[i][j] == 'W')
          whiteSquares++;
        if (position.board[i][j] == 'B')
          blackSquares++;
      }
    }
    return scoreValue(whiteSquares, blackSquares);
  }

  /*
   * Return a score between -100 and 100 depending on how many moves we can make
   */
  private int evaluateMobility(OthelloPosition pos) {
    try {
      int currentMoves = pos.getMoves().size();
      OthelloPosition opponent = pos.makeMove(new OthelloAction(0, 0, true));
      int opponentMoves = opponent.getMoves().size();

      int whiteMoves;
      int blackMoves;

      if (pos.toMove()) {
        whiteMoves = currentMoves;
        blackMoves = opponentMoves;
      } else {
        blackMoves = currentMoves;
        whiteMoves = opponentMoves;
      }

      return scoreValue(whiteMoves, blackMoves);
    } catch (IllegalMoveException exception) {
      throw new RuntimeException("Illegal move: ", exception);
    }

  }

  /*
   * Returns a score between -100 and 100 depending on how many corners are
   * captured
   */
  private int evaluateCorners(OthelloPosition pos) {
    int whiteCorners = 0;
    int blackCorners = 0;

    int[][] corners = { { 1, 1 }, { 1, OthelloPosition.BOARD_SIZE }, { OthelloPosition.BOARD_SIZE, 1 },
        { OthelloPosition.BOARD_SIZE, OthelloPosition.BOARD_SIZE } };

    for (int[] corner : corners) {
      char colour = pos.board[corner[0]][corner[1]];

      if (colour == 'W') {
        whiteCorners++;
      } else if (colour == 'B') {
        blackCorners++;
      }
    }
    return scoreValue(whiteCorners, blackCorners);
  }

  /*
   * Returns a score between -100 and 100 depending on how stable the position is
   */
  private int evaluateStability(OthelloPosition pos) {

    int whiteScore = 0;
    int blackScore = 0;

    for (int i = 1; i <= OthelloPosition.BOARD_SIZE; i++) {
      for (int j = 1; j <= OthelloPosition.BOARD_SIZE; j++) {
        char colour = pos.board[i][j];

        if (colour == 'W') {
          whiteScore += POSITION_WEIGHTS[i - 1][j - 1];
        } else if (colour == 'B') {
          blackScore += POSITION_WEIGHTS[i - 1][j - 1];
        }
      }
    }

    return scoreValue(whiteScore, blackScore);
  }

  /*
   * Helper method for homologating the score to a range between -100 and 100
   */
  private int scoreValue(int whiteScore, int blackScore) {
    int total = Math.abs(whiteScore) + Math.abs(blackScore);

    if (total == 0) {
      return 0;
    }

    return 100 * (whiteScore - blackScore) / total;
  }
}
