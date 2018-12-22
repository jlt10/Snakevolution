package snake.winter.game;

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
      switch(d) {
        case NORTH:
          return SOUTH;
        case EAST:
          return WEST;
        case WEST:
          return EAST;
        case SOUTH:
          return NORTH;
      }
      throw new RuntimeException("invalid direction");
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
