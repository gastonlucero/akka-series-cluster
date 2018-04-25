package cluster.api

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import spray.json.DefaultJsonProtocol

import scala.concurrent.duration._

object MainApi2 extends App with SprayJsonSupport with DefaultJsonProtocol {

  val config = ConfigFactory.load("cluster2")
  implicit val system: ActorSystem = ActorSystem("StratioClusterSystem", config)
  implicit val dispatcher = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val apiNodeActor = system.actorOf(Props[ApiNode], "apiNode")

  implicit val jsonWorkflow = jsonFormat6(WorkflowDebug)

  implicit val timeoutRequest = Timeout(10 second)

  lazy val route: Route =
    path("debug") {
      post {
        entity(as[WorkflowDebug]) { wkDebug =>
          apiNodeActor ! wkDebug
          complete(StatusCodes.OK)
        }
      }
    }

  val http = Http().bindAndHandle(route, "0.0.0.0", config.getInt("akka.http.port"))

}
