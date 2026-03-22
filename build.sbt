scalaVersion := "2.12.20"

scalacOptions ++= Seq(
  "-feature",
  "-language:reflectiveCalls",
)

scalaVersion := "2.13.14"
val chiselVersion = "3.6.1"
addCompilerPlugin("edu.berkeley.cs" %% "chisel3-plugin" % chiselVersion cross CrossVersion.full)
libraryDependencies += "edu.berkeley.cs" %% "chisel3" % chiselVersion
libraryDependencies += "io.github.tjarker" %% "liftoff" % "0.0.1"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19" % "test"