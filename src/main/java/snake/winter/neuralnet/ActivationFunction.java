package snake.winter.neuralnet;

import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.json.JSONObject;

import java.util.function.Function;

public class ActivationFunction {

  private static final Map<String, ActivationFunction> FUNCTION_MAP =
      List.of(
          fxn("identity", x -> x),
          fxn("binary step", x -> (x >= 0) ? 1.0 : 0.0),
          fxn("logistic", x -> 1 / (1 + Math.exp(-1 * x))),
          fxn("tanh", Math::tanh),
          fxn("softsign", x -> x / (1 + Math.abs(x))),
          fxn("relu", x -> Math.max(0, x)),
          fxn("sinusoid", Math::sin),
          fxn("bent identity", x -> (Math.sqrt(Math.pow(x, 2) + 1) - 1) / 2 + x),
          fxn("guassian", x -> Math.exp(-1 * Math.pow(x, 2))),
          fxn("arctan", Math::atan),
          fxn("softplus", x -> Math.log(1 + Math.exp(x))))
          .toMap(af -> af.name, af -> af);

  public static final List<String> PRESET_FUNCTION_NAMES = FUNCTION_MAP.keySet().toList();

  private final String name;
  private final Function<Double, Double> f;

  private ActivationFunction(String name, Function<Double, Double> f) {
    this.name = name;
    this.f = f;
  }

  public static ActivationFunction fxn(String name, Function<Double, Double> f) {
    return new ActivationFunction(name, f);
  }

  /**
   * Returns corresponding activation functions for correct names.
   */
  public static Option<ActivationFunction> get(String name) {
    return FUNCTION_MAP.get(name).orElse(() -> Try.of(() -> Double.parseDouble(name)).toOption()
                                 .map(ActivationFunction::constant));
  }

  public static ActivationFunction constant(double c) {
    return fxn(Double.toString(c), x -> c);
  }

  public String getName() {
    return name;
  }

  public Double apply(double x) {
    return f.apply(x);
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof  ActivationFunction)) {
      return false;
    }

    ActivationFunction af = (ActivationFunction) obj;
    return name.equals(af.name);
  }

  @Override
  public int hashCode() {
    return toString().hashCode();
  }
}
