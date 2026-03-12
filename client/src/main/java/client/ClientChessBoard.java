package client;

import chess.ChessGame;

import static ui.EscapeSequences.*;

public class ClientChessBoard {
    private static final String[] letters = {
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

    public ClientChessBoard() {

    }

    public void draw(ChessGame chessGame, boolean reversed) {
        drawAbc(reversed);
    }

    private void drawAbc(boolean reversed) {
        int increment = reversed ? -1 : 1;
        int start = reversed ? letters.length - 1 : 0;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = start; i >= 0 && i < letters.length; i += increment) {
            stringBuilder.append(letters[i]);
        }
        System.out.println(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + stringBuilder);
    }
}
