package snake.winter.gui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

  public MainFrame() {
    initFrame();
  }

  private void initFrame() {
    add(new Game(25, 25));

    pack();

    setTitle("Snake");
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }

  public static void main(String[] args) {

    EventQueue.invokeLater(() -> {
      JFrame ex = new MainFrame();
      ex.setVisible(true);
    });
  }
}
