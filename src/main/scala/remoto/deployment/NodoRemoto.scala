package remoto.deployment

import akka.actor.{Actor, ActorSystem, Kill}
import com.typesafe.config.ConfigFactory

object NodoRemoto extends App {

  //Inicia en el puerto 2551, sin actores todavia en su ActorSystem, esta
  //en un estado IDLE podriamos decir
  val actorSystem = ActorSystem("NodoRemotoActorSystem", ConfigFactory.load("remote1"))
}

class ActorDeploy extends Actor {

  override def preStart(): Unit = println(s"Soy el actor $self y mi padre es ${context.parent}")

  override def postStop(): Unit = println(s"Kill signal")

  override def receive: Receive = {
    case msg: Int =>
      if(msg > 9) {
        println(s"Detenido")
        self ! Kill
      }
      else println(s"Numero $msg")
  }
}