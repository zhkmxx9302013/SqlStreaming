package com.zhkmxx.scala.parser
import com.zhkmxx.scala.IParser.InListParser

import scala.Some
import scala.util.parsing.combinator.RegexParsers
import scala.util.parsing.combinator.syntactical.StandardTokenParsers
import scala.util.parsing.combinator.token.StdTokens

/**
  * Created by zhao on 2017/3/23.
  * DNASQL,机构代码模糊匹配语法分析器
  * SELECT SUM(SNP_R601.H321) FROM SNP_R601 left semi join MD_ORG on (SNP_R601.UNITID = MD_ORG.RECID and MD_ORG.STDCODE LIKE '36%')
  */
class InListJGDMParser extends StandardTokenParsers with InListParser{
  lexical.delimiters += ("=",">=","<=",">","<","!=","&&","||","[","]",",","(",")")
  lexical.reserved   += ("if","then","SUM","COUNT","AND","OR","InList","JGDM")

  def expr: Parser[String] = "if" ~ expr_front ~ "then" ~ expr_back ^^{
    case "if" ~ exp1 ~ "then" ~ exp2 => exp2 + exp1
  }


  def expr_front: Parser[String] = "(" ~"InList"~"("~"["~"JGDM"~"]"~","~ stringLit ~ ")"~")" ^^{
    case "(" ~"InList"~"("~"["~"JGDM"~"]"~","~ stringLit ~ ")"~")" => "= MD_ORG.RECID AND MD_ORG.STDCODE LIKE '" + stringLit +"%')"
  }

  def expr_back: Parser[String] = ident ~ "[" ~ ident ~ "," ~ ("SUM"|"COUNT") ~ "]" ^^ {

    case ident1~"["~ident2~","~"COUNT"~"]" => "SELECT COUNT("+ ident1.toString +"."+ ident2.toString +") " +
    "FROM " + ident1.toString + " LEFT SEMI JOIN MD_ORG ON (" + ident1.toString + ".UNITID "
    case ident1~"["~ident2~","~"SUM"~"]" => "SELECT SUM("+ ident1.toString +"."+ ident2.toString +") " +
      "FROM " + ident1.toString + " LEFT SEMI JOIN MD_ORG ON (" + ident1.toString + ".UNITID "
  }

  def parserAll[T]( p : Parser[T], input :String): ParseResult[T]= {
    phrase(p)( new lexical.Scanner(input))
  }
}

