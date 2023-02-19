package com.ariskk.flink4s

import scala.collection.mutable.{Buffer => MutableBuffer}

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.apache.flink.streaming.api.functions.sink.SinkFunction

import com.ariskk.flink4s.TypeInfo.{intTypeInfo, stringTypeInfo}

final class WindowedStreamSpec extends AnyFunSpec with Matchers {

  describe("WindowedStream") {
    it("should apply reducers to count windows") {
      val env    = FlinkExecutor.newEnv(parallelism = 1)
      val stream = env.fromCollection((1 to 1000).toList)
      val results = stream
        .keyBy(_ % 2)
        .countWindow(250)
        .reduce(_ + _)
        .runAndCollect

      val firstOdds   = (1 to 499 by 2).sum
      val secondOdds  = (501 to 999 by 2).sum
      val firstEvens  = (2 to 500 by 2).sum
      val secondEvens = (502 to 1000 by 2).sum

      results should contain theSameElementsAs (Seq(firstOdds, secondOdds, firstEvens, secondEvens))

    }

    it("should apply reducers to count windows with slide") {
      val env    = FlinkExecutor.newEnv(parallelism = 1)
      val stream = env.fromCollection((1 to 200).toList.map(_ => 1))
      val results = stream
        .keyBy(identity)
        .countWindow(100, 50)
        .reduce(_ + _)
        .runAndCollect

      results.size should equal(4)
      results shouldBe List(50, 100, 100, 100)
    }

    it("should apply aggregation to count window with slide") {
      val env = FlinkExecutor.newEnv(parallelism = 1)
      val stream = env.fromCollection((1 to 200).toList.map(_ => 1))
      val results = stream
        .keyBy(identity)
        .countWindow(100, 50)
        .aggregate[Int, Int]((agg, i) => agg + i)(identity(_))
        .runAndCollect

      results.size should equal(4)
      results shouldBe List(50, 100, 100, 100)
    }


    it("should apply aggregation if aggregator and out are different types") {
      val env = FlinkExecutor.newEnv(parallelism = 1)
      val stream = env.fromCollection((1 to 200).toList.map(_ => 1))
      val results = stream
        .keyBy(identity)
        .countWindow(100, 50)
        .aggregate[Int, String]((agg, i) => agg + i)(_.toString)
        .runAndCollect

      results.size should equal(4)
      results shouldBe List("50", "100", "100", "100")
    }

    it("should apply aggregation if aggregator and out are different types") {
      val env = FlinkExecutor.newEnv(parallelism = 1)
      val stream = env.fromCollection((1 to 200).toList.map(_ => 1))
      val results = stream
        .keyBy(identity)
        .countWindow(100, 50)
        .aggregate[Int, String]((agg, i) => agg + i)(_.toString)
        .runAndCollect

      results.size should equal(4)
      results shouldBe List("50", "100", "100", "100")
    }
  }

}
