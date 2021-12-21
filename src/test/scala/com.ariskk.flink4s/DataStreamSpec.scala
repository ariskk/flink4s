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
  }

end DataStreamSpec
