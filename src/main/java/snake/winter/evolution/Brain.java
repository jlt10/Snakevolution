package snake.winter.evolution;

import io.vavr.Tuple2;
import io.vavr.collection.List;
import snake.winter.game.Board;
import snake.winter.game.Point;
import snake.winter.game.Point.Direction;
import snake.winter.game.Snake;

import java.util.Random;
import java.util.function.BiFunction;

import static snake.winter.game.Point.point;

public class Brain {
  private static final Random defaultRandom = new Random();

  private List<Double> calculateInputs(Board b) {
    throw new RuntimeException("not implemented yet!");
  }

  public Direction nextMove(Board b) {
    return nextMove(b, defaultRandom);
  }

  public Direction nextMove(Board b, int seed) {
    return nextMove(b, new Random(seed));
  }

  public Direction nextMove(Board b, Random rng) {
    List<Double> outputs = List.fill(3, rng::nextDouble);
    return nextMoveHelper(b.getSnake(), outputs);
  }

  private Direction nextMoveHelper(Snake s, List<Double> outputs) {
    switch (outputs.indexOf(outputs.max().get())) {
      case 0: // go straight
        return s.getDir();
      case 1: // turn left
        return transformDir(s.getDir(), (a, b) -> point(b, -1 * a));
      case 2: // turn right
        return transformDir(s.getDir(), (a, b) -> point(-1 * b, a));
      default:
        throw new RuntimeException("invalid outputs");
    }
  }

  private Direction transformDir(Point.Direction d, BiFunction<Integer, Integer, Point> f) {
    return Direction.fromPoint(d.asPoint().extract(f))
               .getOrElseThrow(() -> {
                 throw new RuntimeException("invalid transformation");
               });
  }
}
