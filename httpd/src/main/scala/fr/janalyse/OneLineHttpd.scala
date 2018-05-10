package fr.janalyse

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.http.scaladsl._
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import scala.concurrent.Await
import scala.concurrent.duration.Duration



class OneLineHttpdServer(serverName:String, routes:List[Route], port:Int, interface:String) {
  val logger = org.slf4j.LoggerFactory.getLogger(getClass)

  implicit val system = ActorSystem(serverName)
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  logger.info("Http server starting")
  val all = routes.reduceLeft(_ ~ _)
  val bindingFuture = Http().bindAndHandle(all, "0.0.0.0", 8080)
  bindingFuture.map( _=> logger.info("Http server started"))

  def shutdown = {
    logger.info("Http server stopping")
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete{_ =>
         system.terminate()
         logger.info("Http server stopped")
      } // and shutdown when done
  }

  def waitForShutdown:Unit = {
    Await.result(system.whenTerminated, Duration.Inf)
  }

}

case class OneLineHttpd(routes:List[Route]=Nil) {

  def withRoute(directory:String, prefix:String=""):OneLineHttpd = {
    val route =pathPrefix(prefix) { getFromDirectory(directory)}
    this.copy(routes=routes:+route)
  }

  def withResourceRoute(directory:String, prefix:String=""):OneLineHttpd = {
    val route = pathPrefix(prefix) { getFromResourceDirectory(directory)}
    this.copy(routes=routes:+route)
  }


  def start(serverName:String="default", port:Int=8080, interface:String="0.0.0.0"):OneLineHttpdServer = {
    new OneLineHttpdServer(serverName, routes, port, interface)
  }

}


object OneLineHttpd {
  def main(args:Array[String]): Unit = {
    val server = OneLineHttpd().withRoute("httpd/example").start(port=8080)
    server.waitForShutdown
  }
}