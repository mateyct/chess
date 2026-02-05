package chess.piececalculators;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;
import java.util.ArrayList;

public class QueenCalculator extends PieceCalculator {
    public QueenCalculator(ChessGame.TeamColor pieceColor) {
        super(pieceColor);
    }

    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        BishopCalculator bishop = new BishopCalculator(this.pieceColor);
        RookCalculator rook = new RookCalculator(this.pieceColor);
        moves.addAll(bishop.calculateMoves(board, myPosition));
        moves.addAll(rook.calculateMoves(board, myPosition));
        return moves;
    }
}
