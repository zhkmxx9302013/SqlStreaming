package com.zhkmxx.scala.ParserFactory

import com.zhkmxx.scala.IParser.TraitParser

import scala.util.parsing.combinator.syntactical.StandardTokenParsers

/**
  * Created by zhao on 2017/5/9.
  */
class PaserClassifier {
  def classifyParser(formula: String):TraitParser = {
    if(formula.contains("InList")){
      val InListParserClassify = new InListParserFactory
      val parserObj = {
        if(formula.contains("InList([JGDM]")){
          InListParserClassify.createParseTask("InList([JGDM]")
        }else{
          InListParserClassify.createParseTask("InList([DCDXDM]")
        }
      }
      parserObj
    }else{
      val SingleParserClassify = new SingleParserFactory
      SingleParserClassify.createParseTask("")
    }

  }
}
