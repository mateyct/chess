package chess;

import chess.PieceCalculators.*;

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
        PieceCalculator calculator = null;
        switch (pieceType) {
            case PAWN -> calculator = new PawnCalculator(pieceColor);
            case KNIGHT -> calculator = new KnightCalculator(pieceColor);
            case ROOK -> calculator = new RookCalculator(pieceColor);
            case BISHOP -> calculator = new BishopCalculator(pieceColor);
            case QUEEN -> calculator = new QueenCalculator(pieceColor);
            case KING -> calculator = new KingCalculator(pieceColor);
        }
        if (calculator == null) {
            throw new RuntimeException("Invalid piece type.");
        }
        return calculator.calculateMoves(board, myPosition);
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
