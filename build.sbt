name := "streaming"

version := "1.0"

scalaVersion := "2.10.4"

mainClass in (Compile, test) := Some("com.ing.journal.IntegrationTest")

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "1.1.0",
  "org.apache.spark" %% "spark-streaming" % "1.1.0",
  "org.apache.spark" %% "spark-streaming-twitter" % "1.1.0",
  "org.apache.spark" %% "spark-streaming-kafka" % "1.1.0",
  "org.apache.kafka" %% "kafka" % "0.8.1.1",
  "io.gatling.uncommons.maths" % "uncommons-maths" % "1.2.3",
  "joda-time" % "joda-time" % "2.4",
  "io.spray" % "spray-can" % "1.2.1",
  "io.spray" % "spray-http" % "1.2.1",
  "io.spray" % "spray-routing" % "1.2.1",
  "io.spray" % "spray-client" % "1.2.1",
  "io.spray" %% "spray-json" % "1.2.6",
  "com.typesafe.akka" %% "akka-actor" % "2.3.6",
  "com.typesafe.akka" %% "akka-testkit" % "2.2.4" % "test",
  "com.typesafe.akka" %% "akka-slf4j" % "2.2.4",
  "com.typesafe.slick" %% "slick" % "1.0.1",
  "mysql" % "mysql-connector-java" % "5.1.25",
  "net.liftweb" %% "lift-json" % "2.5.1",
  "ch.qos.logback" % "logback-classic" % "1.0.13",
  "com.websudos" %% "phantom-dsl" % "1.2.2",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "junit" % "junit" % "4.10" % "test",
  "com.twitter" %% "algebird-core" % "0.6.0",
  "com.novus" %% "salat" % "1.9.8",
  "org.reactivemongo" %% "reactivemongo" % "0.10.0",
  "com.github.nscala-time" %% "nscala-time" % "1.2.0",
  "ch.qos.logback" % "logback-classic" % "1.1.2",
  "com.typesafe.play" %% "play-json" % "2.3.4",
  "org.renjin" % "renjin-script-engine" % "0.7.0-RC7",
  "com.google.guava" % "guava" % "13.0" force(),
  "org.python" % "jython-standalone" % "2.7-b3",
  "org.aspectj" % "aspectjweaver" % "1.8.4",
  "org.aspectj" % "aspectjrt" % "1.8.4"
)

resolvers ++= Seq(
  "Spray repository" at "http://repo.spray.io",
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
  "BeDataDriven" at "http://nexus.bedatadriven.com/content/groups/public/"
)