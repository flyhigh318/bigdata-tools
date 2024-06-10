package com.devops.check.metastore
import com.devops.check.metastore.utils._
import org.apache.spark.sql.SparkSession
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object App {
  def main(args: Array[String]): Unit = {
    if (args.length < 2 || args(0) != "--metastore-url" ) {
        System.err.println("Please provide the parameter. eg: --metastore-url  thrift://10.72.128.51:9083")
        System.exit(1)
    }
    val thriftArray = args(1).split(",")
    val sql = "SHOW DATABASES"
    val app = "check-hive-metastore-app"
    val now = LocalDateTime.now
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val formattedNow = now.format(formatter)
    thriftArray.zipWithIndex.foreach{ case (thrift, index)  =>
      try {
        var appName = app + s"$index"
        val spark = ConectHiveMetastore.getSparkSession(thrift, appName)
        spark.stop()
        val spark1 = ConectHiveMetastore.getSparkSession(thrift, appName)
        ConectHiveMetastore.excuteSql(spark1, sql)
        val message = String.format("%s [INFO] check hive metastore connected successfully: %s", 
          formattedNow, thrift)
        println(message)
      }
      catch {
        case  e: Exception =>
        val message = String.format("%s [ERROR] check hive metastore connected failed: %s", 
          formattedNow, thrift)
        println(message)
        println("Exception: " + e.getMessage)
      }
    }
  }
}
