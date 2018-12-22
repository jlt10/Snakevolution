package snake.winter.game;

import org.junit.jupiter.api.Test;
import org.quicktheories.core.Gen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.quicktheories.QuickTheory.qt;
import static org.quicktheories.generators.SourceDSL.integers;
import static snake.winter.game.Point.Direction.*;
import static snake.winter.game.Point.point;

public class PointTest {
  private Gen<Point> points() {
    return integers().all().zip(integers().all(), Point::point);
  }

  @Test
  public void arithmeticTests() {
    qt().forAll(points(), points()).checkAssert(
        (p1, p2) -> assertEquals(p1.add(p2), p2.add(p1)));

    qt().forAll(integers().all(), integers().all(), points()).checkAssert(
        (x, y, p) -> assertEquals(p.add(x, y), p.add(point(x, y))));

    qt().forAll(integers().all(), points()).checkAssert(
        (c, p) -> assertEquals(point(p.x * c, p.y * c), p.mult(c)));
  }

  @Test
  public void directionTests() {
    assertEquals(point(0, -1), NORTH.getDir());
    assertEquals(point(0, 1), SOUTH.getDir());
    assertEquals(point(-1, 0), WEST.getDir());
    assertEquals(point(1, 0), EAST.getDir());

    qt().forAll(points()).checkAssert(
        p -> {
          assertEquals(p, p.add(NORTH).add(SOUTH));
          assertEquals(p, p.add(WEST).add(EAST));
        });
  }
}
