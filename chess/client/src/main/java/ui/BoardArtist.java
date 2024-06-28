package ui;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static ui.EscapeSequences.*;

public class BoardArtist {
    private boolean white = true;
    private final ChessBoard chessBoard;
    private final List<ChessPosition> reversedPositions;
    private final StringBuilder header = new StringBuilder().append(SET_BG_COLOR_LIGHT_GREY)
            .append(SET_TEXT_COLOR_BLACK).append("    h  g  f  e  d  c  b  a    ").append(RESET_BG_COLOR).append("\n");
    private final StringBuilder reverseHeader = new StringBuilder().append(SET_BG_COLOR_LIGHT_GREY)
            .append(SET_TEXT_COLOR_BLACK).append("    a  b  c  d  e  f  g  h    ").append(RESET_BG_COLOR).append("\n");

    public BoardArtist(ChessBoard chessBoard) {
        this.chessBoard = chessBoard;
        this.reversedPositions = reversePositions(chessBoard);
    }
    private List<ChessPosition> reversePositions(ChessBoard chessBoard) {
        ArrayList<ChessPosition> positions = new ArrayList<>();
        for (ChessPosition position : chessBoard) {
            positions.add(position);
        }
        return positions.reversed();
    }

    public String drawBoard() {
        StringBuilder string = new StringBuilder();
        int squareNumber = 0;
        string.append(header);
        string.append(startRow(8));
        for (ChessPosition position : chessBoard) {
            string.append(drawSquare(position));
            if (++squareNumber % 8 == 0) {
                string.append(startRow(squareNumber));
                string.append(RESET_BG_COLOR).append("\n");
                white = !white;
                string.append(startRow(squareNumber + 8));
            }
        }
        string.append(header);
        return string.toString();
    }
    public String drawReverseBoard() {
        StringBuilder string = new StringBuilder();
        int squareNumber = 64;
        string.append(reverseHeader);
        string.append(startRow(64));
        for (ChessPosition position : reversedPositions) {
            string.append(drawSquare(position));
            if (--squareNumber % 8 == 0) {
                string.append(startRow(squareNumber + 8));
                string.append(RESET_BG_COLOR).append("\n");
                white = !white;
                string.append(startRow(squareNumber));
            }
        }
        string.append(reverseHeader);
        return string.toString();
    }
    public String showMoves(Collection<ChessMove> moves) {
        ArrayList<ChessPosition> markedPositions = new ArrayList<>();
        for (ChessMove move : moves) {
            markedPositions.add(move.getEndPosition());
        }

        StringBuilder string = new StringBuilder();
        int squareNumber = 0;
        string.append(header);
        string.append(startRow(8));
        for (ChessPosition position : chessBoard) {
            if (markedPositions.contains(position)) {
                string.append(drawMarkedSquare(position));
            } else {
                string.append(drawSquare(position));
            }
            if (++squareNumber % 8 == 0) {
                string.append(startRow(squareNumber));
                string.append(RESET_BG_COLOR).append("\n");
                white = !white;
                string.append(startRow(squareNumber + 8));
            }
        }
        string.append(header);
        return string.toString();
    }
    public String showMovesReverse(Collection<ChessMove> moves) {
        ArrayList<ChessPosition> markedPositions = new ArrayList<>();
        for (ChessMove move : moves) {
            markedPositions.add(move.getEndPosition());
        }

        StringBuilder string = new StringBuilder();
        int squareNumber = 64;
        string.append(reverseHeader);
        string.append(startRow(64));
        for (ChessPosition position : reversedPositions) {
            if (markedPositions.contains(position)) {
                string.append(drawMarkedSquare(position));
            } else {
                string.append(drawSquare(position));
            }
            if (--squareNumber % 8 == 0) {
                string.append(startRow(squareNumber + 8));
                string.append(RESET_BG_COLOR).append("\n");
                white = !white;
                string.append(startRow(squareNumber));
            }
        }
        string.append(reverseHeader);
        return string.toString();
    }
    private StringBuilder drawSquare(ChessPosition position) {
        StringBuilder string = new StringBuilder();
        if (this.white) {
            string.append(SET_BG_COLOR_WHITE).append(SET_TEXT_COLOR_BLACK);
        } else {
            string.append(SET_BG_COLOR_BLACK).append(SET_TEXT_COLOR_WHITE);
        }
        string.append(" ");
        if (chessBoard.getPiece(position) != null) {
            string.append(chessBoard.getPiece(position));
        } else {
            string.append(" ");
        }
        string.append(" ");
        this.white = !this.white;
        return string;
    }
    private StringBuilder drawMarkedSquare(ChessPosition position) {
        StringBuilder string = new StringBuilder();
        if (this.white) {
            string.append(SET_BG_COLOR_GREEN).append(SET_TEXT_COLOR_BLACK);
        } else {
            string.append(SET_BG_COLOR_DARK_GREEN).append(SET_TEXT_COLOR_WHITE);
        }
        string.append(" ");
        if (chessBoard.getPiece(position) != null) {
            string.append(chessBoard.getPiece(position));
        } else {
            string.append(" ");
        }
        string.append(" ");
        this.white = !this.white;
        return string;
    }
    private StringBuilder startRow(int squareNumber) {
        int rowNumber = squareNumber / 8;
        StringBuilder string = new StringBuilder();
        if (rowNumber > 8 || rowNumber < 1) {
            return string;
        }
        string.append(SET_BG_COLOR_LIGHT_GREY).append(SET_TEXT_COLOR_BLACK);
        string.append(" ").append(rowNumber).append(" ");
        return string;
    }
}
