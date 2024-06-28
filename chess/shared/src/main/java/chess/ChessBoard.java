package chess;

import java.util.Arrays;
import java.util.Iterator;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard implements Iterable<ChessPosition> {
    ChessPiece[][] board = new ChessPiece[8][8];
    public ChessBoard() {}

    public ChessBoard(ChessBoard copyBoard) {
        // Copy constructor
        this.board = new ChessPiece[8][8];
        for (ChessPosition position : copyBoard) {
            if (copyBoard.getPiece(position) != null) {
                this.addPiece(position, new ChessPiece(copyBoard.getPiece(position)));
            }
        }
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow()-1][position.getColumn()-1] = piece;
    }

    public void removePiece(ChessPosition position) {
        board [position.getRow()-1][position.getColumn()-1] = null;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        board = new ChessPiece[8][8];
        addPiece(new ChessPosition(1,1), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(1,2), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1,3), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1,4), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(1,5), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(1,6), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1,7), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1,8), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(2,1), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(2,2), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(2,3), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(2,4), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(2,5), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(2,6), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(2,7), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(2,8), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));

        addPiece(new ChessPosition(8,1), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(8,2), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8,3), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8,4), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(8,5), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(8,6), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8,7), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8,8), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(7,1), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(7,2), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(7,3), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(7,4), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(7,5), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(7,6), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(7,7), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(7,8), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
    }

    @Override
    public Iterator<ChessPosition> iterator() {
        return new Iterator<>() {
            private int c = 0;
            private int r = 1;

            @Override
            public boolean hasNext() {
                return c < 8 || r < 8;
            }

            @Override
            public ChessPosition next() {
                if (c < 8) {
                    c += 1;
                } else {
                    c = 1;
                    r += 1;
                }
                return new ChessPosition(r, c);
            }
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Arrays.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }
}
