import javax.swing.*;
import java.util.Vector;

public class ChatBox {
    private int index;
    private Vector<String> participants;
    private DefaultListModel<String> messagesModel;
    private DefaultListModel<String> filesModel;

    ChatBox(int index, Vector<String> participants, DefaultListModel<String> messagesModel){
        this.index = index;
        this.participants = participants;
        this.messagesModel = messagesModel;
        this.filesModel = extractFilesModel(messagesModel);
    }

    public int getIndex() {
        return index;
    }
    public Vector<String> getParticipants() {
        return participants;
    }
    public DefaultListModel<String> getMessagesModel() {
        return messagesModel;
    }

    public String getParticipantsString(){
        StringBuilder builder = new StringBuilder();

        for (String p: participants){
            builder.append(p).append("|");
        }

        builder.deleteCharAt(builder.length() - 1);

        return builder.toString();
    }

    public DefaultListModel<String> getFilesModel() {
        return this.filesModel;
    }

    public void updateChatBoxData(DefaultListModel<String> messagesModel){
        this.messagesModel = messagesModel;
        this.filesModel = extractFilesModel(messagesModel);
    }

    public static DefaultListModel<String> extractFilesModel(DefaultListModel<String> messagesModel){
        DefaultListModel<String> filesModel = new DefaultListModel<>();

        // Simple check
        for (int i = 0; i < messagesModel.size(); i++){
            String message = messagesModel.getElementAt(i);

            if (message.contains("[") && message.endsWith("]")){
                filesModel.addElement(message);
            }
        }

        return filesModel;
    }

}
