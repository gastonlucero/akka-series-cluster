package cluster

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object Main extends App {
  val system = ActorSystem("StratioClusterSystem", ConfigFactory.load("applicationCluster"))
  system.actorOf(Props[ApiNode])
}
