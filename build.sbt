name := "tmt-root"

lazy val root = project.in(file(".")).
  aggregate(dtJs, dtJvm)
  .settings(
    publish := {},
    publishLocal := {}
  )

lazy val dataTransfer = crossProject.in(file("."))
  .settings(
    organization := "tmt",
    name := "data-transfer",
    scalaVersion := "2.11.6",
    version := "0.1-SNAPSHOT",
    transitiveClassifiers in Global := Seq(Artifact.SourceClassifier),
    libraryDependencies += "me.chrons" %%% "boopickle" % "1.0.0"
  )
  .jvmSettings(
    fork := true,
    libraryDependencies ++= Dependencies.jvmLibs
  )
  .jvmSettings(Revolver.settings: _*)
  .jsSettings()

lazy val dtJvm = dataTransfer.jvm

lazy val dtJs = dataTransfer.js
