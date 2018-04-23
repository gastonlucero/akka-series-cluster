package remoto.deployment

import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{Actor, ActorSystem, AddressFromURIString, Deploy, Props, Terminated}
import akka.remote.RemoteScope
import com.typesafe.config.ConfigFactory
import remoto.deployment.NodoDeployment.actorSystem

object NodoDeployment extends App {

  val actorSystem = ActorSystem("NodoDeploymentActorSystem", ConfigFactory.load("remote2"))
  actorSystem.actorOf(Props[ActorDeployment])

}

class ActorDeployment extends Actor {

  //Este actor es ek encargado de crear el actor en otro nodo
  val dondeLoDeployo = AddressFromURIString("akka.tcp://NodoRemotoActorSystem@0.0.0.0:2551")
  val actorRemoto = actorSystem.actorOf(
    Props[ActorDeploy].withDeploy(Deploy(scope = RemoteScope(dondeLoDeployo))), "deployRemoto")

  implicit val dispatcher = context.system.dispatcher

  import scala.concurrent.duration._

  val nextValue = new AtomicInteger(0)

  def getValue() = nextValue.getAndIncrement()

  override def preStart(): Unit = {
    context.watch(actorRemoto) //"Vigilamos" al actor remoto, y nos enteramos de cambios de estado
    context.system.scheduler.schedule(1 seconds, 5 seconds, () => actorRemoto ! getValue())
  }


  override def receive: Receive = {
    case Terminated(m) => {
      println(s"Actor detenido $m")
    }
  }
}

//  akka {
//    actor {
//      deployment {
//        /deployRemoto {
//          remote = "akka.tcp://NodoRemotoActorSystem@0.0.0.0:2551"
//        }
//      }
//    }
//  }


