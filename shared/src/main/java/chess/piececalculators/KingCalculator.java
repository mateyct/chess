package chess.piececalculators;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;
import java.util.ArrayList;

public class KingCalculator extends PieceCalculator {
    public KingCalculator(ChessGame.TeamColor pieceColor) {
        super(pieceColor);
    }

    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPosition[] possiblePositions = {
            new ChessPosition(myPosition.getRow(), myPosition.getColumn() - 1),
            new ChessPosition(myPosition.getRow(), myPosition.getColumn() + 1),
            new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()),
            new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()),
            new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1),
            new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1),
            new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1),
            new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1),
        };
        for (ChessPosition position : possiblePositions) {
            if (checkOpenSpot(board, position, false) || checkOpenSpot(board, position, true)) {
                moves.add(new ChessMove(myPosition, position, null));
            }
        }
        return moves;
    }
}
