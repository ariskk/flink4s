package com.ariskk.flink4s

import scala.jdk.CollectionConverters._
import scala.concurrent.duration.Duration

import org.apache.flink.runtime.state.StateBackend
import org.apache.flink.streaming.api.environment.{
  CheckpointConfig,
  StreamExecutionEnvironment => JavaEnv
}
import org.apache.flink.configuration.Configuration
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.graph.StreamGraph
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.api.common.JobExecutionResult
import org.apache.flink.streaming.api.CheckpointingMode
import org.apache.flink.api.common.restartstrategy.RestartStrategies
import org.apache.flink.streaming.api.TimeCharacteristic

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

  def setStreamTimeCharacteristic(
      timeCharacteristic: TimeCharacteristic
  ): StreamExecutionEnvironment =
    javaEnv.setStreamTimeCharacteristic(timeCharacteristic)
    StreamExecutionEnvironment(javaEnv)

  def setRestartStrategy(
      restartStrategy: RestartStrategies.RestartStrategyConfiguration
  ): StreamExecutionEnvironment =
    javaEnv.setRestartStrategy(restartStrategy)
    StreamExecutionEnvironment(javaEnv)

  def enableCheckpointing(interval: Duration, mode: CheckpointingMode): StreamExecutionEnvironment =
    StreamExecutionEnvironment(javaEnv.enableCheckpointing(interval.toMillis, mode))

  def execute: JobExecutionResult = javaEnv.execute()

  def getCheckpointConfig: CheckpointConfig = javaEnv.getCheckpointConfig

end StreamExecutionEnvironment

object StreamExecutionEnvironment:
  def getExecutionEnvironment: StreamExecutionEnvironment =
    StreamExecutionEnvironment(JavaEnv.getExecutionEnvironment())
