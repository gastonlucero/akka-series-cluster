package cluster.debug

import akka.actor.{Actor, Kill}
import cluster.api.WorkflowDebug
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.collection.mutable

class DebugActor extends Actor {

  lazy val ssc = {
    val conf = new SparkConf().setMaster("local[*]").setAppName(self.path.name).set("spark.ui.port", "0")
    new StreamingContext(conf, Seconds(1))
  }

  override def preStart(): Unit = {
    println(s"Iniciando actor worker ${self.path.name}")
  }

  override def postStop(): Unit = {
    println(s"Spark detenido actor worker ${self.path.name}")
  }

  override def receive: Receive = {
    case msg: WorkflowDebug => {
      val lines = mutable.Queue[RDD[String]]()
      val dstream = ssc.queueStream(lines)
      dstream.map(_.toUpperCase).foreachRDD(r => r.foreach(println))
      ssc.start()
      for(i<- 1 to 10){
        lines += ssc.sparkContext.makeRDD(Seq(msg.input))
        Thread.sleep(1000)
      }
      ssc.stop()
      sender() ! msg.address.get
      self ! Kill
    }
  }
}
