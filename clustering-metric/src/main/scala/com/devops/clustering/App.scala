package com.devops.clustering
import com.devops.clustering.utils._
import org.apache.spark.sql.{DataFrame, SparkSession, Row}
import scala.collection.mutable.ListBuffer
import org.apache.spark.sql.types.{StructType, StructField, StringType, DateType}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object App {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
        .enableHiveSupport()
        .getOrCreate()
    val sqlList = new SqlList()
      sqlList.getBuffer()
        .toList
        .foreach( sql => {
           val sparkSql = new SparkSql()
           println(sql)
           sparkSql.excute(sql, spark)
        })
     spark.stop()
  }
}