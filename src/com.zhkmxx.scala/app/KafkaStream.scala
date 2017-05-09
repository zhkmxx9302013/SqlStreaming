package com.zhkmxx.scala.app

import java.net.URL
import java.util.Properties

import com.zhkmxx.scala.dao.JdbcDao
import com.zhkmxx.scala.parser.{ExprParsre, InListDCDXDMPaser, InListJGDMParser}
import com.zhkmxx.scala.util.{Const, JsonParser}
import kafka.serializer.StringDecoder
import org.apache.log4j.LogManager
import org.apache.spark.SparkConf
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.sql.jdbc.{JdbcDialect, JdbcDialects, JdbcType}
import org.apache.spark.sql.types._

import scala.util.parsing.combinator.Parsers
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka._

import scala.util.parsing.combinator.syntactical.StandardTokenParsers

/**
  * Created by zhaozihe on 2016/12/13.
  */
object KafkaStream extends Serializable{
  def main(args: Array[String]): Unit = {
//    @transient lazy val log = LogManager.getRootLogger
//    @transient lazy val warnLog = LogManager.getLogger("WARNLOG")
//    @transient lazy val infoLog = LogManager.getLogger("INFOLOG")

    val brokers = Const.BROKER_LIST
    val jdbc = new JdbcDao
    val sparkConf = new SparkConf()
    sparkConf.setAppName(Const.APP_NAME)
      .set("spark.serializer", Const.SPARK_SERIALIZER)
      .set("spark.local.dir", Const.SPARK_TMP_LOG_DIR)
      .set("spark.streaming.kafka.maxRatePerPartition", "10")

    val ssc = new StreamingContext(sparkConf, Seconds(Const.SPARK_STREAMING_INTERVAL))
    ssc.checkpoint(Const.SPARK_CHECKPOINT_DIR)

    val currentFormulaCount = ssc.sparkContext.accumulator(0,"currentFormulaCount")//全局累加变量，用于统计是否为一个任务集合
    var tempTaskID = ""
    var isCompeleted = false

    val topicsSet = Set("snptest")
    val kafkaParams = Map[String, String]("metadata.broker.list" -> brokers, "auto.offset.reset" -> "smallest", "group.id" -> "snpGroup")
    val km = new KafkaManager(kafkaParams)
    val messages = km.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, topicsSet)

    messages.cache()

    messages.foreachRDD(rdd => {

      if (rdd.isEmpty()) {
        println("[SNP-INFO-APP]No data received![SNP-INFO-APP]")
      } else {
        km.updateZKOffsets(rdd)
        val hiveContext = new HiveContext(rdd.context)
        import hiveContext.implicits._
        import hiveContext.sql

        rdd.collect().foreach(element => {
          println("[SNP_JSON]" + element._2)

          val jp = new JsonParser
          val kafkaContentMap = jp.parseKafkaJsonString(element._2)
          println("[SNP_KAFKA: ]" + kafkaContentMap)
          //SumTable related
          val recId = kafkaContentMap("RECID").toString
          val sumTable = kafkaContentMap("SUMTABLE").toString
          val recVer = kafkaContentMap("RECVER").toString.toInt
          val unitCode = kafkaContentMap("UNITCODE").toString
          val rowIndex = kafkaContentMap("ROWINDEX").toString.toInt
          val colIndex = kafkaContentMap("COLINDEX").toString.toInt
          val formula = kafkaContentMap("FORMULA").toString
          val institutionGUID = kafkaContentMap("INSTITUTIONGUID").toString

          //TaskTable related
          val taskRecid = kafkaContentMap("TASKRECID").toString
          val taskRecver = kafkaContentMap("TASKRECVER").toString
          val taskID = kafkaContentMap("TASKID").toString
          val formulaCount = kafkaContentMap("FORMULACOUNT").toString.toInt



          if(!tempTaskID.equals(taskRecid)||isCompeleted) {
            tempTaskID = taskRecid
            currentFormulaCount.setValue(1)
          } else{
            currentFormulaCount += 1
          }

          val ExpressPaser = {
            if(formula.contains("InList([JGDM]")){
              val exprParser = new InListJGDMParser
              val InListJGDMPaser = exprParser.parserAll(exprParser.expr, formula)//Parsing
              InListJGDMPaser
            }else if(formula.contains("InList([DCDXDM]")){
              val exprParser = new InListDCDXDMPaser
              val InListDCDXDMPaser = exprParser.parserAll(exprParser.expr, formula)//Parsing
              InListDCDXDMPaser
            } else {
              val exprParser = new ExprParsre
              var ExpressPaser = exprParser.parserAll(exprParser.expr, formula)//Parsing
              ExpressPaser
            }
          }

          var hiveSql = ""
          if(ExpressPaser.successful){

            hiveSql = ExpressPaser.get
            try{
              val hiveQueryResultDF = sql(hiveSql)//Hive Query Result DataFrame
              val hiveQueryRDD = hiveQueryResultDF.rdd
              val storageDataSet = hiveQueryRDD.map(row => {
                val result = {
                  if(row.get(0) != null){
                    row.get(0).toString
                  }else{
                    "".toString
                  }
                }
                result
              })
              //Write to Oracle
              storageDataSet.collect.foreach(sumResultValue => {
                val dataSet = (recId,sumTable,recVer,unitCode,rowIndex,colIndex,sumResultValue,institutionGUID)
                jdbc.execute2Oracle(dataSet)
                isCompeleted = jdbc.process2Oracle(currentFormulaCount.value, formulaCount, taskID, taskRecid,Const.PROCESS_STATUS_NORMAL)
              })
            }catch {
              case e: Exception => {println("[SNP Exception]: " + e);
                jdbc.process2Oracle(currentFormulaCount.value, formulaCount, taskID,taskRecid,Const.PROCESS_STATUS_ERROR)}
              case unknown: Throwable => {println("[SNP Unknown Exception]: " + unknown);
                isCompeleted = jdbc.process2Oracle(currentFormulaCount.value, formulaCount, taskID,taskRecid,Const.PROCESS_STATUS_ERROR)}
            }

          } else{
            println(Const.PARSE_FORMULA_FAILURE + " => " + ExpressPaser)
            isCompeleted = jdbc.process2Oracle(currentFormulaCount.value, formulaCount, taskID,taskRecid,Const.PROCESS_STATUS_ERROR)
          }

        })
      }
    })

    // Start the computation
    ssc.start()
    ssc.awaitTermination()
  }



}
