name := "akka-series-cluster"

version := "0.1"

scalaVersion := "2.11.8"

lazy val akkaHttpVersion = "10.0.11"
lazy val igniteVersion    = "2.3.0"
lazy val akkaVersion = "2.5.12"

libraryDependencies += "com.typesafe.akka" %% "akka-remote" % "2.5.12"
libraryDependencies +=  "com.typesafe.akka" %% "akka-actor" % "2.5.12"
libraryDependencies += "com.typesafe.akka" %% "akka-cluster" % "2.5.12"
libraryDependencies += "com.typesafe.akka" %% "akka-http"            % akkaHttpVersion
libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion
libraryDependencies += "org.apache.ignite" % "ignite-core"           % igniteVersion
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.3.0"
libraryDependencies += "org.apache.spark" %% "spark-streaming" % "2.3.0"
