package com.devops.utils
import scala.concurrent._
import scala.concurrent.duration._
import java.util.concurrent.Executors
import scala.concurrent.{Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global


object ThreadPool {
  private var _threadNum: Int = 10 
  def threadNum = _threadNum  //getter
  def threadNum_=(value: Int): Unit = {_threadNum = value} //setter
  private val threadPool = Executors.newFixedThreadPool(threadNum)
  implicit val executionContext: ExecutionContext = ExecutionContext.fromExecutorService(threadPool)
}
