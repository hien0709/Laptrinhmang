package LapTrinhMang.Chess.Connection.Server;

import LapTrinhMang.Chess.Model.Chessboard;
import LapTrinhMang.Chess.Model.Enum.MessageType;
import LapTrinhMang.Chess.Model.Interface.PlayerInterface;
import LapTrinhMang.Chess.Model.Move;
import java.awt.Point;

public class Match implements Runnable, PlayerInterface {

    Player firstPlayer;
    Player secondPlayer;
    Chessboard chessboard;
    private int turn = 1; // 1: first player's turn ----- 2: second player's turn
    private boolean isRunning = true;

    public Match(Player firstPlayer, Player sencondPlayer) {
        this.firstPlayer = firstPlayer;
        this.secondPlayer = sencondPlayer;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("Destructed 1 match");
    }

    @Override
    public void run() {
        this.firstPlayer.setDelegate(this);
        this.secondPlayer.setDelegate(this);
        this.firstPlayer.setIsFirstPlayer(true);
        this.chessboard = new Chessboard();
        this.chessboard.setIsFirstPlayer(true);
        this.chessboard.drawChessboard();
        this.chessboard.setVisible(true);
        this.firstPlayer.start();
        this.secondPlayer.start();
        this.firstPlayer.sendMessage(MessageType.IS_FIRST_PLAYER, true);
        this.secondPlayer.sendMessage(MessageType.IS_FIRST_PLAYER, false);
        while (this.isRunning) {
        }
    }

    private void transform(Move move) {
        //System.out.println("Before trans: " + move.getSource().x + " " + move.getSource().y + " to : " + move.getDestination().x + " " + move.getDestination().y);
        Point destination = move.getDestination();
        Point source = move.getSource();
        destination.y = 7 - destination.y;
        destination.x = 7 - destination.x;
        source.y = 7 - source.y;
        source.x = 7 - source.x;
        //System.out.println("After trans: " + move.getSource().x + " " + move.getSource().y + " to : " + move.getDestination().x + " " + move.getDestination().y);
    }
    
    @Override
    public synchronized void setName(String name, boolean isFirstPlayer) {
        if (isFirstPlayer) {
            this.secondPlayer.sendMessage(MessageType.NAME, name);
            this.firstPlayer.setIsReady(true);
        } else {
            this.firstPlayer.sendMessage(MessageType.NAME, name);
            this.secondPlayer.setIsReady(true);
        }
        if (this.firstPlayer.isReady() && this.secondPlayer.isReady()) {
            this.firstPlayer.sendOperation(MessageType.TURN);
            this.firstPlayer.sendOperation(MessageType.START_GAME);
            this.secondPlayer.sendOperation(MessageType.START_GAME);
        }
    }

    @Override
    public synchronized void didReceiveMessage(String message, boolean isFirstPlayer) {
        if (isFirstPlayer) {
            this.secondPlayer.sendMessage(MessageType.MESSAGE, message);
        } else {
            this.firstPlayer.sendMessage(MessageType.MESSAGE, message);
        }
    }

    @Override
    public synchronized void move(Move move, boolean isFirstPlayer) {
        if (isFirstPlayer && turn == 1) {
            this.chessboard.move(move);
            this.transform(move);
            this.secondPlayer.sendMessage(MessageType.MOVE, move);
            turn = 2;
        } else if (turn == 2) {
            this.transform(move);
            this.chessboard.move(move);
            this.firstPlayer.sendMessage(MessageType.MOVE, move);
            turn = 1;
        }
        int checkWinner = this.chessboard.getWinner();
        if (checkWinner == 1) {
            this.firstPlayer.sendMessage(MessageType.RESULT, true);
            this.secondPlayer.sendMessage(MessageType.RESULT, false);
        } else if (checkWinner == 2) {
            this.firstPlayer.sendMessage(MessageType.RESULT, false);
            this.secondPlayer.sendMessage(MessageType.RESULT, true);
        }
    }

    @Override
    public void surrender(boolean isFirstPlayer) {
        if (isFirstPlayer) {
            this.secondPlayer.sendMessage(MessageType.RESULT, true);
            this.firstPlayer.sendMessage(MessageType.RESULT, false);
        } else {
            this.firstPlayer.sendMessage(MessageType.RESULT, true);
            this.secondPlayer.sendMessage(MessageType.RESULT, false);
        }
    }

    @Override
    public void endGame(boolean isFirstPlayer) {
        if (isFirstPlayer) {
            this.firstPlayer.setIsReady(false);
        } else {
            this.secondPlayer.setIsReady(false);
        }
        if (!this.firstPlayer.isReady() && !this.secondPlayer.isReady()) {
            this.isRunning = false;
        }
    }
}
