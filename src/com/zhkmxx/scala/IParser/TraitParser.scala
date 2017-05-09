package com.zhkmxx.scala.IParser

import scala.util.parsing.combinator.syntactical.StandardTokenParsers

/**
  * Created by zhao on 2017/5/9.
  */
trait TraitParser extends StandardTokenParsers{
  //EBNF 表达式
  def expr: Parser[String]

  def parserAll[T]( p : Parser[T], input :String) : ParseResult[T]
}
