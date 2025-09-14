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

        // rook
        if (getMoveCount() == 0) {
            ChessPiece sideQueenRook = (ChessPiece) getBoard().piece(position.getRow(), 0);
            if (sideQueenRook != null && sideQueenRook.getMoveCount() == 0){
                Position pq = new Position(position.getRow(), 3);
                Position pb = new Position(position.getRow(), 2);
                Position pn = new Position(position.getRow(), 1);
                if (!getBoard().thereIsAPiece(pq) && !getBoard().thereIsAPiece(pb) && !getBoard().thereIsAPiece(pn)) {
                    mat[position.getRow()][2] = true;
                }
            }

            ChessPiece sideKingRook = (ChessPiece) getBoard().piece(position.getRow(), 7);
            if (sideKingRook != null && sideKingRook.getMoveCount() == 0){
                Position pb = new Position(position.getRow(), 5);
                Position pn = new Position(position.getRow(), 6);
                if (!getBoard().thereIsAPiece(pb) && !getBoard().thereIsAPiece(pn)) {
                    mat[position.getRow()][6] = true;
                }
            }

        }


        return mat;


    }
}
