package snake.winter.game;

import io.vavr.control.Option;

import java.util.Random;

public class Board {
  private final Random defaultRandom = new Random();

  private final int height;
  private final int width;
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

  public Board incrementScore() {
    return new Board(height, width, food, snake, score + 1, moves, live);
  }

  public Board incrementMoves() {
    return new Board(height, width, food, snake, score, moves + 1, live);
  }

  public Board killBoard() {
    return new Board(height, width, food, snake, score, moves, false);
  }

  public Board newStartBoard() {
    return newStartBoard(defaultRandom);
  }

  public Board newStartBoard(int seed) {
    return newStartBoard(new Random(seed));
  }

  public Board newStartBoard(Random rng) {
    throw new RuntimeException("not implemented yet");
  }

  public Option<Board> newStartBoard(Snake snake) {
    return newStartBoard(snake, defaultRandom);
  }

  public Option<Board> newStartBoard(Snake snake, int seed) {
    return newStartBoard(snake, new Random(seed));
  }

  public Option<Board> newStartBoard(Snake snake, Random rng) {
    throw new RuntimeException("not implemented yet");
  }

  public Board newFood() {
    return newFood(defaultRandom);
  }

  public Board newFood(int seed) {
    return newFood(new Random(seed));
  }

  public Board newFood(Random rng) {
    throw new RuntimeException("not implemented yet");
  }

  public Board newSnake() {
    return newSnake(defaultRandom);
  }

  public Board newSnake(int seed) {
    return newSnake(new Random(seed));
  }

  public Board newSnake(Random rng) {
    throw new RuntimeException("not implemented yet");
  }

  public Board updateBoard(Point.Direction dir) {
    return updateBoard(dir, defaultRandom);
  }

  public Board updateBoard(Point.Direction dir, int seed) {
    return updateBoard(dir, new Random(seed));
  }

  public Board updateBoard(Point.Direction dir, Random rng) {
    throw new RuntimeException("not implemented yet");
  }

  @Override
  public String toString() {
    return super.toString();
  }
}
