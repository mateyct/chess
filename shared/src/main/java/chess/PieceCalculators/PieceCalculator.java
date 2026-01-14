package chess.PieceCalculators;

import chess.ChessBoard;
import chess.ChessGame.TeamColor;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;

public abstract class PieceCalculator {
    protected final TeamColor pieceColor;

    public PieceCalculator(TeamColor pieceColor) {
        this.pieceColor = pieceColor;
    }

    /**
     * A method that will calculate the possible moves for a type of piece given a board and a starting position.
     * @param board The board being played on.
     * @param myPosition The starting position.
     * @return A collection of possible moves.
     */
    public abstract Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition);

    /**
     * A helper method to check if this piece can legally move to a spot.
     * @param board The board being played on.
     * @param position The desired location.
     * @param forCapture Whether this move is a capture move.
     * @return Whether the piece can move here.
     */
    private boolean checkOpenSpot(ChessBoard board, ChessPosition position, boolean forCapture) {
        int row = position.getRow();
        int col = position.getColumn();
        if (row < 1 || row > 8 || col < 1 || col > 8) {
            return false;
        }
        ChessPiece atPosition = board.getPiece(position);
        if (forCapture) {
            return atPosition != null && atPosition.getTeamColor() != pieceColor;
        }
        return atPosition == null;
    }

    /**
     * A helper method to determine possible spots for pieces that can move continuously across the board.
     * @param board The board being played on.
     * @param myPosition The starting position.
     * @param moves The collection of ChessMove to add to.
     * @param up 1, 0, -1, depending on direction of movement.
     * @param right 1, 0, -1, depending on direction of movement.
     */
    protected void loopMoveDirection (ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves, int up, int right) {
        boolean canContinue = true;
        ChessPosition currentPositionCheck = new ChessPosition(myPosition.getRow(), myPosition.getColumn());
        while (canContinue) {
            canContinue = false;
            ChessPosition newPosition = new ChessPosition(currentPositionCheck.getRow() + up, currentPositionCheck.getColumn() + right);
            // check normal move
            if (checkOpenSpot(board, newPosition, false)) {
                ChessMove move = new ChessMove(myPosition, newPosition, null);
                moves.add(move);
                canContinue = true;
            }
            // check capture move
            else if (checkOpenSpot(board, newPosition, true)) {
                ChessMove move = new ChessMove(myPosition, newPosition, null);
                moves.add(move);
            }
            currentPositionCheck = newPosition;
        }
    }
}
