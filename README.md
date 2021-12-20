## Flink4s

Scala 3.x wrapper for Apache Flink. Scala is not a priority for the core project, and thus support for the latest versions of Scala is missing.
This projects attempts to leverage the upcoming Scala-free 1.15 release to provide a viable wrapper for Scala 3.x.

### Usage

Use it as you would use `flink-streaming-scala`. Most methods are identical

```scala
import com.ariskk.flink4s.StreamExecutionEnvironment

case class Counter(id: String, count: Int)
object Counter {
  implicit val typeInfo = TypeInformation.of(classOf[Counter])
}

val items = (1 to 1000).map(x => s"item-${x % 10}")

val stream = StreamExecutionEnvironment.fromCollection(items)
  .map(x => Counter(x, 1))
  .keyBy(_.id)
  .reduce((acc, v) => acc.copy(counter = acc.count + v.count))
```

Or if you want to get fancy:

```scala
import cats.Semigroup

implicit val semigroup = new Semigroup[Counter] {
  override def combine(x: Counter, y: Counter) = Counter(x.id, x.value + y.value)
}

val stream = StreamExecutionEnvironment.fromCollection(items)
  .map(x => Counter(x, 1))
  .keyBy(_.id)
  .combine
```

