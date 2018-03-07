package com.mtranter.twitterfeelz

import java.util.Properties

import edu.stanford.nlp.pipeline.Annotation
import org.scalatest.{FunSpec, Matchers}
import collection.JavaConverters._

class FeelzSpec extends FunSpec with Matchers{
  describe("Feelz"){
    it("should return positive sentiments for a positive sentence") {
      val sentiment = Feelz.findSentiment("Isabella is amazing.")
      sentiment should be > 2
    }
    it("should return low sentiments for a negative sentence") {
      val sentiment = Feelz.findSentiment("Donald Trump is fat and ugly and absolutely useless.")
      sentiment should be < 2
    }
    it("should return even sentiments for a neutral sentence") {
      val sentiment = Feelz.findSentiment("Roses are red. My name is Dave. This makes no sense. Microwave.")
      sentiment should be(2)
    }
  }

}
