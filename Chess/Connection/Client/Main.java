package LapTrinhMang.Chess.Connection.Client;

public class Main {
    public static void main(String[] args) {
        Client client = new Client();
        
        // Thử kết nối tới server
        if (client.connectToServer()) {
            // Nếu kết nối thành công, hiển thị giao diện
            client.setVisible(true);
        } else {
            // Nếu kết nối thất bại, thông báo lỗi
            System.out.println("Không thể kết nối đến server.");
        }
    }
}
