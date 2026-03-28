package ui;

import chess.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static ui.EscapeSequences.*;

public class ClientChessBoard {
    private static final String[] LETTERS = {
        EMPTY,
        " a ",
        " b ",
        " c ",
        " d ",
        " e ",
        " f ",
        " g ",
        " h ",
        EMPTY
    };

    private Set<ChessPosition> legalMoves;
    private ChessPosition startPosition;

    private static final Map<ChessPiece.PieceType, String> BLACK_PIECE_MAP = Map.of(
        ChessPiece.PieceType.KING, BLACK_KING,
        ChessPiece.PieceType.QUEEN, BLACK_QUEEN,
        ChessPiece.PieceType.ROOK, BLACK_ROOK,
        ChessPiece.PieceType.KNIGHT, BLACK_KNIGHT,
        ChessPiece.PieceType.BISHOP, BLACK_BISHOP,
        ChessPiece.PieceType.PAWN, BLACK_PAWN
    );

    private static final Map<ChessPiece.PieceType, String> WHITE_PIECE_MAP = Map.of(
        ChessPiece.PieceType.KING, WHITE_KING,
        ChessPiece.PieceType.QUEEN, WHITE_QUEEN,
        ChessPiece.PieceType.ROOK, WHITE_ROOK,
        ChessPiece.PieceType.KNIGHT, WHITE_KNIGHT,
        ChessPiece.PieceType.BISHOP, WHITE_BISHOP,
        ChessPiece.PieceType.PAWN, WHITE_PAWN
    );

    public void drawLegalMoves(ChessGame game, ChessPosition pos, boolean reversed) {
        legalMoves = new HashSet<>();
        var validMoves = game.validMoves(pos);
        for (ChessMove move : validMoves) {
            legalMoves.add(move.getEndPosition());
        }
        startPosition = pos;
        draw(game.getBoard(), reversed);
    }

    public void draw(ChessBoard board, boolean reversed) {
        drawAbc(reversed);
        int increment = reversed ? 1 : -1;
        int start = reversed ? 1  : 8;
        for (int i = start; i >= 1 && i <= 8; i += increment) {
            drawChessRow(i, board, reversed);
        }
        drawAbc(reversed);
        System.out.println(RESET_BG_COLOR + RESET_TEXT_COLOR);
        legalMoves = null;
        startPosition = null;
    }

    private void drawAbc(boolean reversed) {
        int increment = reversed ? -1 : 1;
        int start = reversed ? LETTERS.length - 1 : 0;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = start; i >= 0 && i < LETTERS.length; i += increment) {
            stringBuilder.append(LETTERS[i]);
        }
        System.out.println(SET_BG_COLOR_DARK_GREEN + SET_TEXT_COLOR_YELLOW + stringBuilder + RESET_BG_COLOR);
    }

    private void drawChessRow(int rowNum, ChessBoard board, boolean reversed) {
        String border = SET_BG_COLOR_DARK_GREEN + SET_TEXT_COLOR_YELLOW + " " + rowNum + " " + RESET_BG_COLOR;
        StringBuilder builder = new StringBuilder(border);
        int increment = reversed ? -1 : 1;
        int start = reversed ? 8  : 1;
        for (int i = start; i >= 1 && i <= 8; i += increment) {
            ChessPosition position = new ChessPosition(rowNum, i);
            if (position.equals(startPosition)) {
                builder.append(SET_BG_COLOR_YELLOW);
            }
            else if ((rowNum + i) % 2 == 1) {
                if (legalMoves != null && legalMoves.contains(position)) {
                    builder.append(SET_BG_COLOR_ALT_GREEN);
                }
                else {
                    builder.append(SET_BG_COLOR_BLUE);
                }
            }
            else {
                if (legalMoves != null && legalMoves.contains(position)) {
                    builder.append(SET_BG_COLOR_GREEN);
                }
                else {
                    builder.append(SET_BG_COLOR_LIGHT_GREY);
                }
            }
            String pieceString = getPieceString(board.getPiece(position));
            builder.append(pieceString);
        }
        builder.append(border);
        System.out.println(builder);
    }

    private String getPieceString(ChessPiece piece) {
        if (piece == null) {
            return EMPTY;
        }
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            return WHITE_PIECE_MAP.get(piece.getPieceType());
        }
        return BLACK_PIECE_MAP.get(piece.getPieceType());
    }
}
