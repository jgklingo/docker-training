package chess.pieceMoveCalculators;

import chess.*;

import java.util.ArrayList;

public class PawnMoveCalculator extends PieceMoveCalculator {
    public static ArrayList<ChessMove> moves(ChessBoard board, ChessPosition position) {
        ArrayList<ChessMove> moves = new ArrayList<>();

        switch (board.getPiece(position).getTeamColor()) {
            case BLACK -> {
                if (canMove(board, position.b())) {
                    moves.add(new ChessMove(position, position.b(), null));
                    if (position.getRow() == 7 && canMove(board, position.b().b())) {
                        moves.add(new ChessMove(position, position.b().b(), null));
                    }
                }
                moves.addAll(capture(board, position, position.b().l(), ChessGame.TeamColor.BLACK));
                moves.addAll(capture(board, position, position.b().r(), ChessGame.TeamColor.BLACK));
            }
            case WHITE -> {
                if (canMove(board, position.t())) {
                    moves.add(new ChessMove(position, position.t(), null));
                    if (position.getRow() == 2 && canMove(board, position.t().t())) {
                        moves.add(new ChessMove(position, position.t().t(), null));
                    }
                }
                moves.addAll(capture(board, position, position.t().l(), ChessGame.TeamColor.WHITE));
                moves.addAll(capture(board, position, position.t().r(), ChessGame.TeamColor.WHITE));
            }
        }

        ArrayList<ChessMove> promotionMoves = new ArrayList<>();
        for (ChessMove m : moves) {
            if ((board.getPiece(m.getStartPosition()).getTeamColor() == ChessGame.TeamColor.BLACK
                    && m.getEndPosition().getRow() == 1)
                    || (board.getPiece(m.getStartPosition()).getTeamColor() == ChessGame.TeamColor.WHITE
                    && m.getEndPosition().getRow() == 8)) {
                promotionMoves.add(new ChessMove(m.getStartPosition(), m.getEndPosition(), ChessPiece.PieceType.QUEEN));
                promotionMoves.add(new ChessMove(m.getStartPosition(), m.getEndPosition(), ChessPiece.PieceType.BISHOP));
                promotionMoves.add(new ChessMove(m.getStartPosition(), m.getEndPosition(), ChessPiece.PieceType.ROOK));
                promotionMoves.add(new ChessMove(m.getStartPosition(), m.getEndPosition(), ChessPiece.PieceType.KNIGHT));
            }
        }
        moves.removeIf(chessMove ->
                board.getPiece(chessMove.getStartPosition()).getTeamColor() == ChessGame.TeamColor.BLACK
                        && chessMove.getEndPosition().getRow() == 1
                        && chessMove.getPromotionPiece() == null);
        moves.removeIf(chessMove ->
                board.getPiece(chessMove.getStartPosition()).getTeamColor() == ChessGame.TeamColor.WHITE
                        && chessMove.getEndPosition().getRow() == 8
                        && chessMove.getPromotionPiece() == null);
        moves.addAll(promotionMoves);

        return moves;
    }
}
