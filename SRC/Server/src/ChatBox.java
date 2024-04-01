import java.util.Vector;

public class ChatBox {
    private Vector<User> participants;
    private Vector<String> messages;

    ChatBox(Vector<User> participants){
        UserManager.sort(participants);
        this.participants = participants;
        this.messages = new Vector<>();
//        messages.add(participants.get(0) +": Hello");
//        messages.add(participants.get(1) +": Hi");
    }

    public Vector<User> getParticipants() {
        return participants;
    }

    public void setParticipants(Vector<User> participants) {
        this.participants = participants;
    }

    public Vector<String> getMessages() {
        return messages;
    }

    public void setMessages(Vector<String> messages) {
        this.messages = messages;
    }

    public String getParticipantsString(){
        StringBuilder builder = new StringBuilder();

        for (User p: participants){
            builder.append(p.getName()).append("|");
        }

        builder.deleteCharAt(builder.length() - 1);

        return builder.toString();
    }

    public String getChatDataString(){
        // `user1|user2|user3][msg1|msg2|msg3
        StringBuilder builder = new StringBuilder();
        // Chat Box divider
        builder.append("`");

        // Packaging participants
        builder.append(getParticipantsString());

        // User-message divider
        builder.append("][");

        // Packaging messages
        if (messages.isEmpty()) {
            return builder.toString();
        }

        for (String m: messages){
            builder.append(m).append("|");
        }

        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    public boolean hasThisUser(User user){
        return participants.contains(user);
    }
}
