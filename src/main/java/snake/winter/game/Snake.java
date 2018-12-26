package snake.winter.game;

import io.vavr.collection.List;

import io.vavr.control.Option;
import snake.winter.game.Point.Direction;

import static io.vavr.control.Option.none;
import static io.vavr.control.Option.some;
import static snake.winter.game.Point.Direction.complement;

public class Snake {
  private Point head;
  private List<Point> tail;
  private Direction dir;

  private Snake(Point head, List<Point> tail, Direction dir) {
    this.head = head;
    this.tail = tail;
    this.dir = dir;
  }

  public Point getHead() {
    return head;
  }

  public List<Point> getTail() {
    return tail;
  }

  public Direction getDir() {
    return dir;
  }

  public int length() {
    return 1 + tail.length();
  }

  /**
   * Creates a straight snake facing the given direction with the given length.
   */
  public static Snake snake(Point head, int length, Direction dir) {
    List<Point> tail = List.range(1, length).take(length)
                           .map(i -> head.add(complement(dir).asPoint().mult(i)));
    return new Snake(head, tail, dir);
  }

  /**
   * Returns a snake with the given head and tail if inputs are valid.
   */
  public static Option<Snake> snake(Point head, List<Point> tail) {
    return tailHelper(tail) && !tail.contains(head)
               ? tail.get(0).directionTo(head).map(dir -> new Snake(head, tail, dir))
               : none();
  }

  public static Snake pointSnake(Point head, Direction dir) {
    return snake(head, 1, dir);
  }

  private static boolean tailHelper(List<Point> tail) {
    return tail.distinct().equals(tail)
               ? List.range(0, tail.length() - 1)
                     .foldLeft(true, (valid, i) -> valid && tail.get(i).squareDistance(tail.get(i + 1)) == 1)
               : false;
  }

  /**
   * Moves the snake one step in the given direction.
   */
  public Option<Snake> step(Direction d) {
    Point newHead = head.add(d);
    return tail.contains(newHead)
               ? none()
               : snake(newHead, tail.insert(0, head).removeLast(x -> true));
  }

  public Option<Snake> step(Direction... dirs) {
    return List.of(dirs).foldLeft(some(this), (optS, d) -> optS.flatMap(s -> s.step(d)));
  }

  public Option<Snake> grow(Direction d) {
    return new Snake(head, tail.append(tail.get(tail.length() - 1)), d).step(d);
  }

  public boolean contains(Point p) {
    return toList().contains(p);
  }

  public List<Point> toList() {
    return tail.insert(0, head);
  }

  @Override
  public String toString() {
    return "Snake(head: " + head + ", tail: " + tail + ", dir: " + dir + ")";
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Snake)) {
      return false;
    }
    Snake s = (Snake) obj;
    return s.head.equals(head) && s.tail.equals(tail) && s.dir.equals(dir);
  }

  @Override
  public int hashCode() {
    return toString().hashCode();
  }
}
