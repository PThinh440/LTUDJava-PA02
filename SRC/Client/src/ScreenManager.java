import javax.swing.*;

public class ScreenManager {
    private JFrame frame;

    public JFrame getFrame(){
        return frame;
    }
    public void setFrame(JFrame newFrame){
        frame = newFrame;
    }

    public void switchFrame(JFrame newFrame){
        frame.dispose();
        frame = newFrame;
    }
}
