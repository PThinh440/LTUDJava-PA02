import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class EntryScreen {
    private static JTextField textInput;

    public static JTextField getTextInput(){
        return textInput;
    }


    private static JPanel createUserInputPanel(){
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(180, 50));

        /// Label
        JLabel label = new JLabel("Username");
        panel.add(label);

        /// Username input field
        textInput = new JTextField();
        textInput.setPreferredSize(new Dimension(150, 30));
        panel.add(textInput);

        return panel;
    }

    private static JPanel createEntryPanel(){
        EventListener listener = new EventListener();
        JPanel panel = new JPanel();

        /// Sign in button
        JButton button = new JButton("Sign in");
        button.setActionCommand("Sign in");
        button.addActionListener(listener);

        panel.add(button);

        /// "or" label
        JLabel label = new JLabel("or");
        panel.add(label);

        //// Sign up button
        button = new JButton("Sign up");
        button.setActionCommand("Sign up");
        button.addActionListener(listener);
        panel.add(button);

        return panel;
    }

    private static void createContentPane(Container container){
        EventListener listener = new EventListener();

        ///// Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createTitledBorder("Chat Application"));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setPreferredSize(new Dimension(200,150));

        //// Username input section
        JPanel paddingPanel = new JPanel();
        paddingPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        paddingPanel.add(createUserInputPanel());
        mainPanel.add(paddingPanel);

        //// Entry section
        mainPanel.add(createEntryPanel());

        //// Padding panel for Main panel
        paddingPanel = new JPanel();
        paddingPanel.setBorder(new EmptyBorder(20, 10, 50, 10));
        paddingPanel.add(mainPanel);

        container.add(paddingPanel);
    }

    public static JFrame createFrame(){
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("Chat Application");
        frame.setPreferredSize(new Dimension(250, 250));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        createContentPane(frame.getContentPane());
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        return frame;
    }
}
