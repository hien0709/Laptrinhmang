package LapTrinhMang.Chess.Model.Enum;

/**
 * Enum để định nghĩa các loại thông điệp trong game cờ vua.
 */
public enum MessageType {
    // Các loại thông điệp trong game cờ vua
    STRING,
    MOVE,
    RESULT,
    START_GAME,
    TURN,
    IS_FIRST_PLAYER,
    NAME,
    MESSAGE,
    END_GAME, surrender;

    public static void main(String[] args) {
        // Kiểm tra enum bằng cách in tất cả các loại thông điệp
        for (MessageType type : MessageType.values()) {
            System.out.println(type);
        }
    }
}
