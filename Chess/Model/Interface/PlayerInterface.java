/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LapTrinhMang.Chess.Model.Interface;

import LapTrinhMang.Chess.Model.Move;

/**
 *
 * @author thinhle
 */
public interface PlayerInterface {
    void setName(String name, boolean isFirstPlayer);
    void didReceiveMessage(String message, boolean isFirstPlayer);
    void move(Move move, boolean isFirstPlayer);
    void surrender(boolean isFirstPlayer);
    void endGame(boolean isFirstPlayer);
}
