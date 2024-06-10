package com.devops.utils
import org.apache.spark.sql.SparkSession
import com.devops.utils.HdfsPartition
object ReadOrcFile { 
  def excute(spark: SparkSession, filePath: String, hdfsPartition: HdfsPartition, isMoveOccuredFile: Boolean): String = {
    var result = ""
    try {
      var orcDF = spark.read.orc(filePath)
      // println(orcDF.printSchema())
      // println(orcDF.count)
      result = "successfully parse orc file: " + filePath
      orcDF = null
    } 
    catch {
      case  e: Exception => 
      // println("parse  orc failed: " + filePath)
      // println("Exception: " + e.getMessage)
      if (isMoveOccuredFile) {
        hdfsPartition.moveHdfsFile(filePath)
      }
      result = "parse  orc failed: " + s"$filePath"
    }
    return result
  }
}