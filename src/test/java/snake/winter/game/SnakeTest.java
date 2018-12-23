package snake.winter.game;

import io.vavr.collection.List;
import io.vavr.control.Option;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static snake.winter.game.Point.Direction.*;
import static snake.winter.game.Point.point;
import static snake.winter.game.Snake.pointSnake;
import static snake.winter.game.Snake.snake;

public class SnakeTest {

  @Test
  public void hatchTest() {
    // Test that list snake creation operates the same as length snake creation
    Snake snake1 = snake(point(0,0), 3, EAST);
    Snake snake2 = snake(point(0, 0), List.of(point(-1, 0), point(-2, 0))).get();
    assertEquals(snake2, snake1);

    // Test that invalid snakes result in none
    Option<Snake> backwardsTail = snake(point(0, 0),
        List.of(point(-2, 0), point(-1, 0)));
    Option<Snake> middleHead = snake(point(0, 0),
        List.of(point(1, 0), point(-1, 0)));
    Option<Snake> disconnectedTail = snake(point(0, 0),
        List.of(point(-1, 0), point(-2, 0), point(-2, 5)));
    assertFalse(backwardsTail.isDefined());
    assertFalse(middleHead.isDefined());
    assertFalse(disconnectedTail.isDefined());

    Option<Snake> headInTail = snake(point(0, 0),
        List.of(point(0,0), point(-1, 0), point(-2, 0)));
    Option<Snake> nonDistinctTail = snake(point(0, 0),
        List.of(point(-1,0), point(-1, 0), point(-2, 0)));
    assertFalse(headInTail.isDefined());
    assertFalse(nonDistinctTail.isDefined());
  }

  @Test
  public void stepTest() {
    Snake snake = snake(point(0,0), 4, EAST);
    assertEquals(snake(point(0, -1), List.of(point(0,0), point(-1,0), point(-2, 0))).get(),
        snake.step(NORTH).get());
    assertEquals(snake(point(0, 1), List.of(point(0,0), point(-1,0), point(-2, 0))).get(),
        snake.step(SOUTH).get());
    assertEquals(snake(point(1, 0), List.of(point(0,0), point(-1,0), point(-2, 0))).get(),
        snake.step(EAST).get());

    // Test that single steps and multiple steps both work as expected
    assertEquals(snake.step(NORTH).get().step(EAST), snake.step(NORTH, EAST));
    assertEquals(snake(point(0, 0), List.of(point(-1, 0), point(-1, 1), point(-1, 0))),
        snake.step(NORTH, WEST, SOUTH, EAST));

    // Test that order maters
    assertNotEquals(snake.step(NORTH).get().step(EAST), snake.step(EAST, NORTH));

    // Test that snakes cannot step onto themselves
    assertFalse(snake.step(WEST).isDefined());
    assertFalse(snake(point(0,0), 5, EAST).step(NORTH, WEST, SOUTH).isDefined());
  }

  @Test
  public void growTest() {
    // Test that snakes grow as expected
    Snake snake1 = snake(point(-1,0), 3, EAST);
    Snake snake2 = snake(point(0,0), 4, EAST);
    Snake snake3 = pointSnake(point(-2, 0), EAST);
    assertEquals(snake2, snake1.grow(EAST).get());

    assertEquals(snake(point(0,1), snake2.toList()), snake2.grow(SOUTH));

    // Test that snakes cannot grow onto themselves
    assertFalse(snake1.grow(WEST).isDefined());
  }
}
