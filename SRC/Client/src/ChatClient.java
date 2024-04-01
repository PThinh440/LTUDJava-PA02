import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Vector;

public class ChatClient extends Thread
{
    private static BufferedReader receiver;
    private static InputStream inputStream;
    private static OutputStream outputStream;
    private static BufferedWriter sender;
    private static String username;
    private static Vector<ChatBox> chatBoxes;
    private static int curChatBoxIndex;
    private static boolean uploadStatus;

    private String downloadFilePath = "";

    ChatClient(){
        username = "";
        curChatBoxIndex = -1;
        inputStream = null;
        outputStream = null;
        sender = null;
        receiver = null;
        uploadStatus = false;
    }

    public String getUsername(){
        return username;
    }

    public boolean getUploadStatus() {
        return uploadStatus;
    }
    public void setCurChatBoxIndex(int curChatBoxIndex) {
        ChatClient.curChatBoxIndex = curChatBoxIndex;
    }
    public void setDownloadFilePath(String downloadFilePath){
        this.downloadFilePath = downloadFilePath;
    }

    //////////////////////////////////////////////////

    public ChatBox getCurChatBox(){
        if (curChatBoxIndex < 0) {
            return null;
        }
        return chatBoxes.get(curChatBoxIndex);
    }

    private void responseHandler(String res){
        String[] pattern = res.split("`");

        switch (pattern[0]){
            case "Error": case "Notify":{
                JOptionPane.showMessageDialog(null, pattern[1]);
                if (pattern[1].equals("Uploading success!")){
                    uploadStatus = true;
                }
                break;
            }
            case "Verified": {
                JOptionPane.showMessageDialog(null,
                        "Account created successfully! Please sign in to continue.");
                EntryScreen.getTextInput().setText("");
                break;
            }
            case "Access granted": {
                username = pattern[1];
                System.out.println("Your account is: " + username);

                // Get chats
                sendMsg("Get chats`" + username);

                Main.getScreenManager().switchFrame(ChatScreen.createFrame());
                break;
            }
            case "Chat list": {
                // Convert raw data to ChatBox Objects
                chatBoxes = processData(pattern);

                // Display list of chats            // Get names
                ChatScreen.getChatList().setListData(getChatsName());
                break;
            }
            case "Chat update": {
                // patternString = "Chat update`user1|user2|user3][msg1|msg2|msg3"
                // Get data fields by splitting dividers
                String[] rawChatBox = pattern[1].split("]\\[");
                String participantsString = rawChatBox[0];
                ChatBox cb = getChatByParticipantsString(participantsString);

                if (cb != null){
                    // Update chat

                    // Messages data field
                    DefaultListModel<String> messagesModel = new DefaultListModel<>();
                    DefaultListModel<String> filesModel = new DefaultListModel<>();

                    if (rawChatBox.length > 1){ // if is not empty
                        String[] messages = rawChatBox[1].split("\\|");
                        messagesModel.addAll(List.of(messages));
                    }

                    // Update chat box data
                    // The participants will not be changed by any purposes
                    /* Vector<String> participants = new Vector<>(
                            List.of(participantsString.split("\\|")));
                    cb.setParticipants(participants);*/
                    cb.updateChatBoxData(messagesModel);

                    System.out.println("Chat updated successfully");

                    // Update chat box panel if this is the current chat box
                    if (curChatBoxIndex >= 0 && cb == getCurChatBox()){
                        ChatScreen.getChatMsg().setModel(cb.getMessagesModel());
                        ChatScreen.getFilesList().setModel(cb.getFilesModel());
                    }

                } else{
                    System.out.println("Cannot find chat: " + participantsString);
                }

                break;
            }
            case "Signed out": {
                System.out.println("Signed out of " + username);
                username = "";
                Main.getScreenManager().switchFrame(EntryScreen.createFrame());
                break;
            }
            case "File": {
                String filePath = downloadFilePath + "\\" + pattern[1];
                System.out.println(filePath);

                File file = new File(filePath);
                int fileSize = Integer.parseInt(pattern[2]);

                try {
                    OutputStream os = new FileOutputStream(file);

                    System.out.println("Reading");
                    os.write(inputStream.readNBytes(fileSize));
                    System.out.println("Read");
                    os.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                break;
            }
            default:
                break;
        }
    }

    private Vector<ChatBox> processData(String[] pattern) {
        // patternString = "Chat list`user1|user2|user3][msg1|msg2|msg3`user2|user4][..."
        Vector<ChatBox> results = new Vector<>();

        // For every chat box data...
        for (int i = 1; i < pattern.length; i++){
            // Get data fields by splitting dividers
            String[] rawChatBoxData = pattern[i].split("]\\[");
            // Users data field
            Vector<String> users = new Vector<>(List.of(rawChatBoxData[0].split("\\|")));

            // Messages data field
            DefaultListModel<String> messages = new DefaultListModel<>();

            if (rawChatBoxData.length > 1){ // if is not empty
                messages.addAll(List.of(rawChatBoxData[1].split("\\|")));
            }

            results.add(new ChatBox(i - 1, users, messages));
        }

        return results;
    }

    private Vector<String> getChatsName() {
        Vector<String> result = new Vector<>();

        // For every chat box that has this user ...
        for (ChatBox cb : chatBoxes) {
            StringBuilder builder = new StringBuilder();
            boolean isGroupChat = false;

            // Check if this is a group chat
            if (cb.getParticipants().size() > 2){
                builder.append("Group chat: ");
                isGroupChat = true;
            }

            // Get all participant except this user if this is not a group chat
            for (String p : cb.getParticipants()) {
                if (isGroupChat || !p.equals(username)) {
                    builder.append(p);
                    builder.append(", ");
                }
            }

            // Remove tail characters
            builder.delete(builder.length() - 2, builder.length());
            result.add(builder.toString());
        }

        return result;
    }

    private ChatBox getChatByParticipantsString(String participantsString) {
        for (ChatBox cb: chatBoxes){
            // Sorted on server side
            if (cb.getParticipantsString().equals(participantsString)){
                return cb;
            }
        }
        return null;
    }

    public ChatBox getChatBoxByIndex(int index) {
        return chatBoxes.get(index);
    }

    public void sendMsg(String msg) {
        System.out.println("Send: " + msg);
        try {
            sender.write(msg);
            sender.newLine();
            sender.flush();
        } catch (IOException e) {
            System.out.println("Error: Cannot send message");
        }
    }

    public void sendFile(File file) {
        try {
            // Read file into bytes and send
            InputStream in = new FileInputStream(file);
            outputStream.write(in.readAllBytes());
            outputStream.flush();

            in.close();

            // trigger flag to prevent another uploaded data
            // so the output stream will not be mixed up
            uploadStatus = false;
        } catch (IOException e) {
            System.out.println("Error: Cannot send file");
        }
    }

    public void run(){
        try
        {
            Socket s = new Socket("localhost",4400);
            System.out.println(s.getPort());

            // Initialize properties
            inputStream = s.getInputStream();
            outputStream = s.getOutputStream();
            receiver = new BufferedReader(new InputStreamReader(inputStream));
            sender = new BufferedWriter(new OutputStreamWriter(outputStream));
            uploadStatus = true;

            String receivedMessage;

            while (true) {
                try {
                    receivedMessage = receiver.readLine();
                    System.out.println("Received from Server : " + receivedMessage);

                    responseHandler(receivedMessage);

                } catch (IOException e) {
                    System.out.println("Error: Cannot read from server");
                    throw new RuntimeException(e);
                }
            }
        } catch (UnknownHostException e) {
            System.out.println("Error: Cannot connect to server");
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.out.println("Error: Cannot read from server");
        }
    }
}
