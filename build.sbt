val scala3Version = "3.1.0"

lazy val mavenSnapshots = "apache.snapshots" at "https://repository.apache.org/content/groups/snapshots"

resolvers ++= Seq(mavenSnapshots)

val flinkLibs = Seq(
   "org.apache.flink" % "flink-streaming-java" % "1.15-20211220.013854-53" % "provided",
   "org.apache.flink" % "flink-core" % "1.15-20211220.013543-146" % "provided"
)

val otherLibs = Seq(
  "org.typelevel" %% "cats-core" % "2.7.0"
)

lazy val root = project
  .in(file("."))
  .settings(
    name := "flink4s",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies ++= flinkLibs ++ otherLibs
  )
