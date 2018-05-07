#!/bin/bash
exec amm "$0" "$@"
!#

import $ivy.`com.typesafe.akka::akka-http:10.1.1`
import $ivy.`com.typesafe.akka::akka-stream:2.5.12`

import akka.http.scaladsl._
import akka.http.scaladsl.server.Directives._


implicit val system = akka.actor.ActorSystem("MySystem")
implicit val materializer = akka.stream.ActorMaterializer()
implicit val executionContext = system.dispatcher

val route1 = pathPrefix("") { getFromDirectory("example") }
val bindingFuture = Http().bindAndHandle(route1, "0.0.0.0", 8080)

def shutdown = bindingFuture.flatMap(_.unbind()).onComplete{_ => system.terminate() }


/*
 Remember that everything is done asynchronously, so don't exit...
 Following lines allow to wait for the actor system to be terminated
*/
import scala.concurrent.Await
import scala.concurrent.duration.Duration
Await.result(system.whenTerminated, Duration.Inf)

