import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Vector;

public class ChatServer extends Thread
{
    private final UserManager userManager;
    private final ChatBoxManager chatBoxManager;
    private final Vector<ClientConnection> connections;

    ChatServer(){
        userManager = new UserManager();
        chatBoxManager = new ChatBoxManager();
        connections = new Vector<>();
    }

    public ChatBoxManager getChatBoxManager() {
        return chatBoxManager;
    }
    public UserManager getUserManager(){
        return userManager;
    }

    /////////////////////////////////////////////

    public void sendMultipleChatUpdates(ChatBox cb){
        // Chat update`user1|user2|user3][msg1|msg2|msg3
        String msg = "Chat update" + cb.getChatDataString();

        for (ClientConnection connect: connections){
            if (cb.hasThisUser(connect.getUser())){
                connect.sendMsgToUser(msg);
            }
        }
    }

    public void updateChatDatabaseWithNewUser(User newUser){
        // Create chats with any user in user db
        for (User user: userManager.getUsers()){
            if (user == newUser) {
                continue;
            }

            // add in chat db
            chatBoxManager.createNewChat(new Vector<>(List.of(user, newUser)));
        }
    }

    public void broadcastNewGroupChat(ChatBox cb){
        for (User u: cb.getParticipants()){
            System.out.println(u + ": " + u.getName());
        }
        // Send broadcast
        for (ClientConnection connect: connections){
            User user = connect.getUser();

            System.out.println("Checking ");
            System.out.println(user + ": " + user.getName());

            if (cb.hasThisUser(user)) {
                System.out.println("Checked "+ user.getName());
                connect.sendMsgToUser("Chat list" + user.getUserChatListString());
            }
        }
    }

    public void broadcastNewUser(User newUser){
        // Send broadcast
        for (ClientConnection connect: connections){
            User user = connect.getUser();

            if (user == newUser) {
                continue;
            }

            connect.sendMsgToUser("Chat list" + user.getUserChatListString());
        }
    }

    public void run(){
        try
        {
            ServerSocket s = new ServerSocket(4400);

            do {
                System.out.println("Waiting for a Client");

                Socket ss = s.accept(); //synchronous

                ClientConnection connection = new ClientConnection(ss);
                connection.start();
                connections.add(connection);
            }
            while (true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
