package chess.pieceMoveCalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;

public class KingMoveCalculator extends PieceMoveCalculator {
    public static ArrayList<ChessMove> moves(ChessBoard board, ChessPosition position) {
        ArrayList<ChessMove> moves = new ArrayList<>();

        ArrayList<ChessPosition> possiblePositions = new ArrayList<>();
        possiblePositions.add(position.t());
        possiblePositions.add(position.tr());
        possiblePositions.add(position.r());
        possiblePositions.add(position.br());
        possiblePositions.add(position.b());
        possiblePositions.add(position.bl());
        possiblePositions.add(position.l());
        possiblePositions.add(position.tl());

        checkMoveList(board, position, possiblePositions, moves);

        return moves;
    }
}
