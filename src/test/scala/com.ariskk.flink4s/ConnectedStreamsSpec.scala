package com.ariskk.flink4s

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.apache.flink.api.common.typeinfo.TypeInformation

import TypeInfo.stringTypeInfo
import ConnectedStreamsSpec.State

final class ConnectedStreamsSpec extends AnyFunSpec with Matchers {

  describe("ConnectedStreams") {

    it("should biMap data statefully from two keyed streams") {
      val env            = FlinkExecutor.newEnv(2)
      val fooEvents      = (1 to 100).map(i => s"foo-${i % 10}")
      val barEvents      = (1 to 200).map(i => s"bar-${i % 10}")
      def key(s: String) = s.split("-").last

      val fooStream = env.fromCollection(fooEvents).keyBy(key)
      val barStream = env.fromCollection(barEvents).keyBy(key)

      val stream = fooStream
        .connect(barStream)
        .biMapWithState[(String, State), State](
          (foo, state) => {
            val updated = state.incrementFoo
            val out     = (key(foo), updated)
            (out, updated)
          },
          (bar, state) => {
            val updated = state.incrementBar
            val out     = (key(bar), updated)
            (out, updated)
          },
          emptyState = State(0, 0)
        )

      val results = stream.runAndCollect.groupBy(_._1).view.mapValues(_.last._2)
      results.values.toList.forall(_ == State(10, 20)) shouldBe true

    }

    it("should biFlatMap data statefully from two keyed streams") {
      val env       = FlinkExecutor.newEnv(2)
      val fooEvents = (1 to 100).map(i => s"foo-${i % 10}")
      val barEvents = (1 to 200).map(i => s"bar-${i % 10}")

      def key(s: String) = s.split("-").last
      val fooStream      = env.fromCollection(fooEvents).keyBy(key)
      val barStream      = env.fromCollection(barEvents).keyBy(key)

      val stream = fooStream
        .connect(barStream)
        .biFlatMapWithState[(String, State), State](
          (foo, state) => {
            val updated = state.incrementFoo
            val out     = Some((key(foo), updated))
            (out, updated)
          },
          (bar, state) => {
            val updated = state.incrementBar
            val out     = Some((key(bar), updated))
            (out, updated)
          },
          emptyState = State(0, 0)
        )

      val results = stream.runAndCollect.groupBy(_._1).view.mapValues(_.last._2)
      results.values.toList.forall(_ == State(10, 20)) shouldBe true
    }

    it("should correctly update state in biFlatMap independently of output") {
      val env       = FlinkExecutor.newEnv(2)
      val fooEvents = (1 to 100).map(i => s"foo-${i % 10}")
      val barEvents = (1 to 200).map(i => s"bar-${i % 10}")

      def key(s: String) = s.split("-").last

      def filter(count: Int) = if (count < 20) None else Some(count)
      val fooStream          = env.fromCollection(fooEvents).keyBy(key)
      val barStream          = env.fromCollection(barEvents).keyBy(key)

      val stream = fooStream
        .connect(barStream)
        .biFlatMapWithState[(String, State), State](
          (foo, state) => {
            val updated = state.incrementFoo
            val out     = filter(updated.fooCount).map(_ => (key(foo), updated))
            (out, updated)
          },
          (bar, state) => {
            val updated = state.incrementBar
            val out     = filter(updated.barCount).map(_ => (key(bar), updated))
            (out, updated)
          },
          emptyState = State(0, 0)
        )

      val results = stream.runAndCollect.groupBy(_._1).view.mapValues(_.last._2)
      results.values.toList.forall(_ == State(10, 20)) shouldBe true
    }

    it("should not emit None values") {
      val env       = FlinkExecutor.newEnv(2)
      val fooEvents = (1 to 100).map(i => s"foo-${i % 10}")
      val barEvents = (1 to 200).map(i => s"bar-${i % 10}")

      def key(s: String)      = s.split("-").last
      def keyFirst(s: String) = s.split("-").head

      def filter(count: Int) = if (count % 2 == 0) None else Some(count)

      val fooStream = env.fromCollection(fooEvents).keyBy(key)
      val barStream = env.fromCollection(barEvents).keyBy(key)

      val stream = fooStream
        .connect(barStream)
        .biFlatMapWithState[String, State](
          (foo, state) => {
            val updated = state.incrementFoo
            val out     = filter(updated.fooCount).map(_ => keyFirst(foo))
            (out, updated)
          },
          (bar, state) => {
            val updated = state.incrementBar
            val out     = filter(updated.barCount).map(_ => keyFirst(bar))
            (out, updated)
          },
          emptyState = State(0, 0)
        )

      val results = stream.runAndCollect.groupBy(identity(_)).view.mapValues(_.size).toMap
      results shouldEqual Map("foo" -> 50, "bar" -> 100)
    }
  }

}

object ConnectedStreamsSpec {
  final case class State(fooCount: Int, barCount: Int) {
    lazy val incrementFoo = State(fooCount + 1, barCount)
    lazy val incrementBar = State(fooCount, barCount + 1)
  }

  implicit val stateTypeInfo: TypeInformation[State] = TypeInformation.of(classOf[State])
  implicit val keyedTypeInfo: TypeInformation[(String, State)] =
    TypeInformation.of(classOf[(String, State)])

}
