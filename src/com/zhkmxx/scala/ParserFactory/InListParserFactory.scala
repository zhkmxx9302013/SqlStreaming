package com.zhkmxx.scala.ParserFactory

import com.zhkmxx.scala.IParser.TraitParser
import com.zhkmxx.scala.IParserFactory.ParserFactory
import com.zhkmxx.scala.parser.{InListDCDXDMPaser, InListJGDMParser, SingleConditionParser}

import scala.util.parsing.combinator.syntactical.StandardTokenParsers

/**
  * Created by zhao on 2017/5/9.
  */
class InListParserFactory extends ParserFactory{
  override def createParseTask(tasktype: String): TraitParser = {
    tasktype match {
      case "InList([JGDM]" => new InListJGDMParser()
      case "InList([DCDXDM]" => new InListDCDXDMPaser()
    }
  }
}
