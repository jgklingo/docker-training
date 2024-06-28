package chess;

import chess.pieceMoveCalculators.*;

import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    public ChessPiece(ChessPiece copyPiece) {
        // Copy constructor
        pieceColor = copyPiece.getTeamColor();
        type = copyPiece.getPieceType();
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        switch (board.getPiece(myPosition).getPieceType()) {
            case BISHOP -> {
                return BishopMoveCalculator.moves(board, myPosition);
            }
            case ROOK -> {
                return RookMoveCalculator.moves(board, myPosition);
            }
            case QUEEN -> {
                return QueenMoveCalculator.moves(board, myPosition);
            }
            case KING -> {
                return KingMoveCalculator.moves(board, myPosition);
            }
            case KNIGHT -> {
                return KnightMoveCalculator.moves(board, myPosition);
            }
            case PAWN -> {
                return PawnMoveCalculator.moves(board, myPosition);
            }
        }
        throw new RuntimeException("Piece not implemented!");
    }

    @Override
    public String toString() {
        String str = "";
        switch (this.getPieceType()) {
            case BISHOP -> str = "b";
            case ROOK -> str = "r";
            case QUEEN -> str = "q";
            case KING -> str = "k";
            case KNIGHT -> str = "n";
            case PAWN -> str = "p";
        }
        if (this.getTeamColor() == ChessGame.TeamColor.BLACK) {
            return str;
        } else {
            return str.toUpperCase();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
