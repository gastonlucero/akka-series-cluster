package remoto

import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

//Mensajes que se enviaran entre ellos
case class Ping(msg: String)
case class Pong(msg: String)

object Nodo1 extends App {

  //Aqui leemos la configuracion del archivo remote1.conf
  val actorSystem = ActorSystem("Nodo1ActorSystem", ConfigFactory.load("remote1"))

  val actorRemoto1 = actorSystem.actorOf(Props[ActorNodo1], "actorNodo1") //Nombre del actor es actorNodo1
  actorRemoto1 ! "Hola"
}

class ActorNodo1 extends Actor {

  override def preStart(): Unit = {
    println(s"Hola soy el actor ${self.path}, address ${self.path.address}")
  }

  override def receive: Receive = {
    case msg: String =>
      println(s"Mensaje recibido [$msg] , desde [${sender().path}]")
    case Ping(msg) =>
      Thread.sleep(5000)
      println(s"Ping recibido [$msg] , desde [${sender().path}]")
      sender() ! Pong(s"Hola soy ${self.path}")
    case Pong(msg) =>
      Thread.sleep(5000)
      println(s"Pong recibido [$msg] , desde [${sender().path}]")
      sender() ! Ping(s"Hola soy ${self.path}")
  }
}

