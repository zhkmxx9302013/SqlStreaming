package com.zhkmxx.scala.IParser

import scala.util.parsing.combinator.syntactical.StandardTokenParsers

/**
  * Created by zhao on 2017/4/9.
  * 单标查询特征
  */
trait SingleParser extends TraitParser{

  //条件子式
  def expr_condition: Parser[String]

  //前缀子式
  def expr_front: Parser[String]

  //后缀子式
  def expr_back: Parser[String]

}
