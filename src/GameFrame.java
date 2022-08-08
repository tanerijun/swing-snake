import javax.swing.JFrame;

public class GameFrame extends JFrame {

    GameFrame() {
        add(new GamePanel());
        setTitle("Snake");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack(); // window size depends on its components
        setVisible(true);
        setLocationRelativeTo(null); // center window
    }
}
