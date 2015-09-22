name := "tmt-root"

lazy val root = project.in(file(".")).
  aggregate(pubJs, pubsubJvm)
  .settings(
    publish := {},
    publishLocal := {}
  )

lazy val pubsub = crossProject.in(file("."))
  .settings(
    organization := "tmt",
    name := "data-transfer",
    scalaVersion := "2.11.7",
    version := "0.1-SNAPSHOT",
    transitiveClassifiers in Global := Seq(Artifact.SourceClassifier),
    updateOptions := updateOptions.value.withCachedResolution(true),
    parallelExecution in Test := false,
    libraryDependencies += "me.chrons" %%% "boopickle" % "1.1.0",
    libraryDependencies += "com.softwaremill.macwire" %% "macros" % "1.0.7",
    libraryDependencies += "com.lihaoyi" %%% "upickle" % "0.3.6"
  )
  .jvmSettings(Revolver.settings: _*)
  .jvmSettings(
    fork := true,
    libraryDependencies ++= Dependencies.jvmLibs,
    mainClass in Revolver.reStart := Some("tmt.wavefront.Main"),
    Revolver.reStartArgs := Seq("frontend", "dev")
  )
  .jsSettings(
    persistLauncher in Compile := true,
    persistLauncher in Test := false,
    scalaJSStage in Global := FastOptStage,
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.8.1",
      "org.monifu" %%% "monifu" % "1.0-M11"
    )
  )

lazy val pubsubJvm = pubsub.jvm.settings(
  (resourceGenerators in Compile) <+=
    (fastOptJS in Compile in pubJs, packageScalaJSLauncher in Compile in pubJs).map((f1, f2) => Seq(f1.data, f2.data)),
  watchSources <++= (watchSources in pubJs)
)

lazy val pubJs = pubsub.js
