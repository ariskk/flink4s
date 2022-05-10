package com.ariskk.flink4s

import org.apache.flink.api.common.state.ValueStateDescriptor
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.common.typeutils.TypeSerializer
import org.apache.flink.streaming.api.functions.co.RichCoMapFunction
import org.apache.flink.streaming.api.datastream.{
  ConnectedStreams => JavaCStreams,
  DataStream => JavaStream
}
import org.apache.flink.api.java.typeutils.ResultTypeQueryable

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

}
