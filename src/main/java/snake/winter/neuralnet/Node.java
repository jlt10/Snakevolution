package snake.winter.neuralnet;

import io.vavr.collection.HashMap;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.json.JSONObject;

import java.util.function.Function;

import static io.vavr.control.Option.none;
import static io.vavr.control.Option.some;

public class Node {
  public enum NType{
    INPUT,
    HIDDEN,
    OUTPUT,
    CONSTANT; // for default gater node

    public static Option<NType> fromString(String type) {
      switch (type) {
        case "INPUT":
          return some(INPUT);
        case "OUTPUT":
          return some(OUTPUT);
        case "HIDDEN":
          return some(HIDDEN);
        case "CONSTANT":
          return some(CONSTANT);
        default:
          return none();
      }
    }
  }
  // default gater node (no effect on connection) no bias and always returns 1.0 when activated
  private static final Node SINGLETON = node(ActivationFunction.constant(1.0), 0, NType.CONSTANT);

  private final ActivationFunction squash;
  private final double bias;
  private final NType type;

  private Node(ActivationFunction squash, double bias, NType type) {
    this.squash = squash;
    this.bias = bias;
    this.type = type;
  }

  public static Node ghostNode() {
    return SINGLETON;
  }

  public static Node node(ActivationFunction squash, double bias, NType type) {
    return new Node(squash, bias, type);
  }

  public static Option<Node> node(String fName, double bias, NType type) {
    return ActivationFunction.get(fName).map(af -> node(af, bias, type));
  }

  public double activate(double input) {
    return squash.apply(input + bias);
  }

  public ActivationFunction getSquash() {
    return squash;
  }

  public String getSquashName() {
    return squash.getName();
  }

  public double getBias() {
    return bias;
  }

  public NType getType() {
    return type;
  }

  public boolean isType(NType type) {
    return this.type.equals(type);
  }

  public <T> T matchType(NType type, Function<? super Node, ? extends T> matchFunc, Function<? super Node, ? extends T> otherFunc) {
    return isType(type) ? matchFunc.apply(this) : otherFunc.apply(this);
  }

  public <T> T match(Function<? super Node, ? extends T> inputFunc,
                     Function<? super Node, ? extends T> hiddenFunc,
                     Function<? super Node, ? extends T> outputFunc) {
    switch (type) {
      case INPUT:
        return inputFunc.apply(this);
      case HIDDEN:
        return hiddenFunc.apply(this);
      case OUTPUT:
        return outputFunc.apply(this);
      default:
        throw new RuntimeException("Node has invalid type: " + this.type);
    }
  }

  public <T> T match(Function<? super Node, ? extends T> inputFunc,
                     Function<? super Node, ? extends T> hiddenFunc,
                     Function<? super Node, ? extends T> outputFunc,
                     Function<? super Node, ? extends T> constantFunc) {
    switch (type) {
      case INPUT:
        return inputFunc.apply(this);
      case HIDDEN:
        return hiddenFunc.apply(this);
      case OUTPUT:
        return outputFunc.apply(this);
      case CONSTANT:
        return constantFunc.apply(this);
      default:
        throw new RuntimeException("You should never see this.");
    }
  }

  public JSONObject toJSON() {
    return new JSONObject(HashMap.of("squash", squash.toString(), "bias", bias, "type", type).toJavaMap());
  }

  public JSONObject toJSON(Function<Node, String> keyExtracter) {
    return toJSON(keyExtracter.apply(this));
  }

  public JSONObject toJSON(String key) {
    return toJSON().put("key", key);
  }

  public static Option<Node> fromJSON(JSONObject json) {
    String squash = Try.of(() -> json.get("squash").toString()).getOrElse("");
    String type = Try.of(() -> json.get("type").toString()).toOption().getOrElse("");
    Option<Double> oBias = Try.of(() -> Double.parseDouble(json.get("bias").toString())).toOption();

    return oBias.flatMap(b -> NType.fromString(type).flatMap(t -> node(squash, b, t)));
  }

  @Override
  public String toString() {
    return toJSON().toString(1);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Node)) {
      return false;
    }

    Node n = (Node) obj;
    return squash.equals(n.squash) && bias - n.bias < 1e-6 && type.equals(n.type);
  }

  @Override
  public int hashCode() {
    return toString().hashCode();
  }
}
