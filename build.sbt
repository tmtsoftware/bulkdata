lazy val clients = Seq(client)
lazy val scalaV = "2.11.7"

lazy val commonSettings = Seq(
  scalaVersion := scalaV,
  transitiveClassifiers in Global := Seq(Artifact.SourceClassifier),
  updateOptions := updateOptions.value.withCachedResolution(true),
  libraryDependencies += "me.chrons" %%% "boopickle" % "1.1.0",
  libraryDependencies += "com.softwaremill.macwire" %% "macros" % "1.0.7",
  libraryDependencies += "com.lihaoyi" %%% "upickle" % "0.3.6"
) ++ net.virtualvoid.sbt.graph.Plugin.graphSettings

lazy val common = project.in(file("common"))
  .dependsOn(sharedJvm)
  .settings(commonSettings: _*)
  .settings(
    fork := true,
    libraryDependencies += "com.beachape" %% "enumeratum" % "1.3.1"
  )

lazy val backend = project.in(file("backend"))
  .dependsOn(common)
  .settings(commonSettings: _*)
  .settings(
    fork := true,
    libraryDependencies ++= Dependencies.jvmLibs
  )

lazy val aggProjects = (clients :+ backend :+ common).map(Project.projectToRef)

lazy val frontend = project.in(file("frontend"))
  .enablePlugins(PlayScala)
  .dependsOn(common)
  .aggregate(aggProjects: _*)
  .settings(commonSettings: _*)
  .settings(
    routesGenerator := InjectedRoutesGenerator,
    scalaJSProjects := clients,
    pipelineStages := Seq(scalaJSProd, gzip),
    libraryDependencies ++= Seq(
      "com.vmunier" %% "play-scalajs-scripts" % "0.3.0",
      "org.webjars" % "jquery" % "1.11.1",
      "com.typesafe.akka" %% "akka-cluster-tools" % "2.4.0-RC2",
      "com.typesafe.akka" % "akka-slf4j_2.11" % "2.4.0-RC2"
    )
  )

lazy val client = project.in(file("client"))
  .enablePlugins(ScalaJSPlugin, ScalaJSPlay)
  .dependsOn(sharedJs)
  .settings(commonSettings: _*)
  .settings(
    persistLauncher := true,
    persistLauncher in Test := false,
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.8.0",
      "org.monifu" %%% "monifu" % "1.0-M11"
    )
  )

lazy val shared = crossProject.crossType(CrossType.Pure)
  .in(file("shared"))
  .jsConfigure(_ enablePlugins ScalaJSPlay)
  .settings(commonSettings: _*)

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

// loads the Play project at sbt startup
onLoad in Global := (Command.process("project frontend", _: State)) compose (onLoad in Global).value
