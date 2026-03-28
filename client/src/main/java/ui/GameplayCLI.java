package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import exception.ResponseException;
import server.websocket.NotificationHandler;
import server.websocket.WebSocketCommunicator;
import util.Constants;
import websocket.commands.GameConnectionRole;
import websocket.messages.ErrorServerMessage;
import websocket.messages.LoadGameServerMessage;
import websocket.messages.NotificationServerMessage;

import java.security.InvalidParameterException;
import java.util.Scanner;

public class GameplayCLI implements NotificationHandler {

    private String authToken;
    private WebSocketCommunicator ws;
    private int gameID;
    private GameConnectionRole gameRole;
    private Scanner scan;

    private ChessGame chessGame;

    public GameplayCLI(String url, String authToken, int gameID, int port, GameConnectionRole role) throws ResponseException {
        this.authToken = authToken;
        this.ws = new WebSocketCommunicator(url, port, this);
        this.gameID = gameID;
        this.ws.connect(authToken, gameID);
        this.gameRole = role;
        scan = new Scanner(System.in);
        gameplayLoop();
        chessGame = null;
    }

    @Override
    public void handleNotification(NotificationServerMessage message) {
        System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + message.getMessage() + EscapeSequences.RESET_TEXT_COLOR);
    }

    @Override
    public void handleError(ErrorServerMessage message) {
        System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + message.getMessage() + EscapeSequences.RESET_TEXT_COLOR);
    }

    @Override
    public void handleLoadGame(LoadGameServerMessage message) {
        ClientChessBoard chessDrawer = new ClientChessBoard();
        chessGame = message.getGame();
        chessDrawer.draw(chessGame.getBoard(), gameRole == GameConnectionRole.BLACK_PLAYER);
    }

    private void gameplayLoop() throws ResponseException {
        boolean loop = true;
        while (loop) {
            String prompt = """
            1: Make Move
            2: Redraw Board
            3: Highlight Legal Moves
            4: Help
            5: Leave
            6: Resign""";
            switch (CLIUtils.getIntInput(prompt, 6, scan)) {
                case 1 -> makeMove();
                case 2 -> redrawBoard();
                case 3 -> highlightMoves();
                case 5 -> {
                    loop = false;
                    leaveGame();
                }
            }
        }
    }

    private void leaveGame() throws ResponseException {
        ws.leave(authToken, gameID);
    }

    private void redrawBoard() {
        if (chessGame == null) {
            return;
        }
        ClientChessBoard chessDrawer = new ClientChessBoard();
        chessDrawer.draw(chessGame.getBoard(), gameRole == GameConnectionRole.BLACK_PLAYER);
    }

    private void highlightMoves() {
        if (chessGame == null) {
            System.out.println("Cannot highlight moves, no chess board");
            return;
        }
        ChessPosition selectedPos;
        try {
            String posStr = CLIUtils.getStringInput("Input position: ", scan);
            selectedPos = parsePosition(posStr);
        } catch (InvalidParameterException e) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + e.getMessage() + EscapeSequences.RESET_TEXT_COLOR);
            return;
        }
        ClientChessBoard chessDrawer = new ClientChessBoard();
        chessDrawer.drawLegalMoves(chessGame, selectedPos, gameRole == GameConnectionRole.BLACK_PLAYER);
    }

    private void makeMove() throws ResponseException {
        ChessMove chessMove;
        try {
            String posStr = CLIUtils.getStringInput("Input move: ", scan);
            chessMove = parseMove(posStr);
        } catch (InvalidParameterException e) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + e.getMessage() + EscapeSequences.RESET_TEXT_COLOR);
            return;
        }
        ws.makeMove(authToken, gameID, chessMove);
    }

    private ChessMove parseMove(String moveStr) {
        if (moveStr.length() < 4 || moveStr.length() > 5) {
            throw new InvalidParameterException("Invalid move input, wrong length");
        }
        ChessPosition start = parsePosition(moveStr.substring(0, 2));
        ChessPosition end = parsePosition(moveStr.substring(2, 4));
        ChessPiece.PieceType promotion = null;
        if (moveStr.length() == 5) {
            String promotionStr = moveStr.substring(4, 5);
            if (!Constants.LETTER_PIECE_TYPE_MAP.containsKey(promotionStr)) {
                throw new InvalidParameterException("Invalid move input, not valid promotion piece");
            }
            promotion = Constants.LETTER_PIECE_TYPE_MAP.get(promotionStr);
        }
        return new ChessMove(start, end, promotion);
    }

    private ChessPosition parsePosition(String posStr) {
        if (posStr.length() != 2) {
            throw new InvalidParameterException("Invalid position input, wrong length");
        }
        String letter = posStr.substring(0, 1);
        String number = posStr.substring(1, 2);
        if (!Constants.LETTER_POSITION_MAP.containsKey(letter)) {
            throw new InvalidParameterException("Invalid position input, not valid column");
        }
        int col = Constants.LETTER_POSITION_MAP.get(letter);
        int row = 0;
        try {
            row = Integer.parseInt(number);
        } catch (NumberFormatException e) {
            throw new InvalidParameterException("Invalid position input, not valid row");
        }
        if (row < 1 || row > 8) {
            throw new InvalidParameterException("Invalid position input, not valid row");
        }
        return new ChessPosition(row, col);
    }
}
