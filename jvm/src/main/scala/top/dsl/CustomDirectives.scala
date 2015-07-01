package top.dsl

import akka.http.scaladsl.model.ws.{UpgradeToWebsocket, Message}
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.directives.HeaderDirectives._
import akka.http.scaladsl.server.directives.RouteDirectives._
import akka.stream.scaladsl.{Source, Sink, Flow}

trait CustomDirectives {

  def handleWebsocketMessages(inSink: Sink[Message, Any],
                              outSource: Source[Message, Any]): Route =
    optionalHeaderValueByType[UpgradeToWebsocket]() {
      case Some(upgrade) ⇒ complete(upgrade.handleMessagesWithSinkSource(inSink, outSource))
      case None          ⇒ reject(ExpectedWebsocketRequestRejection)
    }
}
