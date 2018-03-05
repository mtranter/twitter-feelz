package com.mtranter.twitterfeelz

import edu.stanford.nlp.ling.CoreAnnotations
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations
import edu.stanford.nlp.pipeline.StanfordCoreNLP
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations
import edu.stanford.nlp.trees.Tree

import scala.collection.JavaConverters._

object Feelz {
  var pipeline: StanfordCoreNLP = new StanfordCoreNLP("nlp.properties")

  private def getPredClass(tree: Tree) = RNNCoreAnnotations.getPredictedClass(tree)

  def findSentiment(tweet: String): Int = {
    if (tweet != null && tweet.length > 0) {
      val annotation = pipeline.process(tweet)
      val entityMentions = annotation.get(classOf[CoreAnnotations.EntityClassAnnotation])

      val sentence = annotation.get(classOf[CoreAnnotations.SentencesAnnotation]).asScala.maxBy(s => s.size())
      val tree = sentence.get(classOf[SentimentCoreAnnotations.SentimentAnnotatedTree])

      val sentiment = RNNCoreAnnotations.getPredictedClass(tree)
      sentiment
    } else {
      2
    }
  }
}