package com.ariskk.flink4s

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import com.ariskk.flink4s.TypeInfo.intTypeInfo

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
  }

end DataStreamSpec
