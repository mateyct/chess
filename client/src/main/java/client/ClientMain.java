package client;

import chess.*;

public class ClientMain {
    public static void main(String[] args) {
        ClientChessBoard board = new ClientChessBoard();
        board.draw(new ChessGame(), false);
    }
}
