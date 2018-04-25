package cluster.api

import akka.actor.{Actor, ActorSelection, RootActorPath}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._

import scala.collection.mutable


/**
  * Este actor solo recibe los mensajes desde la api rest, e invoca en forma remota alguno de los debugNode que se han registrado
  * en el clutser
  */
class ApiNode extends Actor {

  val cluster = Cluster(context.system)

  // subscribe to cluster changes, re-subscribe when restart
  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents, classOf[MemberEvent], classOf[UnreachableMember])
  }

  var debugActorsLibres = mutable.Map[String, ActorSelection]()
  var debugActorsOcupados = mutable.Map[String, ActorSelection]()

  override def postStop(): Unit = cluster.unsubscribe(self)

  def receive = {
    //Recibe cualquier evento que haya pasado en el cluster
    //SI el nuevo miembro tiene el rol debugRole, se agrega en la lista de actoresLibres
    case MemberUp(member) ⇒ {
      if (member.hasRole("debugRole")) {
        //Obtengo la referencia remoto del debugActor que acaba de iniciar en algún nodo remotp
        debugActorsLibres.put(member.uniqueAddress.toString, context.actorSelection(RootActorPath(member.address) / "user" / "debugNodeActor"))
        println(s"ActoresDebug libres para ejecucion ${debugActorsLibres.size}")
      }
      println("Member is Up: {}", member.address)
    }
      //Este mensaje se recibe desde la api rest
    case wd: WorkflowDebug => {
      if (debugActorsLibres.nonEmpty) {
        val actor = debugActorsLibres.head
        actor._2 ! wd.copy(address = Some(actor._1))
        sender() ! "Trabajo en cola"
        debugActorsLibres = debugActorsLibres.tail
        debugActorsOcupados.put(actor._1, actor._2)
        println(s"ActoresDebug libres para ejecucion ${debugActorsLibres.size}")
        println(s"ActoresDebug ocupados en ejecucion ${debugActorsOcupados.size}")
      } else
        sender() ! "No hay contextos spark libres"
    }
      //Este emsanje se recibe desde el debugNode para avisar que el denugRemoto ha finaizado, y el actor esta liberado
      //para una nueva ejecucion
    case msg: String => {
      val actorSelectionLibre = debugActorsOcupados.get(msg).get
      debugActorsLibres.put(msg, actorSelectionLibre)
      debugActorsOcupados.remove(msg)
      println(s"ActoresDebug libres para ejecucion ${debugActorsLibres.size}")
      println(s"ActoresDebug ocupados en ejecucion ${debugActorsOcupados.size}")
    }


  }

}
