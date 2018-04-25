package cluster.debug

import akka.actor.{Actor, ActorSystem, Props}
import cluster.api.WorkflowDebug
import com.typesafe.config.ConfigFactory

object DebugNode extends App {

  //EL puerto 0 en las properties,es para que elija uno al azar que est disponible
  val config = ConfigFactory.load("debug")
  implicit val system: ActorSystem = ActorSystem("StratioClusterSystem", config)
  //Todos los nodos debug tienen un actor que se llama debugActorNode
  system.actorOf(Props[DebugNodeActor], "debugNodeActor")
}


class DebugNodeActor extends Actor {



  override def receive: Receive = {
    case msg: WorkflowDebug =>
      val debugActor = context.actorOf(Props[DebugActor])
      debugActor forward  msg
  }
}