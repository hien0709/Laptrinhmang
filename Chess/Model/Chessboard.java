/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LapTrinhMang.Chess.Model;

import LapTrinhMang.Chess.Model.Interface.ChessboardInterface;
import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;

/**
 *
 * @author thinhle
 */
public class Chessboard extends javax.swing.JFrame {

    private static final String extension = ".PNG";
    private static final int imageRate = 6;
    ArrayList<Piece> firstPlayerPieces = new ArrayList<>();
    ArrayList<Piece> secondPlayerPieces = new ArrayList<>();
    ArrayList<Piece> deadPieces = new ArrayList<>();
    HashMap<String, Square> squares = new HashMap<>();
    ChessboardInterface delegate;
    private Piece firstPiece = null;
    private int chessWidth = 0;
    private final Color dark = new Color(118, 150, 86);
    private final Color light = new Color(238, 238, 210);
    private final Color darkSelected = new Color(187, 203, 74);
    private final Color lightSelected = new Color(246, 246, 141);
    private Move currentMove = null;
    private boolean isFirstPlayer = true;
    private String firstPlayerName = "";
    private String secondPlayerName = "";

    /**
     * Creates new form Chessboard
     */
    public Chessboard() {
        initComponents();
    }

    public void move(Move move) {
        System.out.println("Move from: " + move.source.x + " " + move.source.y + " to : " + move.destination.x + " " + move.destination.y);
        Piece sourcePiece = this.getPieceAt(move.source);
        Piece destinationPiece = this.getPieceAt(move.destination);
        if (sourcePiece == null) {
            System.out.println("there is no piece at " + move.source);
            return;
        }
        if (destinationPiece != null) {
            System.out.println("Kill an enemy!");
            destinationPiece.setIsAlive(false);
            this.playArea.remove(destinationPiece);
            firstPlayerPieces.remove(destinationPiece);
            secondPlayerPieces.remove(destinationPiece);
            this.deadPieces.add(destinationPiece);
        }
        int x = move.getDestination().x;
        int y = move.getDestination().y;
        String sourceKey = Square.getKey(move.source);
        String destinationKey = Square.getKey(move.destination);
        this.squares.get(sourceKey).turnToSecondaryColor();
        this.squares.get(destinationKey).turnToSecondaryColor();
        sourcePiece.setLocation(x * this.chessWidth, y * this.chessWidth);
        Point nowPosition = (Point) move.destination.clone();
        sourcePiece.setNowPosition(nowPosition);
        this.currentMove = move;
        this.addMoveHistory();
        this.turnToOriginalColor();
    }

    public int getWinner() {
        for (Piece piece : this.deadPieces) {
            if (piece instanceof King) {
                if (piece.isBelongToFirstPlayer()) {
                    return 1;
                } else {
                    return 2;
                }
            }
        }
        return 0; // 1: first player win -- 2: second player win -- 0: unknown
    }

    public void setDelegate(ChessboardInterface delegate) {
        this.delegate = delegate;
    }

    public void setIsFirstPlayer(boolean isFirstPlayer) {
        this.isFirstPlayer = isFirstPlayer;
    }

    public void setMessage(String message) {
        this.message.setText(message);
    }

    public void setPlayerName(String firstPlayerName, String secondPlayerName) {
        this.firstPlayerName = firstPlayerName;
        this.secondPlayerName = secondPlayerName;
        this.firstPlayerNameLabel.setText(firstPlayerName);
        this.secondPlayerNameLabel.setText(secondPlayerName);
        this.switchTurn(1);
    }

    public void switchTurn(int turn) {
        if (turn == 1) {
            this.firstPlayerNamePanel.setBorder(BorderFactory.createBevelBorder(0, Color.green, dark));
            this.secondPlayerNamePanel.setBorder(BorderFactory.createEmptyBorder());
        } else if (turn == 2) {
            this.secondPlayerNamePanel.setBorder(BorderFactory.createBevelBorder(0, Color.green, dark));
            this.firstPlayerNamePanel.setBorder(BorderFactory.createEmptyBorder());
        }
    }

