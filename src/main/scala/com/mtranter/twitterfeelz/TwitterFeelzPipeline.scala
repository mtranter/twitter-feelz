package com.mtranter.twitterfeelz

import com.lightbend.kafka.scala.streams.StreamsBuilderS
import com.mtranter.twitterfeelz.models.{Config, InLineAverage, TwitterStatus}
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.{Consumed, KafkaStreams, KeyValue, StreamsBuilder}
import org.apache.kafka.streams.kstream.{Materialized, Serialized}
import org.apache.kafka.streams.state.{KeyValueStore, QueryableStoreTypes}

import scala.collection.JavaConverters._

class TwitterFeelzPipeline extends Serializers {
  def creteStreams(config: Config) = {
    val streamingConfig = config.toProps

    val keywordsStoreName = "keywords"
    val builder = new StreamsBuilderS
    var streamsO: Option[KafkaStreams] = None

    /*
    I'm pretty sure closing over the Streams object is not best practise.
    Given that I know I'm accessing a GlobalTable this should be OK.
    It would be nice if the Streams DSL offered some way of doing this.
     */
    def keywordValues = streamsO
      .map(_.store(keywordsStoreName, QueryableStoreTypes.keyValueStore[String,String]())
        .all().asScala
        .map(k => k.value.toLowerCase())
        .toSeq)
      .getOrElse(Seq())

    val tweetWithKeywords = (t:TwitterStatus) =>
      keywordValues
        .filter(t.Text.toLowerCase().contains)
        .map(_-> t)

    // Get the global list of KeyWords to be watched for in the twitter stream.
    builder.globalTable[String,String]("keywords",
      Materialized.as[String, String, KeyValueStore[Bytes, Array[Byte]]](keywordsStoreName))


    // Listen for tweets coming into kafka on the 'tweets' topic
    builder.stream[Long, TwitterStatus]("tweets", Consumed.`with`[Long, TwitterStatus](longSerde,serdeFor[TwitterStatus]))
      // Fan-out each tweet, matching a tweet against the keywords it contains
      .flatMap((_,t) => tweetWithKeywords(t))
      // Do Sentiment Analysis
      .map((k,v) => (k, Feelz.findSentiment(v.Text)))
      // Group by Keyword for aggregation
      .groupByKey(Serialized.`with`(stringSerde,intSerde))
      // In-line average calculation: Reduce stream
      .aggregate(() => InLineAverage(0,0), (_: String, nextScore: Int, agg: InLineAverage ) => agg.next(nextScore),
        // Materialize to a KTable
        Materialized.as("scores")
          .withKeySerde(stringSerde)
          .withValueSerde(serdeFor[InLineAverage])
      )

    val streams = new KafkaStreams(builder.build(), streamingConfig)

    // Store streams instance for future access to GlobalKTable
    streamsO = Some(streams)
    streams
  }
}



