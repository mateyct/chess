package client;

import chess.*;

public class ClientMain {
    public static void main(String[] args) throws InvalidMoveException {
        ClientChessBoard board = new ClientChessBoard();
        ChessGame game = new ChessGame();
        ChessMove move = new ChessMove(
            new ChessPosition(2, 1),
            new ChessPosition(3, 1),
            null
        );
        game.makeMove(move);
        board.draw(game.getBoard(), false);
        board.draw(game.getBoard(), true);
    }
}
