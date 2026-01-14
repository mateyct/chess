package chess.PieceCalculators;

import chess.*;

import java.util.Collection;
import java.util.ArrayList;

public class PawnCalculator extends PieceCalculator {
    /**
     * The types of chess promotion pieces
     */
    private static final ChessPiece.PieceType[] PROMOTION_TYPES = {
        ChessPiece.PieceType.QUEEN,
        ChessPiece.PieceType.ROOK,
        ChessPiece.PieceType.KNIGHT,
        ChessPiece.PieceType.BISHOP
    };

    public PawnCalculator(ChessGame.TeamColor pieceColor) {
        super(pieceColor);
    }

    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        // get movement based on color
        int direction = pieceColor == ChessGame.TeamColor.WHITE ? 1 : -1;
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPosition moveOne = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn());
        boolean canMoveOne = checkOpenSpot(board, moveOne, false);
        if (canMoveOne) {
            addPawnMove(moves, myPosition, moveOne);
        }
        // handle case where pawn has not moved yet
        if (canMoveOne && (myPosition.getRow() == 2 && direction == 1 || myPosition.getRow() == 7 && direction == -1)) {
            ChessPosition moveTwo = new ChessPosition(myPosition.getRow() + direction * 2, myPosition.getColumn());
            if (checkOpenSpot(board, moveTwo, false)) {
                addPawnMove(moves, myPosition, moveTwo);
            }
        }
        // capture cases
        ChessPosition leftCapture = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn() - 1);
        ChessPosition rightCapture = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn() + 1);
        if (checkOpenSpot(board, leftCapture, true)) {
            addPawnMove(moves, myPosition, leftCapture);
        }
        if (checkOpenSpot(board, rightCapture, true)) {
            addPawnMove(moves, myPosition, rightCapture);
        }
        return moves;
    }

    /**
     * A helper method for handling a legal pawn position. Handles case where it's a promotion spot.
     * @param moves The Collection of moves to add to.
     * @param startPosition The starting position of the move.
     * @param endPosition The ending position of the move.
     */
    private void addPawnMove(Collection<ChessMove> moves, ChessPosition startPosition, ChessPosition endPosition) {
        if (endPosition.getRow() == 8 && pieceColor == ChessGame.TeamColor.WHITE || endPosition.getRow() == 1 && pieceColor == ChessGame.TeamColor.BLACK) {
            for (ChessPiece.PieceType pType : PROMOTION_TYPES) {
                moves.add(new ChessMove(startPosition, endPosition, pType));
            }
            return;
        }
        moves.add(new ChessMove(startPosition, endPosition, null));
    }
}
