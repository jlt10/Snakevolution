package snake.winter.game;

import io.vavr.collection.List;
import io.vavr.collection.Stream;
import io.vavr.control.Option;
import snake.winter.game.Point.Direction;

import java.util.Random;

import static snake.winter.game.Point.Direction.EAST;
import static snake.winter.game.Point.point;
import static snake.winter.game.Snake.snake;

public class Board {
  private static final Random defaultRandom = new Random();

  public final int height;
  public final int width;
  private final Point food;
  private final Snake snake;
  private final int score;
  private final int moves;
  private final boolean live;

  private Board(int height, int width, Point food, Snake snake, int score, int moves, boolean live) {
    this.height = height;
    this.width = width;
    this.food = food;
    this.snake = snake;
    this.score = score;
    this.moves = moves;
    this.live = live;
  }

  public Point getFood() {
    return food;
  }

  public Snake getSnake() {
    return snake;
  }

  public int getScore() {
    return score;
  }

  public int getMoves() {
    return moves;
  }

  public boolean isLive() {
    return live;
  }

  public Direction snakeDir() {
    return snake.getDir();
  }

  public Board incrementScore() {
    return new Board(height, width, food, snake, score + 1, moves, live);
  }

  public Board incrementMoves() {
    return new Board(height, width, food, snake, score, moves + 1, live);
  }

  public Board killBoard() {
    return new Board(height, width, food, snake, score, moves, false);
  }

  private Board updateSnake(Snake newSnake) {
    return new Board(height, width, food, newSnake, score, moves, live);
  }

  private Board updateFood(Point newFood) {
    return new Board(height, width, newFood, snake, score, moves, live);
  }

  public static Board newStartBoard(int height, int width) {
    return newStartBoard(height, width, defaultRandom);
  }

  public static Board newStartBoard(int height, int width, int seed) {
    return newStartBoard(height, width, new Random(seed));
  }

  public static Board newStartBoard(int height, int width, Random rng) {
    Point center = point(width / 2, height / 2);
    if (center.x < 1 || center.y < 1) {
      // board is too small
      throw new RuntimeException("board is too small");
    }
    else {
      // New board with 0 score, 0 moves and a snake of at most length 3 facing east
      Snake newSnake = snake(center, Math.min(3, center.x), EAST);
      return new Board(height, width, randomFoodHelper(height, width, newSnake, rng), newSnake,
          0, 0, true);
    }
  }

  private Point randomPoint(int lowX, int highX, int lowY, int highY, Random rng) {
    int x = rng.nextInt(highX - lowX + 1) + lowX;
    int y = rng.nextInt(highY - lowY + 1) + lowY;
    return point(x, y);
  }

  private static Point randomFoodHelper(int height, int width, Snake snake, Random rng) {
    List<Point> snakeList = snake.toList();
    Stream<Point> validPoints = Stream.range(0, width)
                                    .flatMap(x -> Stream.range(0, height)
                                                      .filter(y -> !snakeList.contains(point(x, y)))
                                                      .map(y -> point(x, y)));
    if (validPoints.length() == 0) {
      throw new RuntimeException("board is full, new food cannot be created");
    }
    return validPoints.get(rng.nextInt(validPoints.length()));
  }

  public Point newFood() {
    return newFood(defaultRandom);
  }

  public Point newFood(int seed) {
    return newFood(new Random(seed));
  }

  public Point newFood(Random rng) {
    return randomFoodHelper(height, width, snake, rng);
  }

  public Board updateBoard(Direction dir) {
    return updateBoard(dir, defaultRandom);
  }

  public Board updateBoard(Direction dir, int seed) {
    return updateBoard(dir, new Random(seed));
  }

  public Board updateBoard(Direction dir, Random rng) {
    if (!live) {
      return this;
    }

    Point newHead = snake.getHead().add(dir);
    Board newBoard;
    if (snake.getHead().add(dir).equals(food)) {
      Option<Snake> newSnake = snake.grow(dir);
      newBoard = newSnake.isDefined()
                     ? updateSnake(newSnake.get()).updateFood(newFood(rng)).incrementScore()
                     : killBoard();
    }
    else if (!(newHead.x >= width || newHead.x < 0 || newHead.y >= height || newHead.y < 0)) {
      Option<Snake> newSnake = snake.step(dir);
      newBoard = newSnake.isDefined()
                     ? updateSnake(newSnake.get())
                     : killBoard();
    }
    else {
      newBoard = killBoard();
    }
    return newBoard.incrementMoves();
  }

  public Board updateBoard(Direction... dirs) {
    return updateBoard(defaultRandom, dirs);
  }

  public Board updateBoard(int seed, Direction... dirs) {
    return updateBoard(new Random(seed), dirs);
  }

  public Board updateBoard(Random rng, Direction... dirs) {
    return List.of(dirs).foldLeft(this, (board, d) -> board.updateBoard(d, rng));
  }

  @Override
  public String toString() {
    return "Board(status: " + (live ? "live" : "dead") + "\n"
               + List.range(0, height)
                     .map(y -> List.range(0, width)
                                   .map(x -> {
                                     Point p = point(x,y);
                                     if (snake.contains(p)) {
                                       return (snake.getHead().equals(p)) ? "H" : "X";
                                     }
                                     else if (food.equals(p)) {
                                       return "O";
                                     }
                                     else {
                                       return ".";
                                     }})
                                   .foldLeft("\t", (row, s) -> row + s))
                      .foldLeft("", (row, s) -> row + s + "\n") +
               "\tScore: " + score + ", Moves: " + moves + "\n)";

  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Board)) {
      return false;
    }

    Board b = (Board) obj;
    return live == b.live && width == b.width && height == b.height
               && score == b.score && moves == b.moves
               && food.equals(b.food) && snake.equals(b.snake);
  }

  @Override
  public int hashCode() {
    return toString().hashCode();
  }
}
