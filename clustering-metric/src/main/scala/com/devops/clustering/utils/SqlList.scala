package com.devops.clustering.utils
import org.apache.spark.sql.SparkSession
import scala.collection.mutable.ListBuffer
import java.time._

class SqlList() {
  
  val sqlListBuffer = new ListBuffer[String]()

  def getBuffer(): ListBuffer[String] = {
    val now = LocalDate.now
    val yesterday = now.minusDays(1)
    /* 
     proemthues metrics: clustering.example_crane_ads.tb_hourly_data
       select car_time from example_crane_ads.tb_test_data where car_time > '2023-10-19' limit 1;
       select car_time from example_crane_ads.tb_test_data where dt='2023-10-19' limit 1;
     proemthues metrics: clustering.example_crane_ads.work_daily_count
       select dt from example_crane_ads.work_daily_count where dt='2023-10-19' limit 1;
     proemthues metrics:  clustering.example_crane_ads.mysql_cluster_daily
       select car_time from example_crane_ads.mysql_cluster_tmp limit 1;
     proemthues metrics: clustering.example_crane_ads.mysql_cluster_detail_daily
       select car_time from example_crane_ads.mysql_cluster_detail_tmp limit 1;
    */
    val sql1 = "select car_time from example_crane_ads.tb_test_data where dt='" + s"$yesterday" + "' limit 1"
    val sql2 = "select dt from example_crane_ads.work_daily_count where dt='" + s"$yesterday" + "' limit 1"
    val sql3 = "select car_time from example_crane_ads.mysql_cluster_tmp limit 1"
    val sql4 = "select car_time from example_crane_ads.mysql_cluster_detail_tmp limit 1"
    sqlListBuffer += s"$sql1"
    sqlListBuffer ++= List(s"$sql2", s"$sql3")
    sqlListBuffer.insert(3, s"$sql4")
    sqlListBuffer 
  }
}