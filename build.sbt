lazy val clients = Seq(client)
lazy val scalaV = "2.11.7"

lazy val sharedSettings = Seq(
  organization := "tmt",
  scalaVersion := scalaV,
  transitiveClassifiers in Global := Seq(Artifact.SourceClassifier),
  updateOptions := updateOptions.value.withCachedResolution(true),
  libraryDependencies ++= Dependencies.sharedLibs.value,
  scalacOptions ++= Seq(
    "-deprecation",
    "-unchecked",
    "-feature",
    "-Xlint",
    "-Ywarn-dead-code",
    "-encoding", "UTF-8" // yes, this is 2 args
  )
) ++ net.virtualvoid.sbt.graph.Plugin.graphSettings

lazy val client = project
  .enablePlugins(ScalaJSPlugin, ScalaJSPlay)
  .dependsOn(sharedJs)
  .settings(sharedSettings: _*)
  .settings(
    persistLauncher := true,
    persistLauncher in Test := false,
    libraryDependencies ++= Dependencies.clientLibs.value
  )

lazy val common = project
  .dependsOn(sharedJvm)
  .settings(sharedSettings: _*)
  .settings(
    fork := true,
    libraryDependencies ++= Dependencies.commonLibs
  )

lazy val aggProjects = (clients :+ backend :+ common).map(Project.projectToRef)

lazy val frontend = project
  .enablePlugins(PlayScala)
  .dependsOn(common)
  .aggregate(aggProjects: _*)
  .settings(sharedSettings: _*)
  .settings(
    routesGenerator := InjectedRoutesGenerator,
    scalaJSProjects := clients,
    pipelineStages := Seq(scalaJSProd, gzip),
    libraryDependencies ++= Dependencies.frontendLibs
  )

lazy val backend = project
  .dependsOn(common)
  .settings(sharedSettings: _*)
  .settings(
    classpathTypes += "maven-plugin",
    fork := true,
    libraryDependencies ++= Dependencies.backendLibs
  )

lazy val shared = crossProject.crossType(CrossType.Pure)
  .jsConfigure(_ enablePlugins ScalaJSPlay)
  .settings(sharedSettings: _*)

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

// loads the Play project at sbt startup
onLoad in Global := (onLoad in Global).value.andThen { state =>
  Command.process("project frontend", state)
}
