package com.ariskk.flink4s

import cats.Semigroup
import cats.kernel.Monoid
import org.apache.flink.streaming.api.datastream.{WindowedStream => JavaWStream}
import org.apache.flink.streaming.api.windowing.windows.Window
import org.apache.flink.api.common.functions.{AggregateFunction, ReduceFunction}
import org.apache.flink.api.common.typeinfo.TypeInformation

final case class WindowedStream[T, K, W <: Window](stream: JavaWStream[T, K, W])(implicit
    typeInfo: TypeInformation[T]
) {

  def reduce(f: (T, T) => T): DataStream[T] = {
    val reducer = new ReduceFunction[T] {
      def reduce(v1: T, v2: T): T = f(v1, v2)
    }
    DataStream(stream.reduce(reducer))
  }

  def aggregate[A, O](f: (A, T) => A)(outF: A => O)(implicit monoid: Monoid[A], aggTypeInfo: TypeInformation[A],
                                                    typeInformation: TypeInformation[O]): DataStream[O] = {
    val reducer = new AggregateFunction[T, A, O] {

      override def createAccumulator(): A = Monoid.empty[A]

      override def add(value: T, accumulator: A): A = f(accumulator, value)

      override def getResult(accumulator: A): O = outF(accumulator)

      override def merge(a: A, b: A): A = Semigroup
        .combine(a, b)
    }
    DataStream(stream.aggregate[A,O](reducer, aggTypeInfo, typeInformation))(typeInformation)
  }

}
