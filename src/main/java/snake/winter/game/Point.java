package snake.winter.game;

import io.vavr.control.Option;

import java.util.function.BiFunction;
import java.util.function.Function;

import static io.vavr.control.Option.none;
import static io.vavr.control.Option.some;

public class Point {
  public int x;
  public int y;

  public enum Direction {
    NORTH(point(0, -1)), SOUTH(point(0, 1)),
    EAST(point(1, 0)), WEST(point(-1, 0));

    private Point dir;

    public Point getDir() {
      return dir;
    }

    Direction(Point d) {
      this.dir = d;
    }

    public static Direction complement(Direction d) {
      switch (d) {
        case NORTH:
          return SOUTH;
        case EAST:
          return WEST;
        case WEST:
          return EAST;
        case SOUTH:
          return NORTH;
      }
      throw new RuntimeException("invalid input direction");
    }

    public static Option<Direction> fromPoint(Point p) {
      if (p.equals(NORTH.dir)) {
        return some(NORTH);
      }
      else if (p.equals(SOUTH.dir)) {
        return some(SOUTH);
      }
      else if (p.equals(EAST.dir)) {
        return some(EAST);
      }
      else if (p.equals(WEST.dir)) {
        return some(WEST);
      }
      return none();
    }
  }

  // External classes shouldn't use this constructor.
  private Point(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public static Point point(int x, int y) {
    return new Point(x, y);
  }

  public Point add(int dx, int dy) {
    return point(x + dx, y + dy);
  }

  public Point add(Point p) {
    return add(p.x, p.y);
  }

  public Point add(Direction d) {
    return add(d.getDir());
  }

  public Point mult(int c) {
    return point(x * c, y * c);
  }

  public Point map(Function<Integer, Integer> f) {
    return point(f.apply(x), f.apply(y));
  }

  public int extract(BiFunction<Integer, Integer, Integer> f) {
    return f.apply(x, y);
  }

  public double distance(Point p) {
    return Math.sqrt(Math.pow(x - p.x, 2) + Math.pow(y - p.y, 2));
  }

  public int squareDistance(Point p) {
    return add(p.mult(-1)).map(Math::abs).extract((x, y) -> x + y);
  }

  public Option<Direction> directionTo(Point p) {
    return Direction.fromPoint(p.add(this.mult(-1)));
  }

  @Override
  public String toString() {
    return "p(" + x + ", " + y + ")";
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Point)) {
      return false;
    }
    Point p = (Point) obj;
    return x == p.x && y == p.y;
  }

  @Override
  public int hashCode() {
    return toString().hashCode();
  }
}
