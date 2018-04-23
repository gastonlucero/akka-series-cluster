package actores

import akka.actor.{Actor, ActorRef, ActorSystem, PoisonPill, Props}

object MainActores extends App {

  //por defecto lee las propiedades que estan en application.conf
  val actorSystem = ActorSystem("NombreActorSystem")

  //Props es necesario para la creacion de los actores
  //nombreDelActor es el nombre unico con el que se identifica
  //Todos los actires que se crean en nuestro código están debajo de /user
  val actorRequest : ActorRef = actorSystem.actorOf(Props[ActorRequest],"nombreDelActor")

  // No puedo repetir el nombre, sino lanza un error
  // val actorRequest2 : ActorRef = actorSystem.actorOf(Props[ActorRequest],"nombreDelActor")

  actorRequest !  10 // Mando el mensaje y no espero resultado
  actorRequest !  50.12 // Puedo usar el metodo ? (ask en java) para esperar un resultado
  actorRequest !  "Que Higuain haga un gol en la final"
  actorRequest ! PoisonPill //Con esto le decimos al actor que "muera" para que se ejecute el metodo postStop
}

class ActorRequest extends Actor {

  override def preStart(): Unit =
    println(s"Se llama cuando se esta inicializando el actor y es ${self.toString()}")
    println(s"En todos los actores tenemos la variable $context para refenciar al actorSystem ${context.system} entre otras cosas")

  override def postStop(): Unit =
    println("Se llama cuando el actor es detenido")

  override def receive: Receive = {
    case msgInt: Int => println(s"Numero entero $msgInt")
    case msgDouble: Double => println(s"Numero double $msgDouble")
    case _ => println("StackOverflowExceptionNivelDios")
  }
}
