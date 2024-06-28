package chess.pieceMoveCalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;

public class QueenMoveCalculator extends PieceMoveCalculator {
    public static ArrayList<ChessMove> moves(ChessBoard board, ChessPosition position) {
        ArrayList<ChessMove> moves = vertical(board, position);
        moves.addAll(diagonal(board, position));
        return moves;
    }

}
