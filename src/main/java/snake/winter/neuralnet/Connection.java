package snake.winter.neuralnet;

import io.vavr.Tuple3;
import io.vavr.Tuple4;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.json.JSONObject;

import java.util.function.Function;

public class Connection {
  private final Node source;
  private final Node destination;
  private final Node gater;
  private double weight;

  private Connection(Node s, Node t, Node g, double weight) {
    this.source = s;
    this.destination = t;
    this.gater = g;
    this.weight = weight;
  }

  public static Connection connect(Tuple3<Node, Node, Double> cxn) {
    return connect(cxn._1, cxn._2, cxn._3);
  }

  public static Connection connect(Node source, Node destination, double weight) {
    return connect(source, destination, Node.ghostNode(), weight);
  }

  public static Connection connect(Tuple4<Node, Node, Node, Double> cxn) {
    return connect(cxn._1, cxn._2, cxn._3, cxn._4);
  }

  public static Connection connect(Node source, Node destination, Node gater, double weight) {
    return new Connection(source, destination, gater, weight);
  }

  public Node source() {
    return source;
  }

  public Node destination() {
    return destination;
  }

  public Node gater() {
    return gater;
  }

  public double weight() {
    return weight;
  }

  @SafeVarargs
  public static List<Connection> connectAll(Tuple3<Node, Node, Double>... cxns) {
    return List.of(cxns).map(Connection::connect);
  }

  @SafeVarargs
  public static List<Connection> connectAll(Tuple4<Node, Node, Node, Double>... cxns) {
    return List.of(cxns).map(Connection::connect);
  }

  public JSONObject toJSON() {
    return toJSON(Node::toString);
  }

  public JSONObject toJSON(Function<Node, String> keyExtractor) {
    return toJSON(keyExtractor.apply(source), keyExtractor.apply(destination), keyExtractor.apply(gater));
  }

  public JSONObject toJSON(String sourceKey, String destinationKey, String gaterKey) {
    return new JSONObject(HashMap.of(
        "source", sourceKey,
        "destination", destinationKey,
        "gater", gaterKey,
        "weight", weight).toJavaMap());
  }

  private static Option<String> safeJSONGet(JSONObject json, String key) {
    return Try.of(() -> json.get(key)).toOption().map(Object::toString);
  }

  public static Option<Connection> fromJSON(JSONObject json) {
    return fromJSON(json, s -> Try.of(() -> new JSONObject(s)).toOption().flatMap(Node::fromJSON));
  }

  public static Option<Connection> fromJSON(JSONObject json, Function<String, Option<Node>> keyConverter) {
    Option<Node> oSource = safeJSONGet(json, "source").flatMap(keyConverter);
    Option<Node> oDest = safeJSONGet(json, "destination").flatMap(keyConverter);
    Option<Node> oGater = safeJSONGet(json, "gater").flatMap(keyConverter)
                              .map(g -> g.equals(Node.ghostNode()) ? Node.ghostNode() : g);

    return oSource.flatMap(s -> oDest.flatMap(t -> oGater.flatMap(g -> fromJSON(json, s, t, g))));
  }

  public static Option<Connection> fromJSON(JSONObject json, Node source, Node destination, Node gater) {
    Option<Double> oWeight = Try.of(() -> Double.parseDouble(safeJSONGet(json, "weight").getOrElse(""))).toOption();

    return oWeight.map(w -> connect(source, destination, gater, w));
  }

  @Override
  public String toString() {
    return toJSON().toString(1);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Connection)) {
      return false;
    }

    Connection c = (Connection) obj;
    return weight - c.weight < 1e-6 && source.equals(c.source) && destination.equals(c.destination) && gater.equals(c.gater);
  }
}