    public boolean isInsideChessboard(Point point) {
        int x = point.x;
        int y = point.y;
        return !(x < 0 || x > 7 || y < 0 || y > 7);
    }

    public Piece getPieceAt(Point point) {
        for (Piece piece : this.firstPlayerPieces) {
            if (point.equals(piece.getNowPosition())) {
                return piece;
            }
        }

        for (Piece piece : this.secondPlayerPieces) {
            if (point.equals(piece.getNowPosition())) {
                return piece;
            }
        }
        return null;
    }

    public void drawChessboard() {
        this.getContentPane().setBackground(new Color(48, 46, 43));
        this.getRootPane().setDefaultButton(sendMessageButton);
        int width = this.playArea.getWidth() / 8;
        int height = this.playArea.getHeight() / 8;
        this.chessWidth = width;
        
        if (this.isFirstPlayer) {
            this.addPieces(firstPlayerPieces, true, false);
            this.addPieces(secondPlayerPieces, false, true);
        } else {
            this.addPieces(firstPlayerPieces, true, true);
            this.addPieces(secondPlayerPieces, false, false);
        }
        
        for (Piece piece : this.firstPlayerPieces) {
            int x = piece.getNowPosition().x;
            int y = piece.getNowPosition().y;
            piece.setBounds(x * width, y * width, width, height);
            this.playArea.add(piece, 2, 0);
        }

        for (Piece piece : this.secondPlayerPieces) {
            int x = piece.getNowPosition().x;
            int y = piece.getNowPosition().y;
            piece.setBounds(x * width, y * width, width, height);
            this.playArea.add(piece, 2, 0);
        }

        boolean isLightStart = false;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Square square = new Square();
                square.setBounds(i * width, j * height, width, height);
                square.setOriginalColor(isLightStart ? j % 2 == 0 ? light : dark : j % 2 == 0 ? dark : light);
                square.setSecondaryColor(isLightStart ? j % 2 == 0 ? lightSelected : darkSelected : j % 2 == 0 ? darkSelected : lightSelected);
                square.setPosition(new Point(i, j));
                square.turnToOriginalColor();
                this.playArea.add(square);
                this.squares.put(square.getKey(), square);
                if (j == 7) {
                    isLightStart = !isLightStart;
                }
            }
        }

        this.playArea.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX() / width;
                int y = e.getY() / height;
                Point point = new Point(x, y);
                Piece piece = getPieceAt(point);
                turnToOriginalColor();
                if (firstPiece == null) {
                    System.out.println("check 1");
                    if (piece != null && !(piece.isBelongToFirstPlayer() ^ isFirstPlayer)) {
                        System.out.println("check 11");
                        firstPiece = piece;
                        showPossibleDestinations(piece);
                    } else {
                        System.out.println("check 12");
                        System.out.println("Not your piece!");
                    }
                } else {
                    System.out.println("check 2");
                    Point source = (Point) firstPiece.getNowPosition().clone();
                    Move move = new Move(source, point);
                    if (piece == null) {
                        System.out.println("check 21");
                        if (firstPiece.isMoveAccepted(move) && delegate != null) {
                            delegate.didMove(move);
                            firstPiece = null;
                        } else {
                            firstPiece = null;
                        }
                    } else if ((piece.isBelongToFirstPlayer() ^ isFirstPlayer)) {
                        System.out.println("check 22");
                        if (firstPiece.isMoveAccepted(move) && delegate != null) {
                            delegate.didMove(move);
                            firstPiece = null;
                        } else {
                            firstPiece = null;
                        }
                    } else {
                        System.out.println("check 23");
                        firstPiece = piece;
                        showPossibleDestinations(piece);
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                int option = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to close this chessboard?", "Close Window?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if ( option == JOptionPane.YES_OPTION) {
                    if (delegate != null) {
                        delegate.didClickCloseChessboard();
                    } else {
                        closeChessboard();
                        System.out.println("Please set delegate to handle close window event!");
                    }
                } else if (option == JOptionPane.NO_OPTION) {
                    System.out.println("cancelled");
                }
            }
        });
    }

    public void destruct() {
        for (Piece piece : this.deadPieces) {
            piece.chessboard = null;
            piece = null;
        }
        for (Piece piece : this.firstPlayerPieces) {
            piece.chessboard = null;
            piece = null;
        }
        for (Piece piece : this.secondPlayerPieces) {
            piece.chessboard = null;
            piece = null;
        }
        this.delegate = null;
    }

    public void addMessageHistory(String message, boolean isFirstPlayer) {
        String messageHistory = this.messageTextArea.getText();
        if (isFirstPlayer) {
            this.messageTextArea.setText(messageHistory + this.firstPlayerName + ": " + message + "\n");
        } else {
            this.messageTextArea.setText(messageHistory + this.secondPlayerName + ": " + message + "\n");
        }
    }

    //---------------------- private function -------------------------
    private void addPieces(ArrayList<Piece> playerPieces, boolean isFirstPlayer, boolean isAbove) {
        String path = "src/MangMayTinh/Resource/Images/";
        int position = 7;
        if (isFirstPlayer) {
            path += "WhitePlayer/";
        } else {
            path += "BlackPlayer/";
        }

        if (isAbove) {
            position = 0;
        } else {
            position = 7;
        }

        File file;
        BufferedImage pieceImage;
        Image pieceImageScale;
        try {
            file = new File(path + King.className + extension);
            pieceImage = ImageIO.read(file);
            pieceImageScale = pieceImage.getScaledInstance(pieceImage.getWidth() / imageRate, pieceImage.getHeight() / imageRate, Image.SCALE_SMOOTH);
            King king = new King(new Point(Math.abs(position - 3), position), pieceImageScale, isFirstPlayer, this);
            file = new File(path + Queen.className + extension);
            pieceImage = ImageIO.read(file);
            pieceImageScale = pieceImage.getScaledInstance(pieceImage.getWidth() / imageRate, pieceImage.getHeight() / imageRate, Image.SCALE_SMOOTH);
            Queen queen = new Queen(new Point(Math.abs(position - 4), position), pieceImageScale, isFirstPlayer, this);
            playerPieces.add(queen);
            playerPieces.add(king);

            file = new File(path + Rook.className + extension);
            pieceImage = ImageIO.read(file);
            pieceImageScale = pieceImage.getScaledInstance(pieceImage.getWidth() / imageRate, pieceImage.getHeight() / imageRate, Image.SCALE_SMOOTH);
            Rook leftRook = new Rook(new Point(0, position), pieceImageScale, isFirstPlayer, this);
            playerPieces.add(leftRook);
            Rook rightRook = new Rook(new Point(7, position), pieceImageScale, isFirstPlayer, this);
            playerPieces.add(rightRook);

            file = new File(path + Knight.className + extension);
            pieceImage = ImageIO.read(file);
            pieceImageScale = pieceImage.getScaledInstance(pieceImage.getWidth() / imageRate, pieceImage.getHeight() / imageRate, Image.SCALE_SMOOTH);
            Knight leftKnight = new Knight(new Point(1, position), pieceImageScale, isFirstPlayer, this);
            playerPieces.add(leftKnight);
            Knight rightKnight = new Knight(new Point(6, position), pieceImageScale, isFirstPlayer, this);
            playerPieces.add(rightKnight);

            file = new File(path + Bishop.className + extension);
            pieceImage = ImageIO.read(file);
            pieceImageScale = pieceImage.getScaledInstance(pieceImage.getWidth() / imageRate, pieceImage.getHeight() / imageRate, Image.SCALE_SMOOTH);
            Bishop leftBishop = new Bishop(new Point(2, position), pieceImageScale, isFirstPlayer, this);
            playerPieces.add(leftBishop);
            Bishop rightBishop = new Bishop(new Point(5, position), pieceImageScale, isFirstPlayer, this);
            playerPieces.add(rightBishop);

            for (int i = 0; i < 8; i++) {
                file = new File(path + Pawn.className + extension);
                pieceImage = ImageIO.read(file);
                pieceImageScale = pieceImage.getScaledInstance(pieceImage.getWidth() / imageRate, pieceImage.getHeight() / imageRate, Image.SCALE_SMOOTH);
                Pawn pawn = new Pawn(new Point(i, Math.abs(position - 1)), pieceImageScale, isFirstPlayer, this);
                playerPieces.add(pawn);
            }
        } catch (IOException ex) {
            System.out.println("Load image error: " + ex.toString());
        }
    }

    private void addMoveHistory() {
        String name;
        Piece piece = this.getPieceAt(this.currentMove.destination);
        if (piece == null) {
            System.out.println("piece at current move destination null");
            return;
        }
        System.out.println(this.isFirstPlayer);
        System.out.println(this.firstPlayerName);
        System.out.println(this.secondPlayerName);
        System.out.println(piece.isBelongToFirstPlayer());
        if (piece.isBelongToFirstPlayer()) {
            name = this.firstPlayerName;
        } else {
            name = this.secondPlayerName;
        }
        String moveHistoryString = this.moveHistory.getText();
        String move = name + ": (" + this.currentMove.source.x + ", " + this.currentMove.source.y + ") ==========> (" + this.currentMove.destination.x + ", " + this.currentMove.destination.y + ")\n";
        moveHistoryString = moveHistoryString + move;
        this.moveHistory.setText(moveHistoryString);
    }

    private void showPossibleDestinations(Piece piece) {
        piece.generatePossibleDestination();
        for (Point destination : piece.possibleDestinations) {
            String key = Square.getKey(destination);
            Square square = squares.get(key);
            square.turnToSecondaryColor();
        }
    }

    private void turnToOriginalColor() {
        for (Map.Entry pair : this.squares.entrySet()) {
            Square square = (Square) pair.getValue();
            Point point = square.getPosition();
            if (this.currentMove != null) {

                if ((point.equals(this.currentMove.destination) || point.equals(this.currentMove.source))) {
                    continue;
                }
            }
            square.turnToOriginalColor();
        }
    }
    
    private void closeChessboard() {
        this.dispose();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        secondPlayerNamePanel = new javax.swing.JPanel();
        secondPlayerNameLabel = new javax.swing.JLabel();
        firstPlayerNamePanel = new javax.swing.JPanel();
        firstPlayerNameLabel = new javax.swing.JLabel();
        moveHistoryContainer = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        moveHistory = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        messageTextArea = new javax.swing.JTextArea();
        messageTextField = new javax.swing.JTextField();
        sendMessageButton = new javax.swing.JButton();
        playArea = new javax.swing.JLayeredPane();
        jPanel3 = new javax.swing.JPanel();
        message = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setBackground(new java.awt.Color(48, 46, 43));
        setForeground(new java.awt.Color(48, 46, 43));
        setPreferredSize(new java.awt.Dimension(1200, 800));
        setResizable(false);

        jPanel1.setPreferredSize(new java.awt.Dimension(340, 150));

        secondPlayerNamePanel.setBackground(new java.awt.Color(255, 255, 255));

        secondPlayerNameLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        secondPlayerNameLabel.setText("Player Name");

        javax.swing.GroupLayout secondPlayerNamePanelLayout = new javax.swing.GroupLayout(secondPlayerNamePanel);
        secondPlayerNamePanel.setLayout(secondPlayerNamePanelLayout);
        secondPlayerNamePanelLayout.setHorizontalGroup(
            secondPlayerNamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(secondPlayerNamePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(secondPlayerNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        secondPlayerNamePanelLayout.setVerticalGroup(
            secondPlayerNamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(secondPlayerNamePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(secondPlayerNameLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        firstPlayerNamePanel.setBackground(new java.awt.Color(255, 255, 255));

        firstPlayerNameLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        firstPlayerNameLabel.setText("Player Name");

        javax.swing.GroupLayout firstPlayerNamePanelLayout = new javax.swing.GroupLayout(firstPlayerNamePanel);
        firstPlayerNamePanel.setLayout(firstPlayerNamePanelLayout);
        firstPlayerNamePanelLayout.setHorizontalGroup(
            firstPlayerNamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(firstPlayerNamePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(firstPlayerNameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE)
                .addContainerGap())
        );
        firstPlayerNamePanelLayout.setVerticalGroup(
            firstPlayerNamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, firstPlayerNamePanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(firstPlayerNameLabel)
                .addContainerGap())
        );

        moveHistoryContainer.setBackground(new java.awt.Color(255, 255, 255));

        moveHistory.setEditable(false);
        moveHistory.setColumns(20);
        moveHistory.setRows(5);
        jScrollPane1.setViewportView(moveHistory);

        javax.swing.GroupLayout moveHistoryContainerLayout = new javax.swing.GroupLayout(moveHistoryContainer);
        moveHistoryContainer.setLayout(moveHistoryContainerLayout);
        moveHistoryContainerLayout.setHorizontalGroup(
            moveHistoryContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
        );
        moveHistoryContainerLayout.setVerticalGroup(
            moveHistoryContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(moveHistoryContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(firstPlayerNamePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(secondPlayerNamePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(secondPlayerNamePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(firstPlayerNamePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(moveHistoryContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        messageTextArea.setEditable(false);
        messageTextArea.setColumns(20);
        messageTextArea.setRows(5);
        jScrollPane2.setViewportView(messageTextArea);

        messageTextField.setToolTipText("Enter your message");
        messageTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                didTypeMessage(evt);
            }
        });

        sendMessageButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/MangMayTinh/Resource/Images/commonIcons/sendIcon.png"))); // NOI18N
        sendMessageButton.setBorderPainted(false);
        sendMessageButton.setContentAreaFilled(false);
        sendMessageButton.setEnabled(false);
        sendMessageButton.setFocusPainted(false);
        sendMessageButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendMessageButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(messageTextField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sendMessageButton)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sendMessageButton)
                    .addComponent(messageTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16))
        );

        playArea.setBackground(new java.awt.Color(255, 255, 255));
        playArea.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        playArea.setForeground(new java.awt.Color(255, 255, 255));
        playArea.setPreferredSize(new java.awt.Dimension(640, 640));

        javax.swing.GroupLayout playAreaLayout = new javax.swing.GroupLayout(playArea);
        playArea.setLayout(playAreaLayout);
        playAreaLayout.setHorizontalGroup(
            playAreaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 638, Short.MAX_VALUE)
        );
        playAreaLayout.setVerticalGroup(
            playAreaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 638, Short.MAX_VALUE)
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        message.setForeground(new java.awt.Color(0, 51, 255));
        message.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        message.setText("Chess");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(177, 177, 177)
                .addComponent(message)
                .addContainerGap(192, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(message)
                .addContainerGap())
        );

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(80, 80, 80)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(playArea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(46, 46, 46)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(92, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(playArea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(80, 80, 80))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void didTypeMessage(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_didTypeMessage
        String message = this.messageTextField.getText();
        if (message.isEmpty()) {
            this.sendMessageButton.setEnabled(false);
        } else {
            this.sendMessageButton.setEnabled(true);
        }
    }//GEN-LAST:event_didTypeMessage

    private void sendMessageButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendMessageButtonActionPerformed
        String message = this.messageTextField.getText();
        this.addMessageHistory(message, isFirstPlayer);
        this.delegate.didSendMessage(message);
        this.messageTextField.setText("");
    }//GEN-LAST:event_sendMessageButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Chessboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Chessboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Chessboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Chessboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Chessboard().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel firstPlayerNameLabel;
    private javax.swing.JPanel firstPlayerNamePanel;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel message;
    private javax.swing.JTextArea messageTextArea;
    private javax.swing.JTextField messageTextField;
    private javax.swing.JTextArea moveHistory;
    private javax.swing.JPanel moveHistoryContainer;
    private javax.swing.JLayeredPane playArea;
    private javax.swing.JLabel secondPlayerNameLabel;
    private javax.swing.JPanel secondPlayerNamePanel;
    private javax.swing.JButton sendMessageButton;
    // End of variables declaration//GEN-END:variables

}
