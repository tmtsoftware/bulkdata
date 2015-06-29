import sbt._

object Dependencies {
  
  val akkaVersion = "2.3.11"

  val jvmLibs = Seq(
    // akka
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-persistence-experimental" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
    "com.typesafe.akka" %% "akka-contrib" % akkaVersion,

    "com.typesafe.akka" %% "akka-stream-experimental" % "1.0-RC4",
    "com.typesafe.akka" %% "akka-http-core-experimental" % "1.0-RC4",
    "com.typesafe.akka" %% "akka-http-experimental" % "1.0-RC4",

    // util
    "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2",
    "ch.qos.logback" % "logback-classic" % "1.1.3",
    "joda-time" % "joda-time" % "2.7",
    "org.joda" % "joda-convert" % "1.7",
    "org.scala-lang.modules" %% "scala-pickling" % "0.10.1",

    //test
    "org.scalatest" %% "scalatest" % "2.2.4" % "test",
    "com.typesafe.akka" %% "akka-stream-testkit-experimental" % "1.0-RC4" % "test",
    "com.typesafe.akka" %% "akka-http-testkit-experimental" % "1.0-RC4" % "test"
  )

}
