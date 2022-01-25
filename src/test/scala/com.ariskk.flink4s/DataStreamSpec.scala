package com.ariskk.flink4s

import scala.collection.mutable.{Buffer => MutableBuffer}

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.apache.flink.streaming.api.functions.sink.SinkFunction

import com.ariskk.flink4s.TypeInfo.{intTypeInfo, stringTypeInfo}

final class DataStreamSpec extends AnyFunSpec with Matchers:

  describe("DataStreamSpec") {
    it("should map data") {
      val env     = FlinkExecutor.newEnv(parallelism = 1)
      val range   = (1 to 5).toList
      val results = env.fromCollection(range).map(_ + 1).runAndCollect
      results should equal((2 to 6).toList)
    }

    it("should flatMap data") {
      val env   = FlinkExecutor.newEnv(parallelism = 1)
      val range = (1 to 10).toList
      val results =
        env.fromCollection(range).flatMap(x => Option.when(x > 5)(x)).runAndCollect
      results should equal((6 to 10).toList)
    }

    it("should filter data") {
      val env     = FlinkExecutor.newEnv(parallelism = 1)
      val range   = (1 to 10).toList
      val results = env.fromCollection(range).filter(_ > 5).runAndCollect
      results should equal((6 to 10).toList)
    }

    it("should filter out data") {
      val env     = FlinkExecutor.newEnv(parallelism = 1)
      val range   = (1 to 10).toList
      val results = env.fromCollection(range).filterNot(_ > 5).runAndCollect
      results should equal((1 to 5).toList)
    }

    it("should collect data") {
      val env     = FlinkExecutor.newEnv(parallelism = 1)
      val range   = (1 to 10).toList
      val results = env.fromCollection(range).collect { case x if x > 5 => s"$x" }.runAndCollect
      results should equal((6 to 10).map(_.toString).toList)
    }

    it("should union homogeneous streams") {
      val env     = FlinkExecutor.newEnv(parallelism = 1)
      val stream1 = env.fromCollection((1 to 10).toList)
      val stream2 = env.fromCollection((11 to 20).toList)
      val stream3 = env.fromCollection((21 to 30).toList)
      val results = stream1.union(stream2, stream3).runAndCollect
      results should contain theSameElementsAs ((1 to 30).toList)
    }

    it("should be able to accept sinks") {
      val env      = FlinkExecutor.newEnv(parallelism = 1)
      val elements = (1 to 10).toList
      val stream   = env.fromCollection(elements)
      stream.addSink(DataStreamSpec.intCollector)
      env.execute
      DataStreamSpec.values.toList should equal(elements)
    }
  }

end DataStreamSpec

object DataStreamSpec:
  val values: MutableBuffer[Int] = MutableBuffer()
  def intCollector = new SinkFunction[Int]:
    override def invoke(value: Int): Unit =
      synchronized(values.addOne(value))
