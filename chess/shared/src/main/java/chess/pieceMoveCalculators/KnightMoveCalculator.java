package chess.pieceMoveCalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;

public class KnightMoveCalculator extends PieceMoveCalculator {
    public static ArrayList<ChessMove> moves(ChessBoard board, ChessPosition position) {
        ArrayList<ChessMove> moves = new ArrayList<>();

        ArrayList<ChessPosition> possiblePositions = new ArrayList<>();
        possiblePositions.add(position.t().t().l());
        possiblePositions.add(position.t().t().r());
        possiblePositions.add(position.r().r().t());
        possiblePositions.add(position.r().r().b());
        possiblePositions.add(position.b().b().r());
        possiblePositions.add(position.b().b().l());
        possiblePositions.add(position.l().l().b());
        possiblePositions.add(position.l().l().t());

        checkMoveList(board, position, possiblePositions, moves);

        return moves;
    }
}
