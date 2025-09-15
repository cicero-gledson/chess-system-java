package chess;

import application.Program;
import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.*;

import javax.xml.transform.Source;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.util.stream.Collectors.toList;

public class ChessMatch {
    private int turn;
    private Color currentPlayer;
    private Board board;
    private List<Piece> piecesOnTheBoard = new ArrayList<>();
    private List<Piece> capturedPieces = new ArrayList<>();
    private boolean check;
    private boolean checkMate;
    private ChessPiece enPassantVulnerable;

    public ChessMatch() {
        board = new Board(8, 8);
        turn = 1;
        currentPlayer = Color.WHITE;
        initialSetup();
    }

    public int getTurn(){
       return turn;
    }

    public ChessPiece getEnPassantVulnerable() {
        return enPassantVulnerable;
    }

    public Color getCurrentPlayer(){
        return currentPlayer;
    }

    public boolean getCheck(){
        return check;
    }

    public boolean getCheckMate(){
        return checkMate;
    }

    private void nextTurn(){
        turn++;
        currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

    private Color opponent (Color color){
        return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

    private ChessPiece king (Color cor){
        ChessPiece piece = null;
        for (int i=0; i < board.getRows(); i++){
            for (int j=0; j< board.getColumns(); j++){
                piece = (ChessPiece) board.piece(i, j);
                if (piece instanceof King && piece.getColor() == cor)
                    return piece;
            }
        }
        return piece;
    }
    private boolean testCheck(Color color, Position kingPosition) {
        List<Piece> opponentPieces = piecesOnTheBoard.stream()
                .filter(x -> ((ChessPiece)x).getColor() == opponent (color)).toList();

        for (Piece p: opponentPieces) {
            if (p.possibleMoves()[kingPosition.getRow()][kingPosition.getColumn()])
                return true;
        }
        return false;
    }
    private boolean testCheck(Color color){
        Position kingPosition = king(color).getChessPosition().toPosition();
        return testCheck(color, kingPosition);
    }

    private boolean testCheckMate(Color color){
        if (!check) return false;
        List<Piece> pieces = piecesOnTheBoard.stream()
                .filter( x -> ((ChessPiece) x ).getColor() == color).toList();
        for (Piece piece: pieces){
            boolean[][] pos = piece.possibleMoves();
            for (int i=0; i< board.getRows(); i++){
                for (int j=0; j< board.getColumns(); j++){
                    if (pos[i][j]){
                        Position source = ((ChessPiece)piece).getChessPosition().toPosition();
                        Position target = new Position(i,j);
                        Piece capturedPiece = makeMove(source, target);
                        boolean testCheckMate = testCheck(color);
                        undoMove(source, target, capturedPiece, null);
                        if (!testCheckMate) return false;
                    }
                }
            }
        }
        return true;

    }

    public ChessPiece[][] getPieces(){
        ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
        for (int i=0; i<board.getRows(); i++){
            for (int j=0; j<board.getColumns(); j++){
                mat[i][j] = (ChessPiece) board.piece(i, j);
            }
        }
        return mat;
    }
    private  void placeNewPiece(char column, int row, ChessPiece piece){
        board.placePiece(piece, new ChessPosition(column, row).toPosition());
        piecesOnTheBoard.add(piece);
    }
    public boolean[][] possibleMoves(ChessPosition sourcePosition){
        Position position = sourcePosition.toPosition();
        validateSourcePosition(position);
        return board.piece(position).possibleMoves();
    }

    public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition){
        Position source = sourcePosition.toPosition();
        Position target = targetPosition.toPosition();
        ChessPiece chessPieceMove = (ChessPiece) board.piece(source);
        validateSourcePosition(source);
        validateTargetPosition(source, target);
        Position positionSourceRook = null;
        Position positionTargetRook = null;

        if (chessPieceMove instanceof King){
            int deslocamento = source.getColumn() - target.getColumn();
            if (Math.abs(deslocamento)  == 2) {
                if (testCheck(currentPlayer))
                    throw new ChessException("You can't castle when your king is in check");

                int directionKing = -1; // left
                if (deslocamento < 0)
                    directionKing = 1;  // right

                Position kingMoveSquare1 = new Position(source.getRow(), source.getColumn() + directionKing);
                if (testCheck(currentPlayer, kingMoveSquare1))
                    throw new ChessException("You can't castle through check");

                positionSourceRook = new Position(source.getRow(), directionKing==1 ? 7 : 0);
                positionTargetRook = new Position(source.getRow(), source.getColumn()+directionKing);
                makeMove(positionSourceRook, positionTargetRook);
            }
        }

        Piece capturedPiece = makeMove(source, target);

        // en passant
        Position posEnPassantCaptured = null;
        if (chessPieceMove instanceof Pawn && enPassantVulnerable != null) {
               if (chessPieceMove.getChessPosition().getColumn() == enPassantVulnerable.getChessPosition().getColumn()
                    && capturedPiece == null
                    && source.getColumn() != target.getColumn()) {

               posEnPassantCaptured = new Position(
                       enPassantVulnerable.getChessPosition().toPosition().getRow(),
                       enPassantVulnerable.getChessPosition().toPosition().getColumn());
                capturedPiece = (ChessPiece) board.removePiece(enPassantVulnerable.getChessPosition().toPosition());
                capturedPieces.add(capturedPiece);
                piecesOnTheBoard.remove(capturedPiece);
            }
        }



        if (testCheck(currentPlayer)) {
            undoMove(source, target, capturedPiece, posEnPassantCaptured);
            if (positionSourceRook != null)
                undoMove(positionSourceRook, positionTargetRook, null, null);
            throw new ChessException("You can´t put yourself in check");
        }

        check = testCheck(opponent(currentPlayer));
        checkMate = testCheckMate(opponent(currentPlayer));

        if (!checkMate) nextTurn();

        if (chessPieceMove instanceof Pawn) {
            int deslocamento = source.getRow() - target.getRow();
            if (Math.abs(deslocamento) == 2)
                enPassantVulnerable = chessPieceMove;
            else
                enPassantVulnerable = null;
        }



        return (ChessPiece) capturedPiece;
    }

    private void validateTargetPosition(Position source, Position target) {
        if (!board.piece(source).possibleMove(target)) {
            throw new ChessException("The chosen piece can´t move to target position");
        }
    }

    private Piece makeMove(Position source, Position target) {
        ChessPiece p = (ChessPiece) board.removePiece(source);
        Piece capturedPiece = board.removePiece(target);
        board.placePiece(p, target);

        if (capturedPiece != null) {
            capturedPieces.add(capturedPiece);
            piecesOnTheBoard.remove(capturedPiece);
        }

        p.increaseMoveCount();
        return capturedPiece;
    }

    private void undoMove(Position source, Position target, Piece capturedPiece, Position posEnPassantCaptured){
        ChessPiece p = (ChessPiece) board.removePiece(target);
        board.placePiece(p, source);
        p.decreaseMoveCount();
        if (capturedPiece != null) {
            if (posEnPassantCaptured != null)
                board.placePiece(capturedPiece, posEnPassantCaptured);
            else
                board.placePiece(capturedPiece, target);

            capturedPieces.remove(capturedPiece);
            piecesOnTheBoard.add(capturedPiece);
        }
    }

    private void validateSourcePosition(Position source) {
        if (!board.thereIsAPiece(source)){
            throw new ChessException("There is not piece on source position");
        }

        if (currentPlayer != ((ChessPiece) board.piece(source)).getColor()){
            throw new ChessException("The chosen chess piece is not yours");
        }

        if (!board.piece(source).isThereAnyPossibleMove()) {
            throw new ChessException("There is not possible moves for the chosen piece");
        }
    }

    private void initialSetup(){


        placeNewPiece('a', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('b', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('c', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('d', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('e', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('f', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('g', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('h', 7, new Pawn(board, Color.BLACK, this));

        placeNewPiece('a', 8, new Rook(board, Color.BLACK));
        placeNewPiece('b', 8, new Knight(board, Color.BLACK));
        placeNewPiece('c', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('d', 8, new Queen(board, Color.BLACK));
        placeNewPiece('e', 8, new King(board, Color.BLACK));
        placeNewPiece('f', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('g', 8, new Knight(board, Color.BLACK));
        placeNewPiece('h', 8, new Rook(board, Color.BLACK));

        // ---------------------------

        placeNewPiece('a', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('b', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('c', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('d', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('e', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('f', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('g', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('h', 2, new Pawn(board, Color.WHITE, this));

        placeNewPiece('a', 1, new Rook(board, Color.WHITE));
        placeNewPiece('b', 1, new Knight(board, Color.WHITE));
        placeNewPiece('c', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('d', 1, new Queen(board, Color.WHITE));
        placeNewPiece('e', 1, new King(board, Color.WHITE));
        placeNewPiece('f', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('g', 1, new Knight(board, Color.WHITE));
        placeNewPiece('h', 1, new Rook(board, Color.WHITE));


    }
}
