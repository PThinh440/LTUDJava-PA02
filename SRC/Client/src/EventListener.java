import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.*;

class EventListener implements ActionListener, MouseListener {

    String previousFilePath = "";

    public void actionPerformed(ActionEvent e){
        String actionCommand = e.getActionCommand();
        System.out.println(actionCommand);

        switch (actionCommand){
            case "Sign in": case "Sign up":
            {
                String username = EntryScreen.getTextInput().getText();
                String msg = actionCommand + "`" + username;

                Main.getChatClient().sendMsg(msg);
                break;
            }
            case "Sign out": {
                String msg = actionCommand + "`" + Main.getChatClient().getUsername();

                Main.getChatClient().sendMsg(msg);
                System.out.println("Signed out");
                break;
            }
            case "New group chat": {
                String input = JOptionPane.showInputDialog(null,
                        "Who do you want to invite?", "John, Bob");

                if (!input.isEmpty()){
                    input += ", " + Main.getChatClient().getUsername();
                    Set<String> participants = Collections.synchronizedSet(new HashSet<>(List.of(input.split(", "))));

                    if (participants.size() < 3){
                        JOptionPane.showMessageDialog(null,
                                "A group should have at least 3 users");
                    } else{

                        String msg = participants.toString()
                                .replaceAll(", ", "|")
                                .replaceAll("\\[", "")
                                .replaceAll("]","");
                        Main.getChatClient().sendMsg(actionCommand + "`" + msg);
                        // New group chat`userA|userB|userC
                    }
                }

                break;
            }
            case "Send": {
                if (Main.getChatClient().getCurChatBox() == null){
                    JOptionPane.showMessageDialog(null, "Please choose a chat box");
                    break;
                }

                String msg = actionCommand + "`" +
                        Main.getChatClient().getCurChatBox().getParticipantsString() + "`" +
                        Main.getChatClient().getUsername() + ": " +
                        ChatScreen.getTextInput().getText();

                System.out.println(ChatScreen.getTextInput().getText());
                // Send`userA|userB`sender: msg
                Main.getChatClient().sendMsg(msg);

                ChatScreen.getTextInput().setText("");

                break;
            }
            case "File": {
                if (Main.getChatClient().getCurChatBox() == null){
                    JOptionPane.showMessageDialog(null, "Please choose a chat box");
                    break;
                }

                if (!Main.getChatClient().getUploadStatus()){
                    JOptionPane.showMessageDialog(null, "There's a file uploading already");
                    break;
                }

                JFileChooser fileChooser = new JFileChooser(previousFilePath);
                fileChooser.setDialogTitle("Choose a file to send");

                int result = fileChooser.showDialog(null, "Choose");
//                fileChooser.setVisible(true);

                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    previousFilePath = file.getAbsolutePath();

                    System.out.println(file.getAbsolutePath() + ",," + file.getName());
                    // Send file
                    // File`user1|user2`sender: [File]`size
                    String msg = actionCommand + "`" +
                            Main.getChatClient().getCurChatBox().getParticipantsString() + "`" +
                            Main.getChatClient().getUsername() + ": [" + file.getName() + "]`" +
                            file.length();
                    Main.getChatClient().sendMsg(msg);
                    Main.getChatClient().sendFile(file);
                }


                break;
            }
            default:
                break;
        }
    }

    public void mouseClicked(MouseEvent e) {
        JList<String> list = (JList<String>) e.getSource();

        int index = list.locationToIndex(e.getPoint());
        System.out.println("Clicked index = " + index);

        if (index >= 0) {
            if (list == ChatScreen.getChatList()) {
                ChatBox chosenChat = Main.getChatClient().getChatBoxByIndex(index);
                ChatScreen.getChatMsg().setModel(chosenChat.getMessagesModel());
                ChatScreen.getFilesList().setModel(chosenChat.getFilesModel());
                Main.getChatClient().setCurChatBoxIndex(index);
            }

            if (list == ChatScreen.getChatMsg()) {
                // Check if user want to delete && if the user has permission
                if (e.getClickCount() == 3 && list.getSelectedValue().startsWith
                        (Main.getChatClient().getUsername() + ":")) {
                    // trigger
                    int option = JOptionPane.showConfirmDialog(null,
                            "Do you want to delete your message?");

                    if (option == JOptionPane.YES_OPTION){
                        String msg = "Delete" + "`" +
                                Main.getChatClient().getCurChatBox().getParticipantsString() + "`" +
                                list.getSelectedValue();
                        Main.getChatClient().sendMsg(msg);
                    }
                }
            }

            if (list == ChatScreen.getFilesList()) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Choose where to save this file");
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                int result = fileChooser.showDialog(null, "Choose");
//                fileChooser.setVisible(true);

                if (result == JFileChooser.APPROVE_OPTION) {
                    File folder = fileChooser.getSelectedFile();
                    previousFilePath = folder.getAbsolutePath();
                    Main.getChatClient().setDownloadFilePath(folder.getAbsolutePath());

                    String msg = "Get file`" +
                            Main.getChatClient().getCurChatBox().getParticipantsString() + "`" +
                            list.getSelectedValue();
                    Main.getChatClient().sendMsg(msg);
                }
            }
        }
    }

    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

}