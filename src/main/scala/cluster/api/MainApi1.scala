package cluster.api

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import spray.json.DefaultJsonProtocol

import scala.concurrent.Future
import scala.concurrent.duration._

case class WorkflowDebug(userId: String, workFlowId: String, input: String, transformation: String, output: String, address: Option[String] = None)

/**
  * Levatar esta clase para que se inicia el primer seedNode
  */
object MainApi1 extends App with SprayJsonSupport with DefaultJsonProtocol {

  val config = ConfigFactory.load("cluster1")
  /* El actor system se tiene que llamar igual en todos los nodos que participen del cluster*/

  implicit val system: ActorSystem = ActorSystem("StratioClusterSystem", config)
  implicit val dispatcher = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val apiNodeActor = system.actorOf(Props[ApiNode], "apiNode")

  implicit val jsonWorkflow = jsonFormat6(WorkflowDebug)

  implicit val timeoutRequest = Timeout(10 second)

  lazy val route: Route =
    path("debug") {
      post {
        entity(as[WorkflowDebug]) { wkDebug => {
          val actorResponse: Future[String] = for {
            response <- (apiNodeActor ? wkDebug).mapTo[String]
          } yield (response)
          onSuccess(actorResponse) {
            result => complete(result)
          }
        }
        }
      }
    }


  val http = Http().bindAndHandle(route, "0.0.0.0", config.getInt("akka.http.port"))

}
