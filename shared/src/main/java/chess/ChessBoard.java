package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    /**
     * The starting order of pieces in a game of chess, discounting pawns.
     */
    private static final ChessPiece.PieceType[] STARTING_PIECE_ORDER = {
            ChessPiece.PieceType.ROOK,
            ChessPiece.PieceType.KNIGHT,
            ChessPiece.PieceType.BISHOP,
            ChessPiece.PieceType.QUEEN,
            ChessPiece.PieceType.KING,
            ChessPiece.PieceType.BISHOP,
            ChessPiece.PieceType.KNIGHT,
            ChessPiece.PieceType.ROOK
    };

    /**
     * The 2D array representing the grid of a chess board.
     */
    private ChessPiece[][] boardGrid;

    public ChessBoard() {
        boardGrid = new ChessPiece[8][8];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
       boardGrid[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return boardGrid[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        ChessPiece[][] board = new ChessPiece[8][8];
        addPieces(board);
        boardGrid = board;
    }

    /**
     * Adds all pieces on the board arranged for a new game.
     * @param board the board to use.
     */
    private void addPieces(ChessPiece[][] board) {
        for (int i = 0; i < board[0].length; i++) {
            board[0][i] = new ChessPiece(ChessGame.TeamColor.WHITE, STARTING_PIECE_ORDER[i]);
            board[1][i] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            board[6][i] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
            board[7][i] = new ChessPiece(ChessGame.TeamColor.BLACK, STARTING_PIECE_ORDER[i]);
        }
    }

    /**
     * Get the king of a specific team color
     * @param color The color of king to find.
     * @return The king for the specified color.
     */
    public ChessPosition getKingPosition(ChessGame.TeamColor color) {
        for (int i = 0; i < boardGrid.length; i++) {
            for (int j = 0; j < boardGrid[i].length; j++) {
                ChessPiece piece = boardGrid[i][j];
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == color) {
                    return new ChessPosition(i + 1, j + 1);
                }
            }
        }
        return null;
    }

    public Collection<ChessPosition> getAllTeamPositions(ChessGame.TeamColor color) {
        Collection<ChessPosition> positions = new ArrayList<>();
        for (int i = 0; i < boardGrid.length; i++) {
            for (int j = 0; j < boardGrid[i].length; j++) {
                ChessPiece piece = boardGrid[i][j];
                if (piece != null && piece.getTeamColor() == color) {
                    positions.add(new ChessPosition(i + 1, j + 1));
                }
            }
        }
        return positions;
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(boardGrid);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ChessBoard compare)) {
            return false;
        }
        return Arrays.deepEquals(boardGrid, compare.boardGrid);
    }

    @Override
    public String toString() {
        StringBuilder repr = new StringBuilder();
        for (int i = 7; i >= 0; i--) {
            for (ChessPiece piece : boardGrid[i]) {
                String toAdd = " ";
                if (piece != null) {
                    switch (piece.getPieceType()) {
                        case KING -> toAdd = "K";
                        case PAWN -> toAdd = "P";
                        case QUEEN -> toAdd = "Q";
                        case KNIGHT -> toAdd = "N";
                        case ROOK -> toAdd = "R";
                        case BISHOP -> toAdd = "B";
                    }
                }
                repr.append(toAdd);
            }
            repr.append("\n");
        }
        return repr.toString();
    }
}
