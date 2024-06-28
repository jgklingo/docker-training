package chess.pieceMoveCalculators;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;

public class PieceMoveCalculator {
    public static ArrayList<ChessMove> diagonal(ChessBoard board, ChessPosition piecePosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        ChessPosition currentPosition;

        currentPosition = piecePosition.tr();
        while (canMove(board, currentPosition)) {
            moves.add(new ChessMove(piecePosition, currentPosition, null));
            currentPosition = currentPosition.tr();
        }
        moves.addAll(capture(board, piecePosition, currentPosition, board.getPiece(piecePosition).getTeamColor()));
        currentPosition = piecePosition.br();
        while (canMove(board, currentPosition)) {
            moves.add(new ChessMove(piecePosition, currentPosition, null));
            currentPosition = currentPosition.br();
        }
        moves.addAll(capture(board, piecePosition, currentPosition, board.getPiece(piecePosition).getTeamColor()));
        currentPosition = piecePosition.bl();
        while (canMove(board, currentPosition)) {
            moves.add(new ChessMove(piecePosition, currentPosition, null));
            currentPosition = currentPosition.bl();
        }
        moves.addAll(capture(board, piecePosition, currentPosition, board.getPiece(piecePosition).getTeamColor()));
        currentPosition = piecePosition.tl();
        while (canMove(board, currentPosition)) {
            moves.add(new ChessMove(piecePosition, currentPosition, null));
            currentPosition = currentPosition.tl();
        }
        moves.addAll(capture(board, piecePosition, currentPosition, board.getPiece(piecePosition).getTeamColor()));

        return moves;
    }
    public static ArrayList<ChessMove> vertical(ChessBoard board, ChessPosition piecePosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        ChessPosition currentPosition;

        currentPosition = piecePosition.t();
        while (canMove(board, currentPosition)) {
            moves.add(new ChessMove(piecePosition, currentPosition, null));
            currentPosition = currentPosition.t();
        }
        moves.addAll(capture(board, piecePosition, currentPosition, board.getPiece(piecePosition).getTeamColor()));
        currentPosition = piecePosition.r();
        while (canMove(board, currentPosition)) {
            moves.add(new ChessMove(piecePosition, currentPosition, null));
            currentPosition = currentPosition.r();
        }
        moves.addAll(capture(board, piecePosition, currentPosition, board.getPiece(piecePosition).getTeamColor()));
        currentPosition = piecePosition.b();
        while (canMove(board, currentPosition)) {
            moves.add(new ChessMove(piecePosition, currentPosition, null));
            currentPosition = currentPosition.b();
        }
        moves.addAll(capture(board, piecePosition, currentPosition, board.getPiece(piecePosition).getTeamColor()));
        currentPosition = piecePosition.l();
        while (canMove(board, currentPosition)) {
            moves.add(new ChessMove(piecePosition, currentPosition, null));
            currentPosition = currentPosition.l();
        }
        moves.addAll(capture(board, piecePosition, currentPosition, board.getPiece(piecePosition).getTeamColor()));

        return moves;
    }

    public static boolean canMove(ChessBoard board, ChessPosition position) {
        return position.getColumn() > 0 && position.getColumn() < 9 && position.getRow() > 0 && position.getRow() < 9 &&
                board.getPiece(position) == null;
    }
    public static ArrayList<ChessMove> capture(ChessBoard board, ChessPosition startPosition, ChessPosition endPosition,
                                               ChessGame.TeamColor color) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        if (endPosition.getColumn() > 0 && endPosition.getColumn() < 9 && endPosition.getRow() > 0 &&
                endPosition.getRow() < 9 && board.getPiece(endPosition) != null &&
                board.getPiece(endPosition).getTeamColor() != color) {
            moves.add(new ChessMove(startPosition, endPosition, null));
        }
        return moves;
    }

    public static void checkMoveList(ChessBoard board, ChessPosition position, ArrayList<ChessPosition> possiblePositions, ArrayList<ChessMove> moves) {
        for (ChessPosition p : possiblePositions) {
            if (canMove(board, p)) {
                moves.add(new ChessMove(position, p, null));
            } else {
                moves.addAll(capture(board, position, p, board.getPiece(position).getTeamColor()));
            }
        }
    }
}
