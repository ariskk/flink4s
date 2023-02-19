package com.ariskk.flink4s

import org.apache.flink.api.common.state.ValueStateDescriptor
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.common.typeutils.TypeSerializer
import org.apache.flink.streaming.api.functions.co.{RichCoFlatMapFunction, RichCoMapFunction}
import org.apache.flink.streaming.api.datastream.{
  ConnectedStreams => JavaCStreams,
  DataStream => JavaStream
}
import org.apache.flink.api.java.typeutils.ResultTypeQueryable
import org.apache.flink.util.Collector

final case class ConnectedStreams[A, B](streams: JavaCStreams[A, B]) {

  def biMapWithState[O, S](
      f1: (A, S) => (O, S),
      f2: (B, S) => (O, S),
      emptyState: S
  )(implicit stateTypeInfo: TypeInformation[S], outTypeInfo: TypeInformation[O]): DataStream[O] = {
    val biMapper = new RichCoMapFunction[A, B, O] with ResultTypeQueryable[O] {
      lazy val serializer: TypeSerializer[S] =
        stateTypeInfo.createSerializer(getRuntimeContext.getExecutionConfig)
      lazy val stateDescriptor = new ValueStateDescriptor[S]("name", serializer)

      def map[I](in: I, f: (I, S) => (O, S)): O = {
        val state         = getRuntimeContext.getState(stateDescriptor)
        val (o, newState) = f(in, Option(state.value).getOrElse(emptyState))
        state.update(newState)
        o
      }

      override def map1(in: A): O = map[A](in, f1)

      override def map2(in: B): O = map[B](in, f2)

      override def getProducedType: TypeInformation[O] = outTypeInfo

    }
    DataStream(streams.map(biMapper))
  }

  def biFlatMapWithState[O, S](
      f1: (A, S) => (Option[O], S),
      f2: (B, S) => (Option[O], S),
      emptyState: S
  )(implicit stateTypeInfo: TypeInformation[S], outTypeInfo: TypeInformation[O]): DataStream[O] = {
    val biFlatMapper = new RichCoFlatMapFunction[A, B, O] with ResultTypeQueryable[O] {
      lazy val serializer: TypeSerializer[S] =
        stateTypeInfo.createSerializer(getRuntimeContext.getExecutionConfig)
      lazy val stateDescriptor = new ValueStateDescriptor[S]("name", serializer)

      def map[I](in: I, f: (I, S) => (Option[O], S)): Option[O] = {
        val state         = getRuntimeContext.getState(stateDescriptor)
        val (o, newState) = f(in, Option(state.value).getOrElse(emptyState))
        state.update(newState)
        o
      }

      override def flatMap1(value: A, out: Collector[O]): Unit = map[A](value, f1)
        .foreach(out.collect(_))

      override def flatMap2(value: B, out: Collector[O]): Unit = map[B](value, f2)
        .foreach(out.collect(_))

      override def getProducedType: TypeInformation[O] = outTypeInfo

    }
    DataStream(streams.flatMap(biFlatMapper))
  }

}
