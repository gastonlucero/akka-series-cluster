package cluster.api

import akka.actor.{Actor, ActorLogging, ActorSelection, RootActorPath}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._

import scala.collection.mutable

class ApiNode extends Actor with ActorLogging {

  val cluster = Cluster(context.system)

  // subscribe to cluster changes, re-subscribe when restart
  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents, classOf[MemberEvent], classOf[UnreachableMember])
  }

  var debugActorsLibres = mutable.Map[String, ActorSelection]()
  var debugActorsOcupados = mutable.Map[String, ActorSelection]()

  override def postStop(): Unit = cluster.unsubscribe(self)

  def receive = {
    case MemberUp(member) ⇒ {
      if (member.hasRole("debugRole")) {
        debugActorsLibres.put(member.uniqueAddress.toString, context.actorSelection(RootActorPath(member.address) / "user" / "debugNodeActor"))
        println(s"ActoresDebug libres para ejecucion ${debugActorsLibres.size}")
      }
      log.info("Member is Up: {}", member.address)
    }
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
    case msg: String => {
      val actorSelectionLibre = debugActorsOcupados.get(msg).get
      debugActorsLibres.put(msg, actorSelectionLibre)
      debugActorsOcupados.remove(msg)
      println(s"ActoresDebug libres para ejecucion ${debugActorsLibres.size}")
      println(s"ActoresDebug ocupados en ejecucion ${debugActorsOcupados.size}")
    }


  }

}
