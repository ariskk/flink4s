val scala3Version = "3.1.0"

lazy val mavenSnapshots = "apache.snapshots" at "https://repository.apache.org/content/groups/snapshots"

resolvers ++= Seq(mavenSnapshots)

val flinkLibs = Seq(
   "org.apache.flink" % "flink-streaming-java" % "1.15-20211220.013854-53" % "provided",
   "org.apache.flink" % "flink-core" % "1.15-20211220.013543-146" % "provided",
   "org.apache.flink" % "flink-statebackend-rocksdb" % "1.15-20211221.010343-54",
   "org.apache.flink" % "flink-test-utils" % "1.15-20211221.010354-54" % Test
)

val otherLibs = Seq(
  "org.typelevel" %% "cats-core" % "2.7.0"
)

val testingLibs = Seq(
  "org.scalatest" %% "scalatest" % "3.2.10" % Test,
)

lazy val root = project
  .in(file("."))
  .settings(
    name := "flink4s",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies ++= flinkLibs ++ otherLibs ++ testingLibs
  )
