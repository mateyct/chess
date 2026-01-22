package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board;
    private TeamColor currentTeam;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        currentTeam = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTeam;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.currentTeam = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return null;
        }
        Collection<ChessMove> uncheckedMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> checkedMoves = new ArrayList<>();
        for (ChessMove move : uncheckedMoves) {
            ChessPiece atEnd = board.getPiece(move.getEndPosition());
            board.addPiece(move.getStartPosition(), null);
            board.addPiece(move.getEndPosition(), piece);
            if (!isInCheck(piece.getTeamColor())) {
                checkedMoves.add(move);
            }
            board.addPiece(move.getStartPosition(), piece);
            board.addPiece(move.getEndPosition(), atEnd);
        }
        return checkedMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece == null || piece.getTeamColor() != currentTeam) {
            throw new InvalidMoveException("Attempted to move piece out of turn.");
        }
        Collection<ChessMove> moves = validMoves(move.getStartPosition());
        if (!moves.contains(move)) {
            throw new InvalidMoveException("Attempted to move piece out of turn.");
        }
        board.addPiece(move.getStartPosition(), null);
        if (move.getPromotionPiece() != null) {
            board.addPiece(move.getEndPosition(), new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
        }
        else {
            board.addPiece(move.getEndPosition(), piece);
        }
        currentTeam = currentTeam == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = board.getKingPosition(teamColor);
        return positionInCheck(teamColor, kingPosition);
    }

    /**
     * Determines if a king in the given location would be in check.
     *
     * @param teamColor The color of the king being tested.
     * @param position The position of the king being tested.
     * @return True if the king would be in check.
     */
    private boolean positionInCheck(TeamColor teamColor, ChessPosition position) {
        // check cardinals for rook or queen
        boolean inCheck = testLoopDirection(teamColor, position, 1, 0, new ChessPiece.PieceType[]{ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.ROOK});
        inCheck = inCheck || testLoopDirection(teamColor, position, -1, 0, new ChessPiece.PieceType[]{ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.ROOK});
        inCheck = inCheck || testLoopDirection(teamColor, position, 0, 1, new ChessPiece.PieceType[]{ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.ROOK});
        inCheck = inCheck || testLoopDirection(teamColor, position, 0, -1, new ChessPiece.PieceType[]{ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.ROOK});
        // check diagonals for bishop or queen
        inCheck = inCheck || testLoopDirection(teamColor, position, 1, 1, new ChessPiece.PieceType[]{ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.BISHOP});
        inCheck = inCheck || testLoopDirection(teamColor, position, -1, 1, new ChessPiece.PieceType[]{ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.BISHOP});
        inCheck = inCheck || testLoopDirection(teamColor, position, 1, -1, new ChessPiece.PieceType[]{ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.BISHOP});
        inCheck = inCheck || testLoopDirection(teamColor, position, -1, -1, new ChessPiece.PieceType[]{ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.BISHOP});
        // check corners for knight
        int row = position.getRow();
        int col = position.getColumn();
        ChessPosition[] knightSpots = {
                new ChessPosition(row + 1, col + 2),
                new ChessPosition(row + 1, col - 2),
                new ChessPosition(row - 1, col + 2),
                new ChessPosition(row - 1, col - 2),
                new ChessPosition(row + 2, col + 1),
                new ChessPosition(row + 2, col - 1),
                new ChessPosition(row - 2, col + 1),
                new ChessPosition(row - 2, col - 1),
        };
        for (ChessPosition checkPos : knightSpots) {
            if (checkPos.getRow() >= 1 && checkPos.getRow() <= 8 && checkPos.getColumn() >= 1 && checkPos.getColumn() <= 8) {
                ChessPiece pieceAt = board.getPiece(checkPos);
                if (pieceAt != null && pieceAt.getTeamColor() != teamColor && pieceAt.getPieceType() == ChessPiece.PieceType.KNIGHT) {
                    return true;
                }
            }
        }
        // check square for king
        ChessPosition[] kingSpots = {
                new ChessPosition(row + 1, col),
                new ChessPosition(row + 1, col + 1),
                new ChessPosition(row + 1, col  - 1),
                new ChessPosition(row, col + 1),
                new ChessPosition(row, col - 1),
                new ChessPosition(row - 1, col),
                new ChessPosition(row - 1, col + 1),
                new ChessPosition(row - 1, col - 1),
        };
        for (ChessPosition checkPos : kingSpots) {
            if (checkPos.getRow() >= 1 && checkPos.getRow() <= 8 && checkPos.getColumn() >= 1 && checkPos.getColumn() <= 8) {
                ChessPiece pieceAt = board.getPiece(checkPos);
                if (pieceAt != null && pieceAt.getTeamColor() != teamColor && pieceAt.getPieceType() == ChessPiece.PieceType.KING) {
                    return true;
                }
            }
        }
        // check one direction corners for pawn
        int checkDirection = teamColor == TeamColor.WHITE ? 1 : -1;
        ChessPosition[] pawnSpots = {
                new ChessPosition(row + checkDirection, col + 1),
                new ChessPosition(row + checkDirection, col - 1),
        };
        for (ChessPosition checkPos : pawnSpots) {
            if (checkPos.getRow() >= 1 && checkPos.getRow() <= 8 && checkPos.getColumn() >= 1 && checkPos.getColumn() <= 8) {
                ChessPiece pieceAt = board.getPiece(checkPos);
                if (pieceAt != null && pieceAt.getTeamColor() != teamColor && pieceAt.getPieceType() == ChessPiece.PieceType.PAWN) {
                    return true;
                }
            }
        }
        return inCheck;
    }

    /**
     * A helper method that checks a specific direction in a loop for specific pieces that put a King in check.
     * @param teamColor The color of king that is being tested.
     * @param startPosition The position of the king being tested.
     * @param up Amount to move up
     * @param right Amount to move right
     * @param testPieces A list of pieces to search for.
     * @return If there is a piece that endangers the king at position.
     */
    private boolean testLoopDirection(TeamColor teamColor, ChessPosition startPosition, int up, int right, ChessPiece.PieceType[] testPieces) {
        boolean continueLoopCheck = true;
        ChessPosition currentPosition = new ChessPosition(startPosition.getRow(), startPosition.getColumn());
        while (continueLoopCheck) {
            currentPosition = new ChessPosition(currentPosition.getRow() + up, currentPosition.getColumn() + right);
            if (currentPosition.getRow() < 1 || currentPosition.getRow() > 8 ||
                    currentPosition.getColumn() < 1 || currentPosition.getColumn() > 8) {
                break;
            }
            continueLoopCheck = false;
            ChessPiece piece = board.getPiece(currentPosition);
            if (piece == null) {
                continueLoopCheck = true;
            }
            else if (piece.getTeamColor() != teamColor && checkPieceType(piece, testPieces)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Helper method to determine if the given piece is one of the types in the provided list of types.
     * @param piece The piece being checked.
     * @param testPieces The list of piece types to check for.
     * @return True if the piece's type is in the provided list.
     */
    private boolean checkPieceType(ChessPiece piece, ChessPiece.PieceType[] testPieces) {
        for (ChessPiece.PieceType type : testPieces) {
            if (piece.getPieceType() == type) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }
        Collection<ChessPosition> teamPositions = board.getAllTeamPositions(teamColor);
        for (ChessPosition position : teamPositions) {
            if (!validMoves(position).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }
        Collection<ChessPosition> teamPositions = board.getAllTeamPositions(teamColor);
        for (ChessPosition position : teamPositions) {
            if (!validMoves(position).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
