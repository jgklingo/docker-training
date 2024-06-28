package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    ChessBoard board = new ChessBoard();
    TeamColor currentTurn = TeamColor.WHITE;
    public boolean gameOver = false;
    public ChessGame() {
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.currentTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if (gameOver || getBoard().getPiece(startPosition).getTeamColor() != currentTurn) {
            return Collections.emptyList();
        }
        ChessPiece piece = board.getPiece(startPosition);
        Collection<ChessMove> pieceCalculatorMoves;
        if (piece == null) {
            return null;
        } else {
            pieceCalculatorMoves = ChessPiece.pieceMoves(board, startPosition);
        }
        pieceCalculatorMoves.removeIf(move -> !isValidMove(move));
        return pieceCalculatorMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public String makeMove(ChessMove move) throws InvalidMoveException {
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        if (this.currentTurn == board.getPiece(move.getStartPosition()).getTeamColor()
                && validMoves.contains(move)) {
            this.board = testMove(move);

            if (isInCheckmate(TeamColor.WHITE)) {
                gameOver = true;
                return "WHITE is in checkmate!";
            }
            else if (isInCheckmate(TeamColor.BLACK)) {
                gameOver = true;
                return "BLACK is in checkmate!";
            }
            else if (isInStalemate(TeamColor.WHITE)) {
                gameOver = true;
                return "WHITE is in stalemate!";
            } else if (isInStalemate(TeamColor.BLACK)) {
                gameOver = true;
                return "BLACK is in stalemate!";
            }

            if (currentTurn == TeamColor.BLACK) {
                this.currentTurn = TeamColor.WHITE;
            } else {
                this.currentTurn = TeamColor.BLACK;
            }
            return null;

        } else {
            throw new InvalidMoveException();
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        for (ChessPosition position : board) {
            if (board.getPiece(position) != null && board.getPiece(position).getTeamColor() != teamColor) {
                for (ChessMove move : ChessPiece.pieceMoves(board, position)) {
                    if (board.getPiece(move.getEndPosition()) != null &&
                            board.getPiece(move.getEndPosition()).getPieceType() == ChessPiece.PieceType.KING
                            && board.getPiece(move.getEndPosition()).getTeamColor() == teamColor) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isValidMove(ChessMove move) {
        TeamColor color = board.getPiece(move.getStartPosition()).getTeamColor();
        ChessBoard originalBoard = new ChessBoard(this.board);
        this.board = testMove(move);
        boolean returnValue = !isInCheck(color);
        this.board = originalBoard;
        return returnValue;
    }

    private ChessBoard testMove(ChessMove move) {
        ChessBoard board = new ChessBoard(this.board);
        ChessPiece piece = board.getPiece(move.getStartPosition());
        board.removePiece(move.getStartPosition());
        if (move.getPromotionPiece() == null) {
            board.addPiece(move.getEndPosition(), piece);
        } else {
            board.addPiece(move.getEndPosition(), new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
        }
        return board;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor) && allMovesForColor(teamColor).isEmpty();
    }

    private Collection<ChessMove> allMovesForColor(TeamColor teamColor) {
        Collection<ChessMove> allMoves = new ArrayList<>();
        for (ChessPosition position : board) {
            if (board.getPiece(position) != null && board.getPiece(position).getTeamColor() == teamColor) {
                allMoves.addAll(validMoves(position));
            }
        }
        return allMoves;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return this.getTeamTurn() == teamColor && allMovesForColor(teamColor).isEmpty();
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }
}
