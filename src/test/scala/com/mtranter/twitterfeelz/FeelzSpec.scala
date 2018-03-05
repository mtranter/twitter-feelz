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

    it("Should be sick at coreferencing") {
      import edu.stanford.nlp.coref.CorefCoreAnnotations
      import edu.stanford.nlp.ling.CoreAnnotations
      import edu.stanford.nlp.pipeline.StanfordCoreNLP
      import edu.stanford.nlp.util.CoreMap
      val document = new Annotation("Barack Obama was born in Hawaii.  He is the president. Obama was elected in 2008.")
      val props = new Properties()
      props.setProperty("annotators", "tokenize,ssplit,pos,lemma,parse,ner,sentiment")
      val pipeline = new StanfordCoreNLP(props)
      pipeline.annotate(document)
      System.out.println("---")
      System.out.println("coref chains")
      for (cc <- document.get(classOf[CorefCoreAnnotations.CorefChainAnnotation]).values.asScala) {
        System.out.println("\t" + cc)
      }
//      for (sentence <- document.get(classOf[CoreAnnotations.SentencesAnnotation]).asScala) {
//        System.out.println("---")
//        System.out.println("mentions")
//
//        for (m <- sentence.get(classOf[CorefCoreAnnotations.CorefMentionsAnnotation]).asScala) {
//          System.out.println("\t" + m)
//        }
//      }
    }
  }

}
