import sbt._

object Dependencies {
  
  val jvmLibs = Seq(
    "com.typesafe.akka" %% "akka-stream-experimental" % "1.0",
    "com.typesafe.akka" %% "akka-http-core-experimental" % "1.0",
    "com.typesafe.akka" %% "akka-http-experimental" % "1.0",
    "net.codingwell" %% "scala-guice" % "4.0.0",

    //test
    "org.scalatest" %% "scalatest" % "2.2.5" % "test",
    "com.typesafe.akka" %% "akka-stream-testkit-experimental" % "1.0" % "test",
    "com.typesafe.akka" %% "akka-http-testkit-experimental" % "1.0" % "test"
  )

}
