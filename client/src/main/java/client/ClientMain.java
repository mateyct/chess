package client;

import chess.*;

import java.util.Scanner;

public class ClientMain {
    private Scanner scan;

    public static void main(String[] args) {
        ClientMain client = new ClientMain();
        client.loggedOutLoop();
    }

    public ClientMain() {
        scan = new Scanner(System.in);
        System.out.println("Welcome! It's time to play chess.");
    }

    private void loggedOutLoop() {
        System.out.println(
            """
                1: Login
                2: Register
                3: Help
                4: Quit"""
        );

    }

    private static void drawTestBoard() {
        ClientChessBoard board = new ClientChessBoard();
        ChessGame game = new ChessGame();
        ChessMove move = new ChessMove(
            new ChessPosition(2, 1),
            new ChessPosition(3, 1),
            null
        );
        try {
            game.makeMove(move);
        } catch (InvalidMoveException e) {
            System.out.println("That's unfortunate.");
        }
        board.draw(game.getBoard(), false);
        board.draw(game.getBoard(), true);
    }
}
