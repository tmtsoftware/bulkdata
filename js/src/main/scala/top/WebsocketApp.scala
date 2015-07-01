package top

import monifu.reactive.Observable
import top.common.Config
import org.scalajs.dom._

import scala.concurrent.duration._
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport
import monifu.concurrent.Implicits.globalScheduler

import scala.scalajs.js.typedarray.ArrayBuffer

object WebsocketApp extends JSApp {

  @JSExport
  override def main() = {

    UiControls.button.onclick = { e: Event =>

      val socket = new WebSocket(s"ws://${Config.interface}:${Config.port}/images")
      socket.binaryType = "arraybuffer"

      Stream.from(socket)
        .map(_.data.asInstanceOf[ArrayBuffer])
        .map(ImageConversions.fromArrayBuffer)
        .map(RenderingData.fromImage)
//        .buffer(500).take(1).doOnStart(_ => socket.close()).concatMap(Observable.fromIterable)
        .zip(Observable.interval(24.millis)).map(_._1)
        .foreach(_.render())

    }
  }
}
