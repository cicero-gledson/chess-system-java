package chess.pieces;

import boardgame.Board;
import boardgame.Piece;
import chess.ChessPiece;
import chess.Color;

public class Queen extends ChessPiece {

    public Queen(Board board, Color color) {
        super(board, color);
    }

    @Override
    public boolean[][] possibleMoves() {
        boolean[][] mat1 = (new Rook(getBoard(), getColor())).possibleMoves(position);
        boolean[][] mat2 = (new Bishop(getBoard(), getColor())).possibleMoves(position);
        boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];
        for (int i = 0; i < getBoard().getRows(); i++){
            for (int j = 0; j< getBoard().getColumns(); j++)
                mat[i][j] = mat1[i][j] || mat2[i][j];
        }
        return mat;
    }
    @Override
    public String toString() {
        return "Q";
    }
}
