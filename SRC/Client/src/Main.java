import javax.swing.*;

public class Main {
    private static ChatClient chatClient;
    private static ScreenManager screenManager;

    public static ChatClient getChatClient(){
        return chatClient;
    }
    public static ScreenManager getScreenManager(){
        return screenManager;
    }

    private static void setUp(){
        screenManager = new ScreenManager();
        chatClient = new ChatClient();
        chatClient.start();

        // init screen
        screenManager.setFrame(EntryScreen.createFrame());
//        screenManager.setFrame(ChatScreen.createFrame());
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setUp();
            }
        });
    }
}