package chess.pieceMoveCalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;

public class RookMoveCalculator extends PieceMoveCalculator {
    public static ArrayList<ChessMove> moves(ChessBoard board, ChessPosition position) {
        return vertical(board, position);
    }
}
