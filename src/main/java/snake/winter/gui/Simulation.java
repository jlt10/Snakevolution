package snake.winter.gui;

import snake.winter.evolution.Brain;
import snake.winter.game.Board;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

import io.vavr.collection.List;

public class Simulation extends JPanel implements ActionListener {

  private static final int DELAY = 100;
  private static final int BOX_SIZE = 20;
  private static final int BUFFER = 20;
  private static final int SIDE_PANEL = 250;

  private List<Brain> generation;
  private List<Board> currentBoards;

  private final int b_height;
  private final int b_width;
  private final int numSnakes;
  private final Timer timer;
  private final Random rng;
  private final GameArtist artist;

  public Simulation(int b_height, int b_width, int numSnakes) {
    this(b_height, b_width, numSnakes, new Random());
  }

  public Simulation(int b_height, int b_width, int numSnakes, int seed) {
    this(b_height, b_width, numSnakes, new Random(seed));
  }

  public Simulation(int b_height, int b_width, int numSnakes, Random rng) {
    this.b_height = b_height;
    this.b_width = b_width;
    this.numSnakes = numSnakes;

    this.timer = new Timer(DELAY, this);
    this.rng = rng;
    this.artist = new GameArtist(BUFFER, BUFFER, b_height, b_width,
        5, BOX_SIZE, Color.BLACK);

    this.generation = List.fill(numSnakes, Brain::new);

    initSimulation();
    repaint();
  }

  public Simulation(int b_height, int b_width, int numSnakes, Timer timer, Random rng) {
    this.b_height = b_height;
    this.b_width = b_width;
    this.numSnakes = numSnakes;

    timer.addActionListener(this);
    this.timer = timer;
    this.rng = rng;
    this.artist = new GameArtist(BUFFER, BUFFER, b_height, b_width,
        5, BOX_SIZE, Color.BLACK);

    this.generation = List.fill(numSnakes, Brain::new);

    initSimulation();
    repaint();
  }

  public void initSimulation() {
    addKeyListener(new SimAdapter());
    setBackground(Color.BLACK);
    setFocusable(true);

    setPreferredSize(new Dimension(
        artist.pixelWidth() + 2 * BUFFER + SIDE_PANEL,
        artist.pixelHeight() + 2 * BUFFER));
  }

  public void startNewGame() {
    this.currentBoards = List.fill(numSnakes, () -> Board.newStartBoard(b_height, b_width, rng));
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
      if (!simulationLive()) {
        gameOver(g);

      }
      artist.drawBoards(currentBoards, g);
    }

    artist.drawFrame(g);
  }

  private void startScreen(Graphics g) {
    int size = BOX_SIZE + 4;

    artist.drawTextInCenter("Snakevolution!",size, artist.pixelHeight() / 2 - size / 2, g);
    artist.drawTextInCenter("SPACE to start", BOX_SIZE, artist.pixelHeight() / 2 + size / 2, g);
  }

  private void gameOver(Graphics g) {
    timer.stop();
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
    if (simulationLive()) {
      currentBoards = List.range(0, numSnakes).map(i -> {
        Board b = currentBoards.get(i);
        return b.updateBoard(rng, generation.get(i).nextMove(b));
      });
    }

    repaint();
  }

  public boolean simulationLive() {
    return !currentBoards.filter(Board::isLive).isEmpty();
  }
  private class SimAdapter extends KeyAdapter {

    @Override
    public void keyPressed(KeyEvent e) {
      int key = e.getKeyCode();

      if (key == KeyEvent.VK_SPACE && !timer.isRunning()) {
        startNewGame();
      }
    }
  }
}
