val scala3Version = "3.1.0"

lazy val mavenSnapshots = "apache.snapshots" at "https://repository.apache.org/content/groups/snapshots"

resolvers ++= Seq(mavenSnapshots)

val flinkLibs = Seq(
   "org.apache.flink" % "flink-streaming-java" % "1.15-20220115.010217-79" % "provided",
   "org.apache.flink" % "flink-core" % "1.15-20220115.005917-172" % "provided",
   "org.apache.flink" % "flink-statebackend-rocksdb" % "1.15-20220115.010305-79",
   "org.apache.flink" % "flink-test-utils" % "1.15-20220115.010320-79" % Test
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
