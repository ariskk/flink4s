val scala3Version = "3.1.1"

lazy val mavenSnapshots = "apache.snapshots" at "https://repository.apache.org/content/groups/snapshots"

resolvers ++= Seq(mavenSnapshots)

val flinkLibs = Seq(
   "org.apache.flink" % "flink-streaming-java" % "1.15-20220219.024841-114" % "provided",
   "org.apache.flink" % "flink-core" % "1.15-20220219.024549-207" % "provided",
   "org.apache.flink" % "flink-statebackend-rocksdb" % "1.15-20220219.024928-114",
   "org.apache.flink" % "flink-test-utils" % "1.15-20220219.024937-114" % Test
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
