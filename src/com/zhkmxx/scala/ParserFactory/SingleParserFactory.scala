package com.zhkmxx.scala.ParserFactory

import com.zhkmxx.scala.IParser.TraitParser
import com.zhkmxx.scala.IParserFactory.ParserFactory
import com.zhkmxx.scala.parser.SingleConditionParser

import scala.util.parsing.combinator.syntactical.StandardTokenParsers

/**
  * Created by zhao on 2017/5/9.
  */
class SingleParserFactory extends ParserFactory{
  override def createParseTask(tasktype: String): TraitParser = {
    tasktype match {
      case "" => new SingleConditionParser()
      case _ => println("[SNP_PARSER_ERROR] cannot parse the formula type");null
    }
  }
}
