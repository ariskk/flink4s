val scala213Version = "2.13.8"
val scala3Version = "3.1.2"

val supportedScalaVersions = List(scala3Version)

lazy val mavenSnapshots = "apache.snapshots" at "https://repository.apache.org/content/groups/snapshots"

resolvers ++= Seq(mavenSnapshots)

val flinkVersion = "1.15.0"

val flinkLibs = Seq(
   "org.apache.flink" % "flink-streaming-java" % flinkVersion % "provided",
   "org.apache.flink" % "flink-core" % flinkVersion % "provided",
   "org.apache.flink" % "flink-statebackend-rocksdb" % flinkVersion,
   "org.apache.flink" % "flink-test-utils" % flinkVersion % Test
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
    crossScalaVersions := Seq(scala3Version, scala213Version),
    libraryDependencies ++= flinkLibs ++ otherLibs ++ testingLibs,
    publishingSettings
  )

import ReleaseTransformations._

lazy val publishingSettings = Seq(
  organization := "com.ariskk",
  organizationName := "ariskk",
  organizationHomepage := Some(url("http://ariskk.com/")),
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/ariskk/flink4s"),
      "scm:git@github.com:ariskk/flink4s.git"
    )
  ),
  developers := List(
    Developer(
      id    = "ariskk",
      name  = "Aris Koliopoulos",
      email = "aris@ariskk.com",
      url   = url("http://ariskk.com")
    )
  ),
  description := "Scala 3 wrapper for Apache Flink",
  licenses := List("MIT" -> new URL("http://opensource.org/licenses/MIT")),
  homepage := Some(url("https://github.com/ariskk/flink4s")),
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
    else Some("releases" at nexus + "service/local/staging/deploy/maven2")
  },
  publishMavenStyle := true,
  releaseProcess := Seq[ReleaseStep](
    inquireVersions,
    runClean,
    runTest,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    publishArtifacts,
    setNextVersion,
    commitNextVersion,
    ReleaseStep(action = Command.process("sonatypeReleaseAll", _)),
    pushChanges
  )
)