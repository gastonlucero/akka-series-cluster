package cluster.debug

import akka.actor.{Actor, ActorSystem, Props}
import cluster.api.WorkflowDebug
import com.typesafe.config.ConfigFactory

/**
  * Nodo que se inicia como un nodo debug para recibir peticiones de debug, forma parte del cluster, por sus configuraciones
  * y sabe donde estan los seedNodes, pero es un nodo común
  */
object DebugNode extends App {

  //EL puerto 0 en las properties,es para que elija uno al azar que est disponible
  val config = ConfigFactory.load("debug")

  //Mismo nombre de actorSytem
  implicit val system: ActorSystem = ActorSystem("StratioClusterSystem", config)

  //Todos los nodos debug tienen un actor que se llama debugActorNode
  system.actorOf(Props[DebugNodeActor], "debugNodeActor")
}

//Este actor est iniciado y a la espera de que le llegue un mnesaje desde el actor que esta en un apiNode remoto
class DebugNodeActor extends Actor {

  override def receive: Receive = {
    case msg: WorkflowDebug =>
      val debugActor = context.actorOf(Props[DebugActor])
      //Con forward, mantenemos en la variable sender() DENTRO de debugActor,la referencia del actor remoto que nos mando el mnesaje
      debugActor forward  msg
  }
}