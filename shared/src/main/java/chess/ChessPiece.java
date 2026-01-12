package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private PieceType pieceType;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.pieceType = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * The types of chess promotion pieces
     */
    private final PieceType[] PROMOTION_TYPES = { PieceType.QUEEN, PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP };

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return pieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        switch (pieceType) {
            case PAWN -> {
                return pawnMoves(board, myPosition);
            }
            case KNIGHT -> {
                return knightMoves(board, myPosition);
            }
            case ROOK -> {
                return rookMoves(board, myPosition);
            }
            case BISHOP -> {
                return bishopMoves(board, myPosition);
            }
            default -> throw new RuntimeException("Not implemented");
        }
    }

    /**
     * A helper method to handle the possible bishop move locations.
     * @param board The board being played on.
     * @param myPosition The starting location.
     * @return A collection of all possible moves.
     */
    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
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

    /**
     * A helper method to handle the possible rook move locations.
     * @param board The board being played on.
     * @param myPosition The starting location.
     * @return A collection of all possible moves.
     */
    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
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

    /**
     * A helper method to determine possible spots for pieces that can move continuously across the board.
     * @param board The board being played on.
     * @param myPosition The starting position.
     * @param moves The collection of ChessMove to add to.
     * @param up 1, 0, -1, depending on direction of movement.
     * @param right 1, 0, -1, depending on direction of movement.
     */
    private void loopMoveDirection (ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves, int up, int right) {
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

    /**
     * A helper method to handle the possible knight move locations.
     * @param board The board being played on.
     * @param myPosition The starting location.
     * @return A collection of all possible moves.
     */
    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPosition[] possiblePositions = {
            new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn() - 1),
            new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn() + 1),
            new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn() - 1),
            new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn() + 1),
            new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 2),
            new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 2),
            new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 2),
            new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 2),
        };
        for (ChessPosition position : possiblePositions) {
            if (checkOpenSpot(board, position, false) || checkOpenSpot(board, position, true)) {
                moves.add(new ChessMove(myPosition, position, null));
            }
        }
        return moves;
    }

    /**
     * A helper method to handle the possible pawn move locations.
     * @param board The board being played on.
     * @param myPosition The starting location.
     * @return A collection of all possible moves.
     */
    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
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
            for (PieceType pType : PROMOTION_TYPES) {
                moves.add(new ChessMove(startPosition, endPosition, pType));
            }
            return;
        }
        moves.add(new ChessMove(startPosition, endPosition, null));
    }

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
            return atPosition != null && atPosition.pieceColor != pieceColor;
        }
        return atPosition == null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, pieceType);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ChessPiece compareTo)) {
            return false;
        }
        return pieceType == compareTo.pieceType && pieceColor == compareTo.pieceColor;
    }
}
