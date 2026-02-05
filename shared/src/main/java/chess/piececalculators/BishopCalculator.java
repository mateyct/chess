package chess.piececalculators;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;
import java.util.ArrayList;

public class BishopCalculator extends PieceCalculator {
    public BishopCalculator(ChessGame.TeamColor pieceColor) {
        super(pieceColor);
    }

    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        // go up-left
        loopMoveDirection(board, myPosition, moves, 1, -1);
        // go up-right
        loopMoveDirection(board, myPosition, moves, 1, 1);
        // go down-left
        loopMoveDirection(board, myPosition, moves, -1, -1);
        // go down-right
        loopMoveDirection(board, myPosition, moves, -1, 1);
        return moves;
    }
}
