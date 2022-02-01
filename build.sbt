val scala3Version = "3.1.1"

lazy val mavenSnapshots = "apache.snapshots" at "https://repository.apache.org/content/groups/snapshots"

resolvers ++= Seq(mavenSnapshots)

val flinkLibs = Seq(
   "org.apache.flink" % "flink-streaming-java" % "1.15-20220201.010159-96" % "provided",
   "org.apache.flink" % "flink-core" % "1.15-20220201.005758-189" % "provided",
   "org.apache.flink" % "flink-statebackend-rocksdb" % "1.15-20220201.010304-96",
   "org.apache.flink" % "flink-test-utils" % "1.15-20220201.010321-96" % Test
)

val otherLibs = Seq(
  "org.typelevel" %% "cats-core" % "2.7.0"
)

val testingLibs = Seq(
  "org.scalatest" %% "scalatest" % "3.2.11" % Test,
)

lazy val root = project
  .in(file("."))
  .settings(
    name := "flink4s",
    scalaVersion := scala3Version,
    libraryDependencies ++= flinkLibs ++ otherLibs ++ testingLibs
  )
