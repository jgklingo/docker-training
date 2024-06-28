package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private final int row;
    private final int col;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return this.row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return this.col;
    }

    public ChessPosition t() {
        return new ChessPosition(this.getRow() + 1, this.getColumn());
    }
    public ChessPosition tr() {
        return new ChessPosition(this.getRow() + 1, this.getColumn() + 1);
    }
    public ChessPosition r() {
        return new ChessPosition(this.getRow(), this.getColumn() + 1);
    }
    public ChessPosition br() {
        return new ChessPosition(this.getRow() - 1, this.getColumn() + 1);
    }
    public ChessPosition b() {
        return new ChessPosition(this.getRow() - 1, this.getColumn());
    }
    public ChessPosition bl() {
        return new ChessPosition(this.getRow() - 1, this.getColumn() - 1);
    }
    public ChessPosition l() {
        return new ChessPosition(this.getRow(), this.getColumn() - 1);
    }
    public ChessPosition tl() {
        return new ChessPosition(this.getRow() + 1, this.getColumn() - 1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPosition that = (ChessPosition) o;
        return row == that.row && col == that.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public String toString() {
        return row + "," + col;
    }
}
