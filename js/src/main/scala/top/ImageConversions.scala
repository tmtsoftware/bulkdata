package top

import boopickle.Unpickle
import top.common.Image
import org.scalajs.dom._
import org.scalajs.dom.raw.Blob

import scala.scalajs.js
import scala.scalajs.js.typedarray.{Uint8Array, TypedArrayBuffer, ArrayBuffer}
import scala.scalajs.js.JSConverters._

object ImageConversions {

  def fromArrayBuffer(arrayBuffer: ArrayBuffer) = {
    val byteBuffer = TypedArrayBuffer.wrap(arrayBuffer)
    Unpickle[Image].fromBytes(byteBuffer)
  }

  def toBlob(image: Image) = {
    val uint8Array = new Uint8Array(image.bytes.toJSArray)
    val properties = js.Dynamic.literal("type" -> image.mimeType).asInstanceOf[BlobPropertyBag]
    new Blob(js.Array(uint8Array), properties)
  }
}
