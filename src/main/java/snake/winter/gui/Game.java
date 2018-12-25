package snake.winter.gui;

import snake.winter.game.Board;
import snake.winter.game.Point;
import snake.winter.game.Point.Direction;
import snake.winter.game.Snake;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

import static snake.winter.game.Point.Direction.*;

public class Game extends JPanel implements ActionListener {
  private static final int DELAY = 100;
  private static final int BOX_SIZE = 20;
  private static final int BUFFER = 20;
  private static final int SIDE_PANEL = 250;

  private Board currentBoard;
  private Direction currentDir = EAST;

  private final int b_height;
  private final int b_width;
  private final Timer timer;
  private final Random rng;

  public Game(int b_height, int b_width) {
    this(b_height, b_width, new Random());
  }

  public Game(int b_height, int b_width, int seed) {
    this(b_height, b_width, new Random(seed));
  }

  public Game(int b_height, int b_width, Random rng) {
    this.b_height = b_height;
    this.b_width = b_width;

    this.timer = new Timer(DELAY, this);
    this.rng = rng;

    initGame();
    repaint();
  }

  public Game(int b_height, int b_width, Timer timer, Random rng) {
    this.b_height = b_height;
    this.b_width = b_width;

    timer.addActionListener(this);
    this.timer = timer;
    this.rng = rng;

    initGame();
    repaint();
  }

  public int getScore() {
    return currentBoard.getScore();
  }

  public int getMoves() {
    return currentBoard.getMoves();
  }

  public void initGame() {
    addKeyListener(new SnakeAdapter());
    setBackground(Color.BLACK);
    setFocusable(true);

    setPreferredSize(new Dimension(
        b_width * BOX_SIZE + 2 * BUFFER + SIDE_PANEL,
        b_height * BOX_SIZE + 2 * BUFFER));
  }

  public void startNewGame() {
    currentDir = EAST;
    currentBoard = Board.newStartBoard(b_height, b_width, rng);
    repaint();

    timer.restart();
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (!timer.isRunning()) {
      startScreen(g);
    }
    else {
      drawScoreAndMoves(g);
      if (currentBoard.isLive()) {
        drawBoard(g);
      }
      else {
        gameOver(g);
      }
    }

    Graphics2D g2 = (Graphics2D) g;
    g2.setStroke(new BasicStroke(5));
    g2.setColor(Color.WHITE);
    g2.drawRect(BUFFER - 5, BUFFER - 5,
        b_width * BOX_SIZE + 10, b_height * BOX_SIZE + 10);
  }

  private void drawBoard(Graphics g) {
    Point food = currentBoard.getFood();
    Snake snake = currentBoard.getSnake();

    drawCircleInGame(food, Color.RED, g);
    drawSquareInGame(snake.getHead(), Color.BLUE, g);
    snake.getTail().forEach(p -> drawSquareInGame(p, Color.GREEN, g));

    Toolkit.getDefaultToolkit().sync();
  }

  private void drawCircleInGame(Point p, Color color, Graphics g) {
    g.setColor(color);
    g.fillOval(p.x * BOX_SIZE + BUFFER, p.y * BOX_SIZE + BUFFER, BOX_SIZE, BOX_SIZE);
  }

  private void drawSquareInGame(Point p, Color color, Graphics g) {
    g.setColor(color);
    g.fillRect(p.x * BOX_SIZE + BUFFER, p.y * BOX_SIZE + BUFFER, BOX_SIZE, BOX_SIZE);

    g.setColor(Color.BLACK);
    g.drawRect(p.x * BOX_SIZE + BUFFER, p.y * BOX_SIZE + BUFFER, BOX_SIZE, BOX_SIZE);

  }

  private void startScreen(Graphics g) {
    int size = BOX_SIZE + 4;
    drawTextInGame("Snakevolution!",size, b_height * BOX_SIZE / 2 + BUFFER - size / 2, g);
    drawTextInGame("SPACE to start", BOX_SIZE, b_height * BOX_SIZE / 2 + BUFFER + size / 2, g);
  }

  private void gameOver(Graphics g) {
    int size = BOX_SIZE + 4;
    drawTextInGame("Game Over", size, b_height * BOX_SIZE / 2 + BUFFER - size / 2, g);
    drawTextInGame("SPACE to try again",BOX_SIZE, b_height * BOX_SIZE / 2 + BUFFER + size / 2, g);
    drawScoreAndMoves(g);
    timer.stop();
  }

  private void drawTextInGame(String text, int size, int y, Graphics g) {
    Font small = new Font("Helvetica", Font.BOLD, size);
    FontMetrics metr = getFontMetrics(small);

    g.setColor(Color.white);
    g.setFont(small);
    g.drawString(text, (b_width * BOX_SIZE - metr.stringWidth(text) ) / 2 + BUFFER, y);
  }

  private void drawScoreAndMoves(Graphics g) {
    int size = BOX_SIZE + 4;
    drawTextInSidePanel("Score: " + getScore(), size,(b_height * BOX_SIZE + 2 * BUFFER) / 2 - size / 2, g);
    drawTextInSidePanel("Moves: " + getMoves(), size,(b_height * BOX_SIZE + 2 * BUFFER) / 2 + size / 2, g);
  }

  private void drawTextInSidePanel(String text, int size, int y, Graphics g) {
    Font small = new Font("Helvetica", Font.BOLD, size);
    FontMetrics metr = getFontMetrics(small);

    g.setColor(Color.white);
    g.setFont(small);
    g.drawString(text, (SIDE_PANEL - metr.stringWidth(text)) / 2 + b_width * BOX_SIZE + 2 * BUFFER, y);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (currentBoard.isLive()) {
      currentBoard = currentBoard.updateBoard(currentDir);
    }

    repaint();
  }

  private class SnakeAdapter extends KeyAdapter {

    private Direction currentSnakeDir() {
      return currentBoard.getSnake().getDir();
    }

    @Override
    public void keyPressed(KeyEvent e) {
      int key = e.getKeyCode();

      if (key == KeyEvent.VK_LEFT && !currentSnakeDir().equals(EAST)) {
        currentDir = WEST;
      }
      else if (key == KeyEvent.VK_RIGHT && !currentSnakeDir().equals(WEST)) {
        currentDir = EAST;
      }
      else if (key == KeyEvent.VK_UP && !currentSnakeDir().equals(SOUTH)) {
        currentDir = NORTH;
      }
      else if (key == KeyEvent.VK_DOWN && !currentSnakeDir().equals(NORTH)) {
        currentDir = SOUTH;
      }
      else if (key == KeyEvent.VK_SPACE && !timer.isRunning()) {
        startNewGame();
      }
    }
  }
}
