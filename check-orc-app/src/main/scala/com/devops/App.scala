package com.devops
import com.devops.utils._
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.conf.Configuration
import org.apache.spark.sql.SparkSession
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import scala.collection.mutable.ListBuffer
import java.time
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.io.{FileNotFoundException, IOException}
import scala.concurrent
import scala.concurrent.duration._
import java.util.concurrent.Executors
import scala.concurrent.{Future, Promise, Await}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}
import java.io.FileInputStream
import java.util.Properties

object App {
  def main(args: Array[String]): Unit = {
    try {
      if (args.length < 2 || args(0) != "--file" ) {
        System.err.println("Please provide the path to config file. eg: --file application.properties")
        System.exit(1)
      }
      val configFilePath = args(1)
      val properties: Properties = new Properties()
      properties.load(new FileInputStream(configFilePath))
      val appVersion = properties.getProperty("app.version")
      
      val conf = new SparkConf()
      val sc = new SparkContext(conf)
      val spark = SparkUtil.getSparkSession
      val hdfsNsUrl = properties.getProperty("app.hdfs.namespace.url")
      val hdfsPartition = new HdfsPartition(hdfsNsUrl)
      val dir = properties.getProperty("app.hdfs.path.orc")
      val hdfsOrcPath = new HdfsOrcPath(dir)
      val filePathList = hdfsPartition.getFileList(hdfsOrcPath.getInputList())
      val isMoveOccuredFile = properties.getProperty("app.move.corrupt.orc").toBoolean
  
      ThreadPool.threadNum = properties.getProperty("app.thread.mumber").toInt
      val futures = filePathList.map(filepath => 
        Future(ReadOrcFile.excute(spark,filepath,hdfsPartition,isMoveOccuredFile))(ThreadPool.executionContext)
      )
      val combinedFuture = Future.sequence(futures)
      // promise transfer filenames to main thread that includes spark context (sc), so that it can use rdd.
      val p = Promise[Seq[String]]() 
      combinedFuture onComplete {
        case Success(filenames) => 
          filenames.foreach(println)
          p.success(filenames)
        case Failure(e) => 
          e.printStackTrace
          p.failure(e)
      }

      // collect info to hdfs
      val now = LocalDateTime.now
      val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
      val formattedNow = now.format(formatter)
      val filenames = Await.result(p.future, Duration.Inf)
      val rdd = sc.parallelize(filenames)
      val coalesceNum = properties.getProperty("app.spark.coalesce.number").toInt
      rdd.coalesce(coalesceNum).saveAsTextFile(hdfsNsUrl + "/tmp/checkOrcFile/dt=" + formattedNow)
  
      // release spark enviroment
      spark.stop()
      sys.exit(0)
    }
    catch {
      case e: Exception =>
        e.printStackTrace()
        //println("The app has encountered an error and needs to exit." )
        //System.exit(1) 
    }
  }
}