package com.zhkmxx.scala.IParser

import scala.util.parsing.combinator.syntactical.StandardTokenParsers

/**
  * Created by zhao on 2017/4/9.
  * 单标查询特征
  */
trait SingleParser extends StandardTokenParsers{
  //EBNF 表达式
  def expr: Parser[String]

  //条件子式
  def expr_condition: Parser[String]

  //前缀子式
  def expr_front: Parser[String]

  //后缀子式
  def expr_back: Parser[String]
}
