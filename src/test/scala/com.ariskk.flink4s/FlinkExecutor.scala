package com.ariskk.flink4s

import scala.util.Random
import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration
import org.apache.flink.test.util.MiniClusterWithClientResource
import org.apache.flink.contrib.streaming.state.RocksDBStateBackend
import org.apache.flink.runtime.state.hashmap.HashMapStateBackend

object FlinkExecutor {

  private var flinkCluster: MiniClusterWithClientResource = _

  def startCluster(): Unit = {
    flinkCluster = new MiniClusterWithClientResource(
      new MiniClusterResourceConfiguration.Builder()
        .setNumberSlotsPerTaskManager(2)
        .setNumberTaskManagers(2)
        .build
    )
    flinkCluster.before()
  }

  def stopCluster(): Unit = flinkCluster.after()

  def newEnv(parallelism: Int = 2): StreamExecutionEnvironment = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(parallelism)
    val rocks = new RocksDBStateBackend(s"file:///tmp/flink-${Random.nextString(10)}")
    env.setStateBackend(rocks)
    env
  }

}
