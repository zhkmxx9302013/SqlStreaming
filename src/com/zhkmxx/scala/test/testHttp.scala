package com.zhkmxx.scala.test

import com.zhkmxx.scala.util.HttpRequest

/**
  * Created by zhao on 2017/2/14.
  */
object testHttp {
  def main(args: Array[String]): Unit = {
   // val content = new HttpRequest().getRestContent("http://10.2.32.10:18080/api/v1/applications/app-20170129112739-0001/jobs/10")
   // println(content)

    val s1 = "123"
    val s2 = "123".toString
    println(s1.equals(s2))

  }
}
