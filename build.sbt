val scala3Version = "3.1.1"

val supportedScalaVersions = List(scala3Version)

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
  pomIncludeRepository := { _ => false },
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