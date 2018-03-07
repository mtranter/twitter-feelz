package com.mtranter.twitterfeelz.models

import java.util.Properties

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsConfig


case class TwitterStatus(Text: String)

case class InLineAverage(count: Int, sum: Int) {
  def avg =
    sum / count.toDouble
  def next(value: Int) =
    this.copy(count = this.count + 1, this.sum + value)
}

case class Config(bootstrapServers: String, httpInterface: String, httpPort: Int, stateStoreDir: String) {
  def toProps = {
    val settings = new Properties
    settings.put(StreamsConfig.APPLICATION_ID_CONFIG, "kstream-weblog-processing")
    settings.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)


    settings.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.ByteArray.getClass.getName)
    settings.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)

    settings.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

    val endpointHostName = translateHostInterface(httpInterface)

    settings.put(StreamsConfig.APPLICATION_SERVER_CONFIG, s"$endpointHostName:${httpPort}")

    // default is /tmp/kafka-streams
    settings.put(StreamsConfig.STATE_DIR_CONFIG, stateStoreDir)

    // Set the commit interval to 500ms so that any changes are flushed frequently and the summary
    // data are updated with low latency.
    settings.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, "500")

    settings
  }

  private def translateHostInterface(host: String) = host match {
    case "0.0.0.0" => java.net.InetAddress.getLocalHost.getHostAddress
    case x => x
  }
}