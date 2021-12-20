package `com.ariskk.flink4s`

import scala.jdk.CollectionConverters._

import org.apache.flink.runtime.state.StateBackend
import org.apache.flink.streaming.api.environment.{
  CheckpointConfig,
  StreamExecutionEnvironment => JavaEnv
}
import org.apache.flink.configuration.Configuration
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.graph.StreamGraph
import org.apache.flink.streaming.api.functions.source.SourceFunction

final case class StreamExecutionEnvironment(javaEnv: JavaEnv):

  def getStreamGraph: StreamGraph = javaEnv.getStreamGraph

  def setParallelism(n: Int): StreamExecutionEnvironment =
    javaEnv.setParallelism(n)
    StreamExecutionEnvironment(javaEnv)

  def setStateBackend(backend: StateBackend): StreamExecutionEnvironment =
    javaEnv.setStateBackend(backend)
    StreamExecutionEnvironment(javaEnv)

  def fromCollection[T](data: Seq[T])(using typeInfo: TypeInformation[T]): DataStream[T] =
    DataStream(javaEnv.fromCollection(data.asJava, typeInfo))

  def addSource[T](function: SourceFunction[T])(using typeInfo: TypeInformation[T]): DataStream[T] =
    DataStream(javaEnv.addSource(function, typeInfo))

end StreamExecutionEnvironment

object StreamExecutionEnvironment:
  def getExecutionEnvironment: StreamExecutionEnvironment =
    StreamExecutionEnvironment(JavaEnv.getExecutionEnvironment())
