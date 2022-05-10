package com.ariskk.flink4s

import org.apache.flink.streaming.api.datastream.{WindowedStream => JavaWStream}
import org.apache.flink.streaming.api.windowing.windows.Window
import org.apache.flink.api.common.functions.ReduceFunction
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

}
