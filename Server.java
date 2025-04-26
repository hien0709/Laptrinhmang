package MangMayTinh.Chess.Model;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static final int PORT = 9999;
    private final List<Player> clients = new ArrayList<>();

    public Server() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running on port " + PORT);
            File saveDir = new File("games");
            if (!saveDir.exists()) saveDir.mkdir();

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected: " + socket);

                Player client = new Player(socket);
                addClient(client);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void addClient(Player client) {
        clients.add(client);
        if (clients.size() >= 2) {
            Player p1 = clients.remove(0);
            Player p2 = clients.remove(0);

            BoardState state = loadGameState(p1.getName());
            Match match;
            if (state != null) {
                match = new Match(p1, p2, state);
                System.out.println("Resuming saved game for " + p1.getName());
            } else {
                match = new Match(p1, p2);
                System.out.println("Starting new match");
            }

            new Thread(() -> {
                match.run();

                // Lưu lại trạng thái khi kết thúc/thoát giữa chừng
                saveGameState(p1.getName(), match.getBoardState());
            }).start();
        }
    }

    private void saveGameState(String playerName, BoardState state) {
        try (FileOutputStream fileOut = new FileOutputStream("games/" + playerName + ".sav");
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(state);
            System.out.println("Game state saved for " + playerName);
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    private BoardState loadGameState(String playerName) {
        File file = new File("games/" + playerName + ".sav");
        if (!file.exists()) return null;

        try (FileInputStream fileIn = new FileInputStream(file);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            BoardState state = (BoardState) in.readObject();
            return state;
        } catch (IOException | ClassNotFoundException i) {
            i.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        new Server();
    }
}
