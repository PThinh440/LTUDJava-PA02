import java.util.Vector;

public class User {
    private String name;
    User(String name){
        setName(name);
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getUserChatListString(){

        Vector<ChatBox> cbs = Main.getChatServer().getChatBoxManager().getChatOfUser(this);
        StringBuilder builder = new StringBuilder();

        for (ChatBox cb: cbs){
            // Find any chat box that this user is participating
            if (cb.hasThisUser(this)){
                // `user1|user2|user3][msg1|msg2|msg3
                builder.append(cb.getChatDataString());
            }
        }

        // `user1|user2|user3][msg1|msg2|msg3`user2|user4][...
        return builder.toString();
    }


}


