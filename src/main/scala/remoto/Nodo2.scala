package remoto

import akka.actor.{Actor, ActorSelection, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import remoto.Nodo2.actorSystem

object Nodo2 extends App {

  val actorSystem = ActorSystem("Nodo2ActorSystem", ConfigFactory.load("remote2"))

  val actorRemoto2 = actorSystem.actorOf(Props[ActorNodo2], "actorNodo2") //El nombre del actor es actorNodo2
  actorRemoto2 ! "Hola busca al actorNodo1"
}

class ActorNodo2 extends Actor {

  override def preStart(): Unit = {
    println(s"Hola soy el actor ${self.path}, address ${self.path.address}")
  }

  override def receive: Receive = {
    case msg: String =>
      println("Mensaje recibido {}, desde {}", msg, sender().path)
      //Con actorSelection podemos obtener la referencia de un actor que este en otro nodo
      val actorNodo1: ActorSelection =
        actorSystem.actorSelection("akka.tcp://Nodo1ActorSystem@0.0.0.0:2551/user/actorNodo1")
      actorNodo1 ! Ping("Hola desde el nodo 2")
    case Ping(msg) =>
      Thread.sleep(5000)
      println("Ping recibido [{}], desde [{}]", msg, sender().path)
      sender() ! Pong(s"Hola soy ${self.path}")
    case Pong(msg) =>
      Thread.sleep(5000)
      println("Pong recibido [{}], desde [{}]", msg, sender().path)
      sender() ! Ping(s"Hola soy ${self.path}")
  }
}
