package com.zhkmxx.scala.parser

import scala.Some
import scala.util.parsing.combinator.RegexParsers
import scala.util.parsing.combinator.syntactical.StandardTokenParsers
import scala.util.parsing.combinator.token.StdTokens

/**
  * Created by zhao on 2016/12/27.
  * DNASQL常规语法分析器
  */

/**
  * -@operator => [SUM,COUNT]
  * -@tableName,valueName =>ident;
  * -@comparator => ["=",">=","<=",">","<","!="]
  * -@Condition => stringLit
  * expr-condition ::= tableName "[" valueName "]" comparator Condition
  * expr-front ::= expr-condition (("&&"|"||")expr-front)*
  * expr-back ::= tableName "[" valueName "," operator "]"
  * expr ::= "if" expr-front "then" expr-back
  */
class ExprParsre extends StandardTokenParsers{
  lexical.delimiters += ("=",">=","<=",">","<","!=","&&","||","[","]",",","(",")")
  lexical.reserved   += ("if","then","SUM","COUNT","AND","OR")

  def expr: Parser[String] = "if" ~ expr_front ~ "then" ~ expr_back ^^{
    case "if" ~ exp1 ~ "then" ~ exp2 => exp2 + " WHERE " +exp1
  }

  def expr_priority: Parser[String] = opt("(") ~ expr_condition ~ opt(")") ^^{
    case Some("(") ~ conditions ~ Some(")") => "(" + conditions +")"
    case Some("(") ~ conditions ~ None => "(" + conditions
    case None ~ conditions ~ Some(")") => conditions +")"
    case None ~ conditions ~ None => conditions
  }

  def expr_condition: Parser[String] = ident ~ "[" ~ ident ~ "]" ~ ("="|">="|"<="|">"|"<"|"!=") ~ (numericLit|stringLit) ^^{
    case ident1~"["~ident2~"]"~"="~stringList => ident1 + "." + ident2 +"='" + stringList +"'"
    case ident1~"["~ident2~"]"~">="~stringList => ident1 + "." + ident2 +">='" + stringList +"'"
    case ident1~"["~ident2~"]"~"<="~stringList => ident1 + "." + ident2 +"<='" + stringList +"'"
    case ident1~"["~ident2~"]"~">"~stringList => ident1 + "." + ident2 +">'" + stringList +"'"
    case ident1~"["~ident2~"]"~"<"~stringList => ident1 + "." + ident2 +"<'" + stringList +"'"
    case ident1~"["~ident2~"]"~"!="~stringList => ident1 + "." + ident2 +"!='" + stringList +"'"

    case ident1~"["~ident2~"]"~"="~numericLit => ident1 + "." + ident2 +"='" + numericLit +"'"
    case ident1~"["~ident2~"]"~">="~numericLit => ident1 + "." + ident2 +">='" + numericLit +"'"
    case ident1~"["~ident2~"]"~"<="~numericLit => ident1 + "." + ident2 +"<='" + numericLit +"'"
    case ident1~"["~ident2~"]"~">"~numericLit => ident1 + "." + ident2 +">'" + numericLit +"'"
    case ident1~"["~ident2~"]"~"<"~numericLit => ident1 + "." + ident2 +"<'" + numericLit +"'"
    case ident1~"["~ident2~"]"~"!="~numericLit => ident1 + "." + ident2 +"!='" + numericLit +"'"
  }

  def comparator: Parser[String] = ("&&"|"||"|"AND"|"OR") ^^{
    case "&&" => " AND "
    case "||" => " OR "
    case "AND" => " AND "
    case "OR" => " OR "
  }

  def expr_front: Parser[String] = expr_priority ~ rep(comparator ~ expr_priority) ^^{
    case exp1 ~ exp2  => exp1 +  exp2.map(x =>{x._1 + " " + x._2}).mkString(" ")  //flatMap(_.toString().replaceAll("~"," "))
  }

  def expr_back: Parser[String] = ident ~ "[" ~ ident ~ "," ~ ("SUM"|"COUNT") ~ "]" ^^ {
    case ident1~"["~ident2~","~"COUNT"~"]" => "SELECT COUNT("+ ident2.toString() +") FROM " + ident1.toString()
    case ident1~"["~ident2~","~"SUM"~"]" => "SELECT SUM("+ ident2.toString() +") FROM " + ident1.toString()
  }

  def parserAll[T]( p : Parser[T], input :String) : ParseResult[T]= {
    phrase(p)( new lexical.Scanner(input))
  }


  //  val number = "[0-9]+".r
  //  val tableName = "[(a-zA-Z0-9)_]*\\[".r                //SNP_R601[
  //  val valueName = "\\[[(a-zA-Z0-9)_]*(,|\\])".r         //[D003],[H022,
  //  val comparator = "=|>=|<=|>|<|!=".r
  //  val conditions = "\"[(a-zA-Z0-9\u4E00-\u9FA5)_]*\"".r  //"邢薇67肮傻呆"
  //  val operator = ",[(a-zA-Z0-9)_]*\\]".r                //,SUM]
  //
}
