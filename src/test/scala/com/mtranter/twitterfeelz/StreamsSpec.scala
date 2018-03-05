package com.mtranter.twitterfeelz

import com.mtranter.twitterfeelz.models.{Config, InLineAverage, TwitterStatus, TwitterUser}
import org.apache.kafka.common.serialization.{Serdes, Serializer}
import org.apache.kafka.streams.state.QueryableStoreTypes
import org.scalatest.{BeforeAndAfterEach, FunSpec, Matchers}

class StreamsSpec extends FunSpec with Matchers with BeforeAndAfterEach {

  private var kafkaLocalServer: TestKafkaServer = _

  override protected def beforeEach() ={
    kafkaLocalServer = new TestKafkaServer()
    kafkaLocalServer.startup()
  }
  override protected def afterEach() = {
    kafkaLocalServer.close()
  }

  describe("The streams") {
    it("should rate tweets") {
      val pipeline = new TwitterFeelzPipeline()
      val streams = pipeline.creteStreams(Config(kafkaLocalServer.bootstrapServerAddress, "0.0.0.0", 8079, "/tmp/twitter-feelz"))

      streams.start()
      try {

        kafkaLocalServer.produce("keywords", Seq("kafka" -> "kafka"), Serdes.String().serializer(), Serdes.String().serializer())
        kafkaLocalServer.produce("keywords", Seq("python" -> "python"), Serdes.String().serializer(), Serdes.String().serializer())

        sendTweet("kafka is cool")
        sendTweet("Python is annoying")
        sendTweet("Python is ok")
        sendTweet("Python is garbage")

        Thread.sleep(5000)
        val store = streams.store("scores", QueryableStoreTypes.keyValueStore[String, InLineAverage]())
        val kafka = store.get("kafka")
        kafka.avg should be > 2d

        val python = store.get("python")
        python.avg should be < 2d

        val scala1 = store.get("scala")
        scala1 should be(null)

        kafkaLocalServer.produce("keywords", Seq("scala" -> "scala"), Serdes.String().serializer(), Serdes.String().serializer())
        sendTweet("scala is great")
        Thread.sleep(5000)
        val scala2 = store.get("scala")
        scala2.avg should be > 2d
      } finally {
        streams.close()
        streams.cleanUp()
      }
    }

    def sendTweet(tweet: String) =
      kafkaLocalServer.produce("tweets",
        Seq(1l.toString -> TwitterStatus(1, "2018-1-1T12:00:00z", tweet, TwitterUser(2, "mtranter","mtranter"))),
        Serdes.String().serializer().asInstanceOf[Serializer[Any]],
        AvroSerializer[TwitterStatus]()
      )
  }
}
