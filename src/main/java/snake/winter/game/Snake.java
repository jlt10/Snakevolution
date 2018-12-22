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

  public Integer getLength() {
    return 1 + tail.length();
  }

  public static Snake snake(Point head, int length, Direction dir) {
    List<Point> tail = List.range(1, length).take(length)
                           .map(i -> head.add(complement(dir).getDir().mult(i)));
    return new Snake(head, tail, dir);
  }

  public static Option<Snake> snake(Point head, List<Point> tail, Direction dir) {
    return (tail.distinct().equals(tail) && head.add(complement(dir)).equals(tail.get(0)))
               ? some(new Snake(head, tail, dir))
               : none();
  }

  public Option<Snake> step(Direction d) {
    Point newHead = head.add(d);
    return tail.contains(newHead)
               ? none()
               : snake(newHead, tail.insert(0, head).removeLast(x -> true), d);
  }

  public Option<Snake> step(Direction... dirs) {
    return List.of(dirs).foldLeft(some(this), (optS, d) -> optS.flatMap(s -> s.step(d)));
  }

  public Option<Snake> grow(Direction d) {
    return new Snake(head, tail.append(tail.get(tail.length() - 1)), d).step(d);
  }

  @Override
  public String toString() {
    return "Snake( head: " + head + ", tail: " + tail + ")";
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
