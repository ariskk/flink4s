package `com.ariskk.flink4s`

import org.apache.flink.api.common.functions.{
  FilterFunction,
  FlatMapFunction,
  MapFunction,
  Partitioner
}
import org.apache.flink.api.java.functions.KeySelector
import org.apache.flink.streaming.api.datastream.{
  DataStream => JavaStream,
  KeyedStream => JavaKeyedStream,
  SingleOutputStreamOperator
}
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.ResultTypeQueryable

final case class DataStream[T](stream: JavaStream[T])(using typeInfo: TypeInformation[T]):

  def map[R](f: T => R)(using typeInfo: TypeInformation[R]): DataStream[R] =
    val mapper = new MapFunction[T, R]:
      def map(in: T): R = f(in)
    DataStream(stream.map(mapper, typeInfo).asInstanceOf[JavaStream[R]])

  def keyBy[K](f: T => K)(using keyTypeInfo: TypeInformation[K]): KeyedStream[T, K] =
    val keyExtractor = new KeySelector[T, K] with ResultTypeQueryable[K]:
      def getKey(in: T)                                = f(in)
      override def getProducedType: TypeInformation[K] = keyTypeInfo
    KeyedStream(new JavaKeyedStream(stream, keyExtractor, keyTypeInfo))

end DataStream

object DataStream:
  def apply[T: TypeInformation](stream: SingleOutputStreamOperator[T]): DataStream[T] =
    new DataStream[T](stream.asInstanceOf[JavaStream[T]])
