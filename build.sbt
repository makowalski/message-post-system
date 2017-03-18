name := "message-post-system"

version := "0.0.1"

scalaVersion := "2.12.1"

resolvers += "akka" at "http://repo.akka.io/snapshots"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-remote" % "2.4.17",
  "com.typesafe.akka" %% "akka-http" % "10.0.4",
  "com.typesafe.akka" %% "akka-http-core" % "10.0.4",
  "de.heikoseeberger" %% "akka-http-circe" % "1.11.0",
  "io.circe" %% "circe-core" % "0.6.1",
  "io.circe" %% "circe-generic" % "0.6.1",
  "io.circe" %% "circe-parser" % "0.6.1",
  "com.typesafe.slick" %% "slick" % "3.2.0",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "com.typesafe.akka" %% "akka-testkit" % "2.4.17" % "test",
  "org.mockito" % "mockito-core" % "2.2.9" % "test"
)

parallelExecution in Test := false

