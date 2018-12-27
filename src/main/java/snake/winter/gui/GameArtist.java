package snake.winter.gui;

import io.vavr.Lazy;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.collection.Set;
import snake.winter.game.Board;
import snake.winter.game.Snake;
import snake.winter.game.Point;

import java.awt.*;

public class GameArtist {
  private static final Color defaultFoodColor = Color.RED;
  private static final Color defaultSnakeColor = Color.GREEN;

  private final int startX;
  private final int startY;
  private final int boardHeight;
  private final int boardWidth;
  private final int frameStroke;
  private final int boxSize;

  private final Color backgroundColor;
  private final Lazy<Integer> pixelHeight;
  private final Lazy<Integer> pixelWidth;

  public GameArtist(int x, int y, int bHeight, int bWidth, int frame, int boxSize, Color bg) {
    this.startX = x;
    this.startY = y;
    this.boardHeight = bHeight;
    this.boardWidth = bWidth;
    this.frameStroke = frame;
    this.boxSize = boxSize;

    this.backgroundColor = bg;
    this.pixelHeight = Lazy.of(() -> boardHeight * boxSize + 2 * frameStroke);
    this.pixelWidth = Lazy.of(() -> boardWidth * boxSize + 2 * frameStroke);
  }

  public int pixelHeight() {
    return pixelHeight.get();
  }

  public int pixelWidth() {
    return pixelWidth.get();
  }

  public void drawFrame(Graphics g) {
    Graphics2D g2 = (Graphics2D) g;
    g2.setStroke(new BasicStroke(frameStroke));
    g2.setColor(Color.WHITE);
    g2.drawRect(startX, startY, pixelWidth(), pixelHeight());
  }

  public void drawBoard(Board b, Graphics g) {
    drawBoard(b, defaultFoodColor, defaultSnakeColor, g);
  }

  public void drawBoard(Board b, Color foodColor, Color snakeColor, Graphics g) {
    drawSnake(b.getSnake(), snakeColor, g);
    drawFood(b.getFood(), foodColor, g);

    Toolkit.getDefaultToolkit().sync();
  }

  public void drawBoards(List<Board> boards, Graphics g) {
    drawBoards(boards, defaultFoodColor, defaultSnakeColor, g);
  }

  public void drawBoards(List<Board> boards, Color foodColor, Color snakeColor, Graphics g) {
    drawSnakes(boards.map(Board::getSnake), snakeColor, g);
    drawFeast(boards.map(Board::getFood), foodColor, g);

    Toolkit.getDefaultToolkit().sync();
  }

  public void drawFood(Point f, Color c, Graphics g) {
    drawCircle(f, c, g);
  }

  public void drawFeast(List<Point> f, Color c, Graphics g) {
    drawCircles(f.toSet().toList(), c, g);
  }

  public void drawSnake(Snake s, Color c, Graphics g) {
    drawSquares(s.toList(), c, g);
  }

  public void drawSnakes(List<Snake> snakes, Color c, Graphics g) {
    List<Point> allPoints = HashSet.ofAll(snakes.flatMap(Snake::toList)).toList();
    drawSquares(allPoints, c, g);
  }

  private void drawCircle(Point p, Color c, Graphics g) {
    g.setColor(c);
    g.fillOval(p.x * boxSize + startX + frameStroke, p.y * boxSize + startY + frameStroke, boxSize, boxSize);
  }

  private void drawSquare(Point p, Color c, Graphics g) {
    g.setColor(c);
    g.fillRect(p.x * boxSize + startX + frameStroke, p.y * boxSize + startY + frameStroke, boxSize, boxSize);

    g.setColor(backgroundColor);
    g.drawRect(p.x * boxSize + startX + frameStroke, p.y * boxSize + startY + frameStroke, boxSize, boxSize);

  }

  private void drawCircles(List<Point> points, Color c, Graphics g) {
    points.forEach(p -> drawCircle(p, c, g));
  }

  private void drawSquares(List<Point> points, Color c, Graphics g) {
    points.forEach(p -> drawSquare(p, c, g));
  }

  public void drawTextInCenter(String text, int size, int y, Graphics g) {
    Font small = new Font("Helvetica", Font.BOLD, Math.max(12, size));
    FontMetrics metr = g.getFontMetrics(small);

    g.setColor(Color.white);
    g.setFont(small);
    g.drawString(text, (pixelWidth() - metr.stringWidth(text)) / 2 + startX, y + startY);
  }
}
