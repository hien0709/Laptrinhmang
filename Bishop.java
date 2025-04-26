/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MangMayTinh.Chess.Model;

import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 *
 * @author thinhle
 */
public class Bishop extends Piece {

    static String className = "bishop";

    public Bishop(Point nowPosition, Image image, boolean isBelongToFirstPlayer, Chessboard chessboard) {
        super(nowPosition, image, isBelongToFirstPlayer, chessboard);
    }

    @Override
    public void generatePossibleDestination() {
        possibleDestinations.clear();
        int x = this.getNowPosition().x;
        int y = this.getNowPosition().y;
        //all possible moves in the down positive diagonal
        for (int i = x + 1, j = y + 1; j < 8 && i < 8; i++, j++) {
            Point destination = new Point(i, j);
            Piece piece = this.chessboard.getPieceAt(destination);
            if (piece == null || (piece.isBelongToFirstPlayer() != this.isBelongToFirstPlayer())) {
                possibleDestinations.add(destination);
            }
            if (piece != null) {
                break;
            }
        }
        //all possible moves in the up positive diagonal
        for (int i = x - 1, j = y + 1; j < 8 && i > -1; i--, j++) {
            Point destination = new Point(i, j);
            Piece piece = this.chessboard.getPieceAt(destination);
            if (piece == null || (piece.isBelongToFirstPlayer() != this.isBelongToFirstPlayer())) {
                possibleDestinations.add(destination);
            }
            if (piece != null) {
                break;
            }
        }
        //all possible moves in the up negative diagonal
        for (int i = x - 1, j = y - 1; j > -1 && i > -1; j--, i--) {
            Point destination = new Point(i, j);
            Piece piece = this.chessboard.getPieceAt(destination);
            if (piece == null || (piece.isBelongToFirstPlayer() != this.isBelongToFirstPlayer())) {
                possibleDestinations.add(destination);
            }
            if (piece != null) {
                break;
            }
        }
        //all possible moves in the down negative diagonal
        for (int i = x + 1, j = y - 1; i < 8 && j > -1; i++, j--) {
            Point destination = new Point(i, j);
            Piece piece = this.chessboard.getPieceAt(destination);
            if (piece == null || (piece.isBelongToFirstPlayer() != this.isBelongToFirstPlayer())) {
                possibleDestinations.add(destination);
            }
            if (piece != null) {
                break;
            }
        }
    }
}
