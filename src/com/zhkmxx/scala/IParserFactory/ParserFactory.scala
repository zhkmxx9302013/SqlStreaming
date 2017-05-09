package com.zhkmxx.scala.IParserFactory

import scala.util.parsing.combinator.syntactical.StandardTokenParsers

/**
  * Created by zhao on 2017/5/9.
  */
trait ParserFactory {
  def createParseTask(tasktype: String): StandardTokenParsers
}
