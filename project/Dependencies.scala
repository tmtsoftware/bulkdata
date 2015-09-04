import sbt._

object Dependencies {
  
  val jvmLibs = Seq(
    "com.typesafe.akka" %% "akka-stream-experimental" % "1.0",
    "com.typesafe.akka" %% "akka-http-core-experimental" % "1.0",
    "com.typesafe.akka" %% "akka-http-experimental" % "1.0",
    "com.typesafe.play" % "play-json_2.11" % "2.4.2",
    "de.heikoseeberger" %% "akka-http-play-json" % "1.1.0",

    //test
    "org.scalatest" %% "scalatest" % "2.2.5" % "test",
    "com.typesafe.akka" %% "akka-stream-testkit-experimental" % "1.0" % "test",
    "com.typesafe.akka" %% "akka-http-testkit-experimental" % "1.0" % "test"
  )

}
