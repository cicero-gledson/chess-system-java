package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

import java.util.Scanner;
import java.util.Timer;

public class King extends ChessPiece {

    public King(Board board, Color color) {
        super(board, color);
    }

    @Override
    public String toString() {
        return "K";
    }

    private boolean canMove(Position position){
        ChessPiece p = (ChessPiece) getBoard().piece(position);
        return p == null || p.getColor() != getColor();
    }

    @Override
    public boolean[][] possibleMoves() {
        boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];
        Position p = new Position(position.getRow(), position.getColumn());

        // esquerda superior
        p.setRow(p.getRow()-1);
        p.setColumn(p.getColumn()-1);
        if (getBoard().positionExists(p)) {mat[p.getRow()][p.getColumn()] = canMove(p);}

        // acima
        p.setColumn(p.getColumn()+1);
        if (getBoard().positionExists(p)) {mat[p.getRow()][p.getColumn()] = canMove(p);}

        // direita superior
        p.setColumn(p.getColumn()+1);
        if (getBoard().positionExists(p)) {mat[p.getRow()][p.getColumn()] = canMove(p);}

        // esquerda
        p.setColumn(p.getColumn()-2);
        p.setRow(p.getRow()+1);
        if (getBoard().positionExists(p)) {mat[p.getRow()][p.getColumn()] = canMove(p);}

        // direita
        p.setColumn(p.getColumn()+2);
        if (getBoard().positionExists(p)) {mat[p.getRow()][p.getColumn()] = canMove(p);}

        // esquerda inferior
        p.setColumn(p.getColumn()-2);
        p.setRow(p.getRow()+1);
        if (getBoard().positionExists(p)) {mat[p.getRow()][p.getColumn()] = canMove(p);}

        // a baixo
        p.setColumn(p.getColumn()+1);
        if (getBoard().positionExists(p)) {mat[p.getRow()][p.getColumn()] = canMove(p);}

        // direita inferior
        p.setColumn(p.getColumn()+1);
        if (getBoard().positionExists(p)) {mat[p.getRow()][p.getColumn()] = canMove(p);}

        return mat;


    }
}
