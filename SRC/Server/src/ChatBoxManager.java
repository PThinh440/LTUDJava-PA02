import java.io.IOException;
import java.util.Vector;

public class ChatBoxManager {

    private Vector<ChatBox> chatBoxes;

    ChatBoxManager(){
        chatBoxes = new Vector<ChatBox>();

    }

    public Vector<ChatBox> getChatBoxes() {
        return chatBoxes;
    }

    public void setChatBoxes(Vector<ChatBox> chatBoxes) {
        this.chatBoxes = chatBoxes;
    }

    /////////////////////////////////////////////

    private void load() throws IOException {

//        while(true) {// while N files
//            // 1 Room
//            BufferedReader br = new BufferedReader(new FileReader("room1.txt"));
//            int n = Integer.parseInt(br.readLine());
//            String[] lines = new String[n];
//            for (int i = 0; i < n; i++) {
//                lines[i] = br.readLine();
//            }
//            br.close();
////            ChatBox cb = new ChatBox();
////            cb.setMessages(lines);
//            break;
//        }
//
//        // N room
//        this.setChatBoxes(chatBoxes);


    }

    public ChatBox getChat(Vector<User> participants){
        if (participants == null) return null;

        for (ChatBox cb: chatBoxes){
            if (cb.getParticipants().equals(participants)){
                return cb;
            }
        }
        return null;
    }

    public Vector<ChatBox> getChatOfUser(User user){

        Vector<ChatBox> result = new Vector<>();
        for (ChatBox cb: chatBoxes){
            if (cb.hasThisUser(user)){
                result.add(cb);
            }
        }

        return result;
    }

    public ChatBox createNewChat(Vector<User> participants){
        ChatBox cb = new ChatBox(participants);
        chatBoxes.add(cb);

        return cb;
    }
}
