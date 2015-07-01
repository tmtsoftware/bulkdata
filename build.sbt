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
  .jvmSettings(
    mainClass in Revolver.reStart := Some("top.dsl.Server")
  )
  .jsSettings(
    persistLauncher in Compile := true,
    persistLauncher in Test := false,
    scalaJSStage in Global := FastOptStage,
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.8.0",
      "org.monifu" %%% "monifu" % "1.0-M1"
    )
  )

lazy val dtJvm = dataTransfer.jvm.settings(
  (resourceGenerators in Compile) <+=
    (fastOptJS in Compile in dtJs, packageScalaJSLauncher in Compile in dtJs)
      .map((f1, f2) => Seq(f1.data, f2.data)),
  watchSources <++= (watchSources in dtJs)
)

lazy val dtJs = dataTransfer.js
