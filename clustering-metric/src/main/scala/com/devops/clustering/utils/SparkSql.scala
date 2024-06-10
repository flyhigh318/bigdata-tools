package com.devops.clustering.utils
import org.apache.spark.sql.{DataFrame, SparkSession, Row}
import org.apache.spark.sql.types.{StructType, StructField, StringType, DateType}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SparkSql() {

  def createSchema(spark: SparkSession): DataFrame = {
    val schema = Metric.getSchema()
    val emptyDF = spark.createDataFrame(spark.sparkContext.emptyRDD[Row], schema)
    return emptyDF
  }

  def excute(sql: String, spark: SparkSession): Unit = {
    val metricName = Metric.getName(sql)
    val now = LocalDateTime.now
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val formattedNow = now.format(formatter)
    try {
      val df = spark.sql(sql)
      println(df.show())
      val message = String.format("%s [INFO] %s SQL query successfully: %s", 
         formattedNow, metricName, sql
      )
      println(message)
    }
    catch {
      case  e: Exception => 
      val message = String.format("%s [ERROR] %s SQL query failed: %s", 
         formattedNow, metricName, sql
      )
      println(message)
      println("Exception: " + e.getMessage)
    }
  }
}  