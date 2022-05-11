## Flink4s

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://github.com/ariskk/flink4s/blob/main/LICENSE)
![CI](https://github.com/ariskk/flink4s/workflows/CI/badge.svg)

Scala 2.13 and 3.x wrapper for Apache Flink. Scala is not a priority for the core project, and thus support for the latest versions of Scala is missing.
This project attempts to leverage the Scala-free 1.15 release to provide a viable wrapper for Scala 2.13 and 3.x.

### Usage

In your `build.sbt`, add

```scala
libraryDependencies ++= Seq(
  "com.ariskk" %% "flink4s" % "1.15.2"
)
```

Note: Major and minor versions follow Flink releases. Patches are independent.

Use it as you would use `flink-streaming-scala`. Most methods are identical

```scala
import com.ariskk.flink4s.StreamExecutionEnvironment

final case class Counter(id: String, count: Int)
object Counter:
  given typeInfo: TypeInformation[Counter] = TypeInformation.of(classOf[Counter])

val items = (1 to 1000).map(x => s"item-${x % 10}")

val stream = StreamExecutionEnvironment.fromCollection(items)
  .map(x => Counter(x, 1))
  .keyBy(_.id)
  .reduce((acc, v) => acc.copy(counter = acc.count + v.count))
```

Or if you want to get fancy:

```scala
import cats.Semigroup

given semigroup: Semigroup[Counter] with
  def combine(x: Counter, y: Counter) = Counter(x.id, x.value + y.value)

val stream = StreamExecutionEnvironment.fromCollection(items)
  .map(x => Counter(x, 1))
  .keyBy(_.id)
  .combine
```

### Caveats

There is a number of features found in `flink-streaming-scala` that is (intentionally) missing:
- No automatic derivation of `TypeInformation` instances via macros. Compile times scale very poorly as codebase sizes increase.
This means that some custom serializers Flink provides won't be used. For more context, check [this](https://medium.com/drivetribe-engineering/towards-achieving-a-10x-compile-time-improvement-in-a-flink-codebase-a69596edcb50).
- No closure cleaner. It is very hard to implement and generally imperfect. If all stream processing logic lives in `object`s, then there is no need for a closure cleaner in the first place.


### Contributing

I would very much love contributions. The lowest hanging fruit would be opening a PR with methods from `flink-streaming-scala` that are currently missing. 
Ideally, this repo should move to its own org at some point.
