import sbt._

object Dependencies {
  
  val backendLibs = Seq(
    "com.typesafe.akka" %% "akka-stream-experimental" % "1.0",
    "com.typesafe.akka" %% "akka-http-core-experimental" % "1.0",
    "com.typesafe.akka" %% "akka-http-experimental" % "1.0",
    "net.codingwell" %% "scala-guice" % "4.0.0",
    "com.typesafe.akka" %% "akka-cluster" % "2.4.0-RC2",
    "com.typesafe.akka" %% "akka-cluster-tools" % "2.4.0-RC2",
    "com.typesafe.akka" %% "akka-cluster-metrics" % "2.4.0-RC2",

  //test
    "org.scalatest" %% "scalatest" % "2.2.5" % "test",
    "com.typesafe.akka" %% "akka-stream-testkit-experimental" % "1.0" % "test",
    "com.typesafe.akka" %% "akka-http-testkit-experimental" % "1.0" % "test"
  )

}
