name := "twitter-feelz"

version := "0.1"

scalaVersion := "2.12.4"

scalacOptions ++= Seq("-explaintypes")

libraryDependencies ++= Seq(
  "com.lightbend"                %% "kafka-streams-scala"      % "0.1.2" exclude("org.slf4j", "slf4j-log4j12"),
  "com.sksamuel.avro4s"          %% "avro4s-core"              % "1.8.3",
  "org.apache.kafka"             %% "kafka"                    % "1.0.0" excludeAll(ExclusionRule("org.slf4j", "slf4j-log4j12"), ExclusionRule("org.apache.zookeeper", "zookeeper")),
  "com.sksamuel.avro4s"          %% "avro4s-core"              % "1.8.3",
  "edu.stanford.nlp"              % "stanford-corenlp"         % "3.9.1",
  "edu.stanford.nlp"              % "stanford-corenlp"         % "3.9.1" classifier "models",
  "org.apache.curator"            % "curator-test"             % "4.0.0" % "test",
  "org.scalatest"                %% "scalatest"                % "3.0.5" % "test"
)