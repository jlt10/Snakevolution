package snake.winter.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static snake.winter.game.Point.Direction.*;

public class BoardTest {
  private static final int testSeed = 121345;

  @Test
  public void snakeMovementTests() {
    Board startBoard = Board.newStartBoard(7, 11, testSeed);
    assertEquals(0, startBoard.getScore());
    assertEquals(0, startBoard.getMoves());
    assertEquals(3, startBoard.getSnake().length());
    assertTrue(startBoard.isLive());

    // First food should be at (4, 0)
    Board eat1Board = startBoard.updateBoard(testSeed, NORTH, NORTH, NORTH, WEST);
    assertEquals(1, eat1Board.getScore());
    assertEquals(4, eat1Board.getMoves());
    assertEquals(4, eat1Board.getSnake().length());
    assertTrue(eat1Board.isLive());

    // Second food should be at (3, 6)
    Board eat2Board = eat1Board.updateBoard(testSeed, SOUTH, SOUTH, SOUTH, SOUTH, SOUTH, SOUTH, WEST);
    assertEquals(2, eat2Board.getScore());
    assertEquals(11, eat2Board.getMoves());
    assertEquals(5, eat2Board.getSnake().length());
    assertTrue(eat2Board.isLive());
  }

  @Test
  public void deadBoardTests() {
    // Test that snakes die as expected
    Board startBoard = Board.newStartBoard(7, 11, testSeed);
    Board eat1Board = startBoard.updateBoard(testSeed, NORTH, NORTH, NORTH, WEST);
    Board wallDeath = eat1Board.updateBoard(NORTH, testSeed);
    assertFalse(wallDeath.isLive());

    Board eat2Board = eat1Board.updateBoard(testSeed, SOUTH, SOUTH, SOUTH, SOUTH, SOUTH, SOUTH, WEST);
    Board loopDeath = eat2Board.updateBoard(testSeed, NORTH, EAST);
    assertFalse(loopDeath.isLive());

    // Test that dead boards do not update after death
    assertEquals(wallDeath, wallDeath.updateBoard(testSeed, SOUTH, SOUTH, WEST, SOUTH));
    assertEquals(loopDeath, loopDeath.updateBoard(testSeed, NORTH, NORTH, WEST, SOUTH));
  }
}
