import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ChatScreen {
    private static JTextArea textInput;
    private static JList<String> chatList;
    private static JList<String> chatMsg;
    private static JList<String> filesList;

    public static JTextArea getTextInput(){
        return textInput;
    }

    public static JList<String> getChatMsg(){
        return chatMsg;
    }

    public static JList<String> getChatList(){
        return chatList;
    }

    public static JList<String> getFilesList(){
        return filesList;
    }

    private static JPanel createChatListPanel(EventListener listener){
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(150, 200));

        /// Create new group chat Button
        // Align panel
        JPanel alignPanel = new JPanel();
        alignPanel.setLayout(new BoxLayout(alignPanel, BoxLayout.X_AXIS));

        // Button
        JButton button = new JButton("New group chat");
        button.setActionCommand("New group chat");
        button.addActionListener(listener);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        alignPanel.add(button);
        panel.add(alignPanel);

        /// Add rigid area
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        /// Chat List
        chatList = new JList<>();
        chatList.addMouseListener(listener);

        /// Add scroll pane
        JScrollPane scrollPane = new JScrollPane(chatList);
        panel.add(scrollPane);

        /// Add rigid area
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        /// Sign out button
        // Align panel
        alignPanel = new JPanel();
        alignPanel.setLayout(new BoxLayout(alignPanel, BoxLayout.X_AXIS));

        // Button
        button = new JButton("Sign out");
        button.setActionCommand("Sign out");
        button.addActionListener(listener);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        alignPanel.add(button);
        panel.add(alignPanel);

        return panel;
    }

    private static JPanel createChatBoxPanel(EventListener listener){
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
//        panel.setPreferredSize(new Dimension(200, 200));

        /// Chat history List
        chatMsg = new JList<>(new String[]{"Welcome to the chat box!","Please choose a chat."});

        chatMsg.addMouseListener(listener);

        // Add scroll pane
        JScrollPane scrollPane = new JScrollPane(chatMsg);

        panel.add(scrollPane);

        /// Add rigid area
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Label
//        JLabel label = new JLabel("Type here");
//        label.setAlignmentX(Component.LEFT_ALIGNMENT);
//        panel.add(label);

        /// Chat section
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.LINE_AXIS));

        // Text input Area
        textInput = new JTextArea();
        textInput.setPreferredSize(new Dimension(100, 40));
        textInput.setLineWrap(true);
        textPanel.add(textInput);

        textPanel.add(Box.createRigidArea(new Dimension(5, 0)));

        // File button
        JButton button = new JButton("File");
        button.setActionCommand("File");
        button.addActionListener(listener);
        textPanel.add(button);

        // Send Button
        button = new JButton("Send");
        button.setActionCommand("Send");
        button.addActionListener(listener);
        textPanel.add(button);

        panel.add(textPanel);

        return panel;
    }

    private static JPanel createChatFilesPanel(EventListener listener){
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(100, 200));

        /// Files list
        filesList = new JList<>();
        filesList.addMouseListener(listener);

        /// Add scroll pane
        JScrollPane scrollPane = new JScrollPane(filesList);
        panel.add(scrollPane);

        return panel;
    }

    private static void createContentPane(Container container){
        EventListener listener = new EventListener();
        ///// Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
//        mainPanel.setPreferredSize(new Dimension(400,300));

        //// Chat list section
        /// Add border
        JPanel borderPanel = new JPanel();
        borderPanel.setBorder(BorderFactory.createTitledBorder("Chat List"));
        borderPanel.add(createChatListPanel(listener));
        mainPanel.add(borderPanel);

        //// Add rigid area
        mainPanel.add(Box.createRigidArea(new Dimension(10, 0)));

        //// Chat box section
        /// Add border
        borderPanel = new JPanel();
        borderPanel.setBorder(BorderFactory.createTitledBorder("Chat Box"));
        borderPanel.add(createChatBoxPanel(listener));
        mainPanel.add(borderPanel);

        //// Add rigid area
        mainPanel.add(Box.createRigidArea(new Dimension(10, 0)));

        //// Chat list section
        /// Add border
        borderPanel = new JPanel();
        borderPanel.setBorder(BorderFactory.createTitledBorder("Chat files"));
        borderPanel.add(createChatFilesPanel(listener));
        mainPanel.add(borderPanel);

        //// Add padding for main panel
        JPanel paddingPanel = new JPanel();
        paddingPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        paddingPanel.add(mainPanel);

        container.add(paddingPanel);
    }

    public static JFrame createFrame(){
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("Chat Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        createContentPane(frame.getContentPane());
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        return frame;
    }
}
