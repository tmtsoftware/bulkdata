package tmt.common

import java.nio.ByteBuffer

import boopickle.Default._
import org.scalajs.dom.ext.Ajax

import scala.scalajs.js.typedarray.{ArrayBuffer, TypedArrayBuffer}
import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow

object AjaxClient extends autowire.Client[ByteBuffer, Pickler, Pickler] {
  def doCall(req: AjaxClient.Request) = {
    val path = req.path.mkString("/")
    Ajax.post(
      url = s"/api/$path",
      data = Pickle.intoBytes(req.args),
      responseType = "arraybuffer",
      headers = Map("Content-Type" -> "application/octet-stream")
    ).map(request => TypedArrayBuffer.wrap(request.response.asInstanceOf[ArrayBuffer]))
  }

  def write[T: Pickler](r: T) = ???

  def read[T: Pickler](p: ByteBuffer) = ???
}
