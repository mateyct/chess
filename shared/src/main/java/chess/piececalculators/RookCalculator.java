package chess.piececalculators;

import chess.ChessBoard;
import chess.ChessGame.TeamColor;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class RookCalculator extends PieceCalculator {
    public RookCalculator(TeamColor pieceColor) {
        super(pieceColor);
    }

    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        // go up
        loopMoveDirection(board, myPosition, moves, 1, 0);
        // go left
        loopMoveDirection(board, myPosition, moves, 0, -1);
        // go right
        loopMoveDirection(board, myPosition, moves, 0, 1);
        // go down
        loopMoveDirection(board, myPosition, moves, -1, 0);
        return moves;
    }
}
