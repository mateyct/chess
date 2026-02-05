package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board;
    private ChessBoard testBoard;
    private TeamColor currentTeam;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        testBoard = new ChessBoard();
        testBoard.resetBoard();
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
        ChessPiece piece = testBoard.getPiece(startPosition);
        if (piece == null) {
            return null;
        }
        Collection<ChessMove> uncheckedMoves = piece.pieceMoves(testBoard, startPosition);
        Collection<ChessMove> checkedMoves = new ArrayList<>();
        for (ChessMove move : uncheckedMoves) {
            ChessPiece atEnd = testBoard.getPiece(move.getEndPosition());
            testBoard.addPiece(move.getStartPosition(), null);
            testBoard.addPiece(move.getEndPosition(), piece);
            if (!isInCheck(piece.getTeamColor())) {
                checkedMoves.add(move);
            }
            testBoard.addPiece(move.getStartPosition(), piece);
            testBoard.addPiece(move.getEndPosition(), atEnd);
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
        testBoard.addPiece(move.getStartPosition(), null);
        if (move.getPromotionPiece() != null) {
            board.addPiece(move.getEndPosition(), new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
            testBoard.addPiece(move.getEndPosition(), new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
        }
        else {
            board.addPiece(move.getEndPosition(), piece);
            testBoard.addPiece(move.getEndPosition(), piece);
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
        ChessPosition kingPosition = testBoard.getKingPosition(teamColor);
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
        boolean inCheck = checkByHorizontal(teamColor, position);
        inCheck = inCheck || checkByDiagonal(teamColor, position);
        inCheck = inCheck || checkByKnight(teamColor, position);
        inCheck = inCheck || checkByKing(teamColor, position);
        inCheck = inCheck || checkByPawn(teamColor, position);
        return inCheck;
    }

    /**
     * Private helper method to test if a team's king is in check by rook or queen.
     * @param teamColor Color of king to test.
     * @param position Position of the king to test.
     * @return True if the king is in check by a rook or queen.
     */
    private boolean checkByHorizontal(TeamColor teamColor, ChessPosition position) {
        boolean inCheck = testLoopDirection(
                teamColor,
                position,
                1,
                0,
                new ChessPiece.PieceType[]{ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.ROOK}
        );
        inCheck = inCheck || testLoopDirection(
                teamColor,
                position,
                -1,
                0,
                new ChessPiece.PieceType[]{ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.ROOK}
        );
        inCheck = inCheck || testLoopDirection(
                teamColor,
                position,
                0,
                1,
                new ChessPiece.PieceType[]{ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.ROOK}
        );
        inCheck = inCheck || testLoopDirection(
                teamColor,
                position,
                0,
                -1,
                new ChessPiece.PieceType[]{ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.ROOK}
        );
        return inCheck;
    }

    /**
     * Private helper method to test if a team's king is in check by bishop or queen.
     * @param teamColor Color of king to test.
     * @param position Position of the king to test.
     * @return True if the king is in check by a bishop or queen.
     */
    private boolean checkByDiagonal(TeamColor teamColor, ChessPosition position) {
        boolean inCheck = testLoopDirection(
                teamColor,
                position,
                1,
                1,
                new ChessPiece.PieceType[]{ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.BISHOP}
        );
        inCheck = inCheck || testLoopDirection(
                teamColor,
                position,
                -1,
                1,
                new ChessPiece.PieceType[]{ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.BISHOP}
        );
        inCheck = inCheck || testLoopDirection(
                teamColor,
                position,
                1,
                -1,
                new ChessPiece.PieceType[]{ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.BISHOP}
        );
        inCheck = inCheck || testLoopDirection(
                teamColor,
                position,
                -1,
                -1,
                new ChessPiece.PieceType[]{ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.BISHOP}
        );
        return inCheck;
    }

    /**
     * Helper method to check if a given team's king is in check by a knight.
     * @param teamColor Color of king to test.
     * @param position Position of the king to test.
     * @return True if the king is in check by a knight.
     */
    private boolean checkByKnight(TeamColor teamColor, ChessPosition position) {
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
        return checkByPositions(teamColor, knightSpots, ChessPiece.PieceType.KNIGHT);
    }

    /**
     * Helper method to check if a given team's king is in check by a king.
     * @param teamColor Color of king to test.
     * @param position Position of the king to test.
     * @return True if the king is in check by a king.
     */
    private boolean checkByKing(TeamColor teamColor, ChessPosition position) {
        int row = position.getRow();
        int col = position.getColumn();
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
        return checkByPositions(teamColor, kingSpots, ChessPiece.PieceType.KING);
    }

    /**
     * Helper method to check if a given team's king is in check by a pawn.
     * @param teamColor Color of king to test.
     * @param position Position of the king to test.
     * @return True if the king is in check by a pawn.
     */
    private boolean checkByPawn(TeamColor teamColor, ChessPosition position) {
        int row = position.getRow();
        int col = position.getColumn();
        int checkDirection = teamColor == TeamColor.WHITE ? 1 : -1;
        ChessPosition[] pawnSpots = {
                new ChessPosition(row + checkDirection, col + 1),
                new ChessPosition(row + checkDirection, col - 1),
        };
        return checkByPositions(teamColor, pawnSpots, ChessPiece.PieceType.PAWN);
    }

    /**
     * Private helper method to test if a team's king is in check by a given piece in a list of possible spots.
     * @param teamColor Color of king to test.
     * @param possiblePositions List of positions to check for this piece.
     * @param dangerPiece The type of ChessPiece to check for.
     * @return True if the king is in check by the given piece.
     */
    private boolean checkByPositions(TeamColor teamColor, ChessPosition[] possiblePositions, ChessPiece.PieceType dangerPiece) {
        for (ChessPosition checkPos : possiblePositions) {
            if (checkPos.getRow() >= 1 && checkPos.getRow() <= 8 && checkPos.getColumn() >= 1 && checkPos.getColumn() <= 8) {
                ChessPiece pieceAt = testBoard.getPiece(checkPos);
                if (pieceAt != null && pieceAt.getTeamColor() != teamColor && pieceAt.getPieceType() == dangerPiece) {
                    return true;
                }
            }
        }
        return false;
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
            ChessPiece piece = testBoard.getPiece(currentPosition);
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
        Collection<ChessPosition> teamPositions = testBoard.getAllTeamPositions(teamColor);
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
        Collection<ChessPosition> teamPositions = testBoard.getAllTeamPositions(teamColor);
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
        try {
            this.testBoard = (ChessBoard) board.clone();
        }
        catch (CloneNotSupportedException e) {
            System.out.println(e.toString());
        }
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, currentTeam);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ChessGame compareTo)) {
            return false;
        }
        return board.equals(compareTo.board) && currentTeam == compareTo.currentTeam;
    }
}
