import javax.swing.*;
import java.util.Vector;

public class Main {
    private static ChatServer chatServer;
    public static ChatServer getChatServer(){
        return chatServer;
    }

    private static void setUp(){
        chatServer = new ChatServer(); // server
        chatServer.start();
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setUp();

                JOptionPane.showMessageDialog(null,
                        "Server is running. Click to stop the server.");
                System.exit(0);
            }
        });
    }
}