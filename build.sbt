scalaVersion := "2.13.14"

scalacOptions ++= Seq(
  "-feature",
  "-language:reflectiveCalls",
)

val chiselVersion = "6.7.0"
addCompilerPlugin("org.chipsalliance" % "chisel-plugin" % chiselVersion cross CrossVersion.full)
libraryDependencies += "org.chipsalliance" %% "chisel" % chiselVersion
