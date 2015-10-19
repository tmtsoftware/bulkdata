import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt._

object Dependencies {

  val sharedLibs = Def.setting {
    Seq(
      "me.chrons" %%% "boopickle" % "1.1.0",
      "com.softwaremill.macwire" %% "macros" % "1.0.7",
      "com.github.benhutchison" %%% "prickle" % "1.1.9",
      "org.scala-lang.modules" %% "scala-async" % "0.9.5"
    )
  }

  val clientLibs = Def.setting {
    Seq(
      "com.lihaoyi" %%% "scalatags" % "0.5.2",
      "com.lihaoyi" %%% "scalarx" % "0.2.8",
      "org.scala-js" %%% "scalajs-dom" % "0.8.2",
      "org.monifu" %%% "monifu" % "1.0-M11"
    )
  }

  val commonLibs = Seq(
    "com.typesafe" % "config" % "1.3.0",
    "com.typesafe.akka" %% "akka-cluster" % Versions.Akka,
    "com.typesafe.akka" %% "akka-cluster-tools" % Versions.Akka,
    "com.typesafe.akka" %% "akka-cluster-metrics" % Versions.Akka,
    "com.typesafe.akka" %% "akka-distributed-data-experimental" % Versions.Akka
  )

  val frontendLibs = Seq(
    "com.vmunier" %% "play-scalajs-scripts" % "0.3.0",
    "org.webjars" % "jquery" % "1.11.1",
    "com.typesafe.akka" % "akka-slf4j_2.11" % Versions.Akka,
    "com.lihaoyi" %% "scalatags" % "0.5.2",
    "com.typesafe.akka" %% "akka-persistence" % Versions.Akka,
    "org.iq80.leveldb" % "leveldb" % "0.7",
    "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8",
    "org.webjars" % "bootstrap" % "3.3.5"
  )

  val backendLibs = Seq(
    "com.typesafe.akka" %% "akka-stream-experimental" % Versions.Streams,
    "com.typesafe.akka" %% "akka-http-core-experimental" % Versions.Streams,
    "com.typesafe.akka" %% "akka-http-experimental" % Versions.Streams,
    "net.codingwell" %% "scala-guice" % "4.0.0",
    "org.scala-lang.modules" %% "scala-async" % "0.9.5",
    "org.imgscalr" % "imgscalr-lib" % "4.2",

    //test
    "org.scalatest" %% "scalatest" % "2.2.5" % "test",
    "com.typesafe.akka" %% "akka-stream-testkit-experimental" % Versions.Streams % "test",
    "com.typesafe.akka" %% "akka-http-testkit-experimental" % Versions.Streams % "test"
  )

}

object Versions {
  val Akka    = "2.4.0"
  val Streams = "1.0"
}
