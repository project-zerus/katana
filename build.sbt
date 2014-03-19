import com.typesafe.sbt._
import com.typesafe.sbt.packager._
import com.typesafe.sbt.SbtNativePackager._

name := "katana"

organization := "co.zerus.katana"

version := "0.1-SNAPSHOT"

scalaVersion := "2.10.3"

libraryDependencies += "com.github.scopt" %% "scopt" % "3.2.0"

libraryDependencies += "com.google.guava" % "guava" % "16.0.1"

libraryDependencies += "org.redline-rpm" % "redline" % "1.1.15"

libraryDependencies += "com.twitter" %% "util-eval" % "6.13.0"

libraryDependencies += "org.specs2" %% "specs2" % "2.3.10" % "test"

libraryDependencies += "junit" % "junit" % "4.11" % "test"

compileOrder := CompileOrder.Mixed

javacOptions ++= Seq( "-source", "1.6", "-target", "1.6" )

seq(packagerSettings: _*)

packageArchetype.java_application
